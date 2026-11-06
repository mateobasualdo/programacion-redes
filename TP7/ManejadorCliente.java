import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * Atiende UNA sesion de cliente en su propio hilo.
 *
 * PROTOCOLO (DataInputStream / DataOutputStream sobre el socket):
 *  - Comandos: el cliente envia una linea con writeUTF ("LOGIN u c", "LIST", ...).
 *  - Respuestas: el servidor responde con writeUTF ("OK ..." o "ERROR ...").
 *  - Datos binarios (archivos): primero un long con el tamano y luego exactamente
 *    ese numero de bytes. Al conocer el tamano no hace falta cerrar el socket para
 *    marcar el fin del archivo, y el mismo socket sigue sirviendo para mas comandos.
 *
 * Cada instancia tiene su propio estado (autenticado, usuario) y sus propios streams,
 * por lo que las sesiones no se interfieren entre si.
 */
public class ManejadorCliente implements Runnable {

    private static final int BUFFER = 8192;

    private final Socket socket;
    private final File carpeta;
    private final String ip;

    /** Estado de la sesion: solo lo toca este hilo, no necesita sincronizacion. */
    private boolean autenticado = false;
    private String usuario = null;

    public ManejadorCliente(Socket socket, File carpeta) {
        this.socket = socket;
        this.carpeta = carpeta;
        this.ip = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        Log.info("Conexion entrante desde " + ip);
        // try-with-resources: cierra streams y socket pase lo que pase.
        try (Socket s = socket;
             DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()))) {

            while (true) {
                String linea = in.readUTF(); // bloquea solo a este hilo
                String[] p = linea.trim().split("\\s+", 3);
                String cmd = p[0].toUpperCase();
                Log.info(ip + " -> " + (cmd.equals("LOGIN") ? "LOGIN " + (p.length > 1 ? p[1] : "") + " ****" : linea));

                if (cmd.equals("EXIT")) {
                    responder(out, "OK Hasta luego");
                    break;
                }
                // Sin autenticacion solo se permite LOGIN.
                if (!cmd.equals("LOGIN") && !autenticado) {
                    responder(out, "ERROR Debe autenticarse primero con LOGIN <usuario> <clave>");
                    continue;
                }
                switch (cmd) {
                    case "LOGIN"    -> login(p, out);
                    case "LIST"     -> listar(out);
                    case "UPLOAD"   -> subir(p, in, out);
                    case "DOWNLOAD" -> descargar(p, out);
                    default         -> responder(out, "ERROR Comando desconocido: " + cmd);
                }
            }
        } catch (EOFException e) {
            Log.info(ip + " cerro la conexion sin EXIT");
        } catch (IOException e) {
            Log.info("Error con " + ip + ": " + e.getMessage());
        }
        Log.info("Sesion finalizada: " + ip + (usuario != null ? " (" + usuario + ")" : ""));
    }

    private void responder(DataOutputStream out, String msg) throws IOException {
        out.writeUTF(msg);
        out.flush(); // los streams tienen buffer: sin flush el cliente no recibe nada
    }

    private void login(String[] p, DataOutputStream out) throws IOException {
        if (p.length < 3) {
            responder(out, "ERROR Uso: LOGIN <usuario> <clave>");
            return;
        }
        String clave = ServidorNube.USUARIOS.get(p[1]);
        if (clave != null && clave.equals(p[2])) {
            autenticado = true;
            usuario = p[1];
            responder(out, "OK Bienvenido " + usuario);
        } else {
            responder(out, "ERROR Credenciales invalidas");
        }
    }

    private void listar(DataOutputStream out) throws IOException {
        File[] archivos = carpeta.listFiles(File::isFile);
        if (archivos == null) archivos = new File[0];
        Arrays.sort(archivos);
        out.writeUTF("OK " + archivos.length);       // cabecera con la cantidad
        for (File f : archivos) {
            out.writeUTF(f.getName());
            out.writeLong(f.length());               // tamano en bytes
        }
        out.flush();
    }

    /** Recibe un archivo: OK -> long tamano -> bytes. */
    private void subir(String[] p, DataInputStream in, DataOutputStream out) throws IOException {
        File destino = resolver(p);
        if (destino == null) {
            responder(out, "ERROR Nombre de archivo invalido");
            return;
        }
        responder(out, "OK Listo para recibir");
        long tamano = in.readLong();
        // Se escribe en un temporal y se renombra al terminar: otros hilos que hagan
        // LIST/DOWNLOAD nunca ven un archivo a medio subir.
        File tmp = File.createTempFile("subida-", ".tmp", carpeta);
        try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(tmp))) {
            byte[] buf = new byte[BUFFER];
            long restante = tamano;
            while (restante > 0) {
                int n = in.read(buf, 0, (int) Math.min(buf.length, restante));
                if (n == -1) throw new EOFException("Conexion cortada durante la subida");
                fos.write(buf, 0, n);
                restante -= n;
            }
        } catch (IOException e) {
            tmp.delete();
            throw e;
        }
        Files.move(tmp.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Log.info(ip + " subio '" + destino.getName() + "' (" + tamano + " bytes)");
        responder(out, "OK Archivo recibido (" + tamano + " bytes)");
    }

    /** Envia un archivo: OK -> long tamano -> bytes. */
    private void descargar(String[] p, DataOutputStream out) throws IOException {
        File origen = resolver(p);
        if (origen == null || !origen.isFile()) {
            responder(out, "ERROR El archivo no existe");
            return;
        }
        long tamano = origen.length();
        out.writeUTF("OK " + tamano);
        out.writeLong(tamano);
        try (InputStream fis = new BufferedInputStream(new FileInputStream(origen))) {
            byte[] buf = new byte[BUFFER];
            int n;
            while ((n = fis.read(buf)) != -1) out.write(buf, 0, n);
        }
        out.flush();
        Log.info(ip + " descargo '" + origen.getName() + "' (" + tamano + " bytes)");
    }

    /** Devuelve el archivo dentro de la carpeta o null si el nombre es invalido (evita "../"). */
    private File resolver(String[] p) {
        if (p.length < 2) return null;
        String nombre = p.length > 2 ? p[1] + " " + p[2] : p[1]; // permite espacios
        if (!nombre.equals(new File(nombre).getName())) return null;
        return new File(carpeta, nombre);
    }
}
