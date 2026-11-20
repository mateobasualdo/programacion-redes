package TCP_MiniChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConexionCliente implements Runnable {
	
	
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    private final ServidorLocal servidor;
    private final String direccion;
    private final AtomicBoolean conectado = new AtomicBoolean(true);

    public ConexionCliente(Socket socket, ServidorLocal servidor) throws IOException {
        this.socket = socket;
        this.servidor = servidor;
        this.direccion = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

        this.entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        this.salida = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                true
        );
    }

    @Override
    public void run() {
        try {
            enviar(ServidorLocal.BIENVENIDA);
            servidor.registrar(this);

            String linea;
            while (conectado.get() && (linea = entrada.readLine()) != null) {
                linea = linea.trim();

                if (linea.isEmpty()) {
                    continue;
                }

                if (linea.startsWith("/")) {
                    if (!procesarComando(linea)) {
                        break;
                    }
                } else {
                    servidor.difundir(this, linea);
                }
            }
        } catch (IOException e) {
        } finally {
            cerrar();
        }
    }

    private boolean procesarComando(String comando) {
        switch (comando.toLowerCase(Locale.ROOT)) {
            case "/hi":
                enviar(ServidorLocal.BIENVENIDA);
                return true;

            case "/logout":
                return false;

            default:
                enviar("COMANDO DESCONOCIDO");
                return true;
        }
    }

    public synchronized boolean enviar(String mensaje) {
        if (!conectado.get()) {
            return false;
        }

        salida.println(mensaje);

        if (salida.checkError()) {
            cerrar();
            return false;
        }
        return true;
    }

    public void cerrar() {
        if (!conectado.compareAndSet(true, false)) {
            return;
        }

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando el socket de " + direccion);
        }

        servidor.quitar(this);
    }

    public String getDireccion() {
        return direccion;
    }
}
