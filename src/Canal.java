import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;

/** Envuelve el socket: cada mensaje viaja cifrado (Base64 de AES) con writeUTF/readUTF. */
public class Canal implements Closeable {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Logger logger;

    public Canal(Socket socket, Logger logger) throws IOException {
        this.socket = socket;
        this.logger = logger;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void enviar(String claro) throws IOException {
        try {
            String cifrado = Seguridad.cifrar(claro);
            logger.log(Logger.Tipo.CIFRADO, "TX " + resumen(cifrado));
            out.writeUTF(cifrado);
            out.flush();
        } catch (GeneralSecurityException e) {
            throw new IOException("Error al cifrar", e);
        }
    }

    /** Bloquea hasta recibir un mensaje. Lanza EOFException si el otro extremo cerro. */
    public String recibir() throws IOException {
        String cifrado = in.readUTF();
        logger.log(Logger.Tipo.CIFRADO, "RX " + resumen(cifrado));
        try {
            return Seguridad.descifrar(cifrado);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IOException("Error al descifrar", e);
        }
    }

    private static String resumen(String cifrado) {
        String corto = cifrado.length() > 44 ? cifrado.substring(0, 44) + "..." : cifrado;
        return corto + "  (" + cifrado.length() + " chars)";
    }

    public Socket socket() {
        return socket;
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        } finally {
            try {
                out.close();
            } finally {
                socket.close();
            }
        }
    }
}
