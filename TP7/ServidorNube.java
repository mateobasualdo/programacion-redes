import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Servidor de Nube.
 *
 * MANEJO DE HILOS:
 *  - El hilo principal (main) solo ejecuta un bucle accept() sobre el ServerSocket.
 *  - Cada vez que llega una conexion, se crea un ManejadorCliente (Runnable) y se
 *    lo ejecuta en un Thread nuevo. Asi accept() vuelve a quedar libre de inmediato
 *    y ningun cliente bloquea la atencion de los demas.
 *
 * Uso: java ServidorNube [puerto] [carpeta]
 */
public class ServidorNube {

    public static final int PUERTO_DEFECTO = 5000;
    public static final String CARPETA_DEFECTO = "nube";

    /** Credenciales validas (usuario -> clave). Solo lectura, por eso es seguro entre hilos. */
    static final Map<String, String> USUARIOS = Map.of(
            "admin", "1234",
            "mateo", "clave",
            "invitado", "invitado");

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : PUERTO_DEFECTO;
        File carpeta = new File(args.length > 1 ? args[1] : CARPETA_DEFECTO);

        // Carpeta local donde se alojan los archivos transferidos.
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            System.err.println("No se pudo crear la carpeta " + carpeta.getAbsolutePath());
            return;
        }

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            Log.info("Servidor escuchando en el puerto " + puerto
                    + " | carpeta: " + carpeta.getAbsolutePath());
            int contador = 0;
            while (true) {
                Socket socket = servidor.accept(); // bloquea solo al hilo main
                contador++;
                Thread hilo = new Thread(new ManejadorCliente(socket, carpeta), "cliente-" + contador);
                hilo.start(); // la sesion corre en su propio hilo
            }
        }
    }
}
