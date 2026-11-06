import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Cliente TCP por consola.
 * Uso: java Cliente [host] [puerto] ["Nombre Apellido"]
 */
public class Cliente {

    // CAMBIAR por el nombre y apellido real del alumno (o pasarlo como 3er argumento)
    private static final String NOMBRE_ALUMNO = "Mateo Basualdo";

    private static final Logger LOG = new Logger("CLIENTE");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final List<String> historial = new ArrayList<>();

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        String alumno = args.length > 2 ? args[2] : NOMBRE_ALUMNO;

        LOG.separador("CLIENTE E-COMMERCE  |  " + alumno + "  |  " + host + ":" + puerto);
        Scanner teclado = new Scanner(System.in);
        try (Socket socket = new Socket(host, puerto); Canal canal = new Canal(socket, LOG)) {
            LOG.log(Logger.Tipo.OK, "Conexion exitosa con el servidor " + socket.getRemoteSocketAddress());

            // Saludo inicial con el nombre del alumno (visible en ambas consolas)
            intercambiar(canal, "MSG|Hola, mi nombre es " + alumno, "Hola, mi nombre es " + alumno);
            LOG.log(Logger.Tipo.INFO, "Comandos: autenticar | ayuda | ver stock <producto> | "
                    + "subir comprobante <ruta> | ver historial de chat | salir");

            while (true) {
                LOG.prompt("\n" + alumno + " > ");
                if (!teclado.hasNextLine()) break;
                String linea = teclado.nextLine().trim();
                if (linea.isEmpty()) continue;
                String cmd = linea.toLowerCase(Locale.ROOT);

                if (cmd.equals("salir")) {
                    intercambiar(canal, "BYE", "salir");
                    break;
                } else if (cmd.equals("ayuda")) {
                    intercambiar(canal, "HELP", linea);
                } else if (cmd.equals("autenticar")) {
                    LOG.prompt("  Usuario : ");
                    String u = teclado.nextLine().trim();
                    LOG.prompt("  Clave   : ");
                    String c = teclado.nextLine().trim();
                    intercambiar(canal, "AUTH|" + u + "|" + c, "autenticar (usuario " + u + ")");
                } else if (cmd.startsWith("ver stock")) {
                    String prod = linea.substring("ver stock".length()).trim();
                    if (prod.isEmpty()) {
                        LOG.prompt("  Producto: ");
                        prod = teclado.nextLine().trim();
                    }
                    intercambiar(canal, "STOCK|" + prod, "ver stock " + prod);
                } else if (cmd.startsWith("subir comprobante")) {
                    String ruta = linea.substring("subir comprobante".length()).trim();
                    if (ruta.isEmpty()) {
                        LOG.prompt("  Ruta del archivo: ");
                        ruta = teclado.nextLine().trim();
                    }
                    subirComprobante(canal, ruta);
                } else if (cmd.equals("ver historial de chat")) {
                    mostrarHistorial();
                } else {
                    intercambiar(canal, "MSG|" + linea, linea);
                }
            }
            LOG.log(Logger.Tipo.AVISO, "Sesion finalizada por el usuario.");
        } catch (EOFException | SocketException e) {
            LOG.log(Logger.Tipo.ERROR, "Desconexion del servidor: " + e.getMessage());
        } catch (IOException e) {
            LOG.log(Logger.Tipo.ERROR, "Error de comunicacion: " + e.getMessage());
        } finally {
            LOG.log(Logger.Tipo.INFO, "Streams y socket cerrados. Recursos de red liberados.");
        }
    }

    private static void subirComprobante(Canal canal, String ruta) throws IOException {
        Path archivo = Paths.get(ruta);
        if (!Files.isRegularFile(archivo)) {
            LOG.log(Logger.Tipo.ERROR, "El archivo '" + ruta + "' no existe.");
            return;
        }
        byte[] datos = Files.readAllBytes(archivo);
        String b64 = Base64.getEncoder().encodeToString(datos);
        String nombre = archivo.getFileName().toString();
        intercambiar(canal, "UPLOAD|" + nombre + "|" + b64,
                "subir comprobante " + nombre + " (" + datos.length + " bytes)");
    }

    /** Envia un mensaje, espera la respuesta (modo secuencial) y registra ambos en pantalla e historial. */
    private static void intercambiar(Canal canal, String protocolo, String descripcion) throws IOException {
        LOG.log(Logger.Tipo.ENVIADO, descripcion);
        historial.add("[" + LocalTime.now().format(HORA) + "] Yo       : " + descripcion);
        canal.enviar(protocolo);

        String resp = canal.recibir();
        int i = resp.indexOf('|');
        String tipo = i < 0 ? "OK" : resp.substring(0, i);
        String texto = i < 0 ? resp : resp.substring(i + 1);
        Logger.Tipo t = tipo.equals("ERR") ? Logger.Tipo.ERROR : tipo.equals("BOT") ? Logger.Tipo.CHAT : Logger.Tipo.RECIBIDO;
        LOG.log(t, "Servidor: " + texto);
        historial.add("[" + LocalTime.now().format(HORA) + "] Servidor : " + texto.replace("\n", " "));
    }

    private static void mostrarHistorial() {
        LOG.log(Logger.Tipo.INFO, "Historial de chat de la sesion (" + historial.size() + " mensajes):");
        for (String h : historial) {
            LOG.log(Logger.Tipo.INFO, "\t" + h);
        }
    }
}
