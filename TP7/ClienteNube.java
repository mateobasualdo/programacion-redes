import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente de Nube con consola interactiva.
 *
 * HILOS: una sola sesion secuencial (comando -> respuesta), por lo que alcanza con
 * el hilo main; las lecturas del socket bloquean hasta que el servidor responde.
 *
 * STREAMS: DataInputStream/DataOutputStream envueltos en Buffered*Stream.
 *  - writeUTF/readUTF para comandos y respuestas de texto.
 *  - writeLong/readLong + bytes crudos para los archivos.
 *  - flush() tras cada envio, porque el buffer retiene los datos hasta llenarse.
 *
 * Uso: java ClienteNube [ip] [puerto]
 * Los archivos descargados se guardan en la carpeta "descargas".
 */
public class ClienteNube {

    private static final int BUFFER = 8192;

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        String ip;
        if (args.length > 0) {
            ip = args[0];
        } else {
            System.out.print("IP del servidor: ");
            ip = teclado.nextLine().trim();
        }
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : ServidorNube.PUERTO_DEFECTO;

        File descargas = new File("descargas");
        descargas.mkdirs();

        try (Socket socket = new Socket(ip, puerto);
             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {

            System.out.println("Conectado a " + ip + ":" + puerto);
            System.out.println("Comandos: LOGIN <usuario> <clave> | LIST | UPLOAD <archivo> | DOWNLOAD <archivo> | EXIT");

            while (true) {
                System.out.print("nube> ");
                if (!teclado.hasNextLine()) break;
                String linea = teclado.nextLine().trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\s+", 2);
                String cmd = p[0].toUpperCase();

                switch (cmd) {
                    case "LIST" -> {
                        enviar(out, "LIST");
                        listar(in);
                    }
                    case "UPLOAD" -> {
                        if (p.length < 2) { System.out.println("Uso: UPLOAD <archivo>"); continue; }
                        File f = new File(p[1]);
                        if (!f.isFile()) { System.out.println("No existe el archivo local: " + p[1]); continue; }
                        subir(f, in, out);
                    }
                    case "DOWNLOAD" -> {
                        if (p.length < 2) { System.out.println("Uso: DOWNLOAD <archivo>"); continue; }
                        enviar(out, "DOWNLOAD " + p[1]);
                        descargar(p[1], descargas, in);
                    }
                    case "EXIT" -> {
                        enviar(out, "EXIT");
                        System.out.println(in.readUTF());
                        return; // try-with-resources cierra streams y socket
                    }
                    default -> { // LOGIN y cualquier otro: el servidor decide
                        enviar(out, linea);
                        System.out.println(in.readUTF());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
    }

    private static void enviar(DataOutputStream out, String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    private static void listar(DataInputStream in) throws IOException {
        String resp = in.readUTF();
        if (!resp.startsWith("OK")) { System.out.println(resp); return; }
        int n = Integer.parseInt(resp.substring(3).trim());
        System.out.println("Archivos en la nube (" + n + "):");
        for (int i = 0; i < n; i++) {
            String nombre = in.readUTF();
            long tam = in.readLong();
            System.out.printf("  %-30s %12d bytes%n", nombre, tam);
        }
    }

    private static void subir(File f, DataInputStream in, DataOutputStream out) throws IOException {
        enviar(out, "UPLOAD " + f.getName());
        String resp = in.readUTF();
        if (!resp.startsWith("OK")) { System.out.println(resp); return; }
        out.writeLong(f.length());
        try (InputStream fis = new BufferedInputStream(new FileInputStream(f))) {
            byte[] buf = new byte[BUFFER];
            int n;
            while ((n = fis.read(buf)) != -1) out.write(buf, 0, n);
        }
        out.flush();
        System.out.println(in.readUTF());
    }

    private static void descargar(String nombre, File carpeta, DataInputStream in) throws IOException {
        String resp = in.readUTF();
        if (!resp.startsWith("OK")) { System.out.println(resp); return; }
        long tamano = in.readLong();
        File destino = new File(carpeta, nombre);
        try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(destino))) {
            byte[] buf = new byte[BUFFER];
            long restante = tamano;
            while (restante > 0) { // se lee exactamente 'tamano' bytes
                int n = in.read(buf, 0, (int) Math.min(buf.length, restante));
                if (n == -1) throw new EOFException("Conexion cortada durante la descarga");
                fos.write(buf, 0, n);
                restante -= n;
            }
        }
        System.out.println("Descargado en " + destino.getPath() + " (" + tamano + " bytes)");
    }
}
