package TCP_MiniChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServidorLocal implements AutoCloseable {

    public static final String BIENVENIDA = "HOLA BIENVENIDO AL SERVIDOR LOCAL";

    private final ServerSocket server;
    private volatile boolean ejecutando = true;

    private final List<ConexionCliente> clientes = new CopyOnWriteArrayList<>();

    public ServidorLocal(int puerto) throws IOException {
        server = new ServerSocket(puerto);
    }

    public void iniciar() {
        System.out.println(
                Colores.VERDE
                + "Servidor local escuchando en el puerto "
                + server.getLocalPort()
                + Colores.RESET
        );

        while (ejecutando) {
            Socket socket;

            try {
                socket = server.accept();
            } catch (IOException e) {
                if (ejecutando) {
                    System.err.println(
                            Colores.ROJO + "[ERROR] " + e.getMessage()
                            + Colores.RESET
                    );
                }
                break;
            }

            try {
                ConexionCliente cliente = new ConexionCliente(socket, this);
                new Thread(cliente, "CLIENTE-" + socket.getPort()).start();

            } catch (IOException e) {
                System.err.println(
                        Colores.ROJO + "[ERROR] No se pudo atender a un cliente: "
                        + e.getMessage() + Colores.RESET
                );
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void registrar(ConexionCliente cliente) {
        clientes.add(cliente);

        System.out.println(
                Colores.VERDE
                + "[CONECTADO] " + cliente.getDireccion()
                + " (" + clientes.size() + " en linea)"
                + Colores.RESET
        );
    }

    public void quitar(ConexionCliente cliente) {
        if (clientes.remove(cliente)) {
            System.out.println(
                    Colores.AMARILLO
                    + "[DESCONECTADO] " + cliente.getDireccion()
                    + " (" + clientes.size() + " en linea)"
                    + Colores.RESET
            );
        }
    }

    public void difundir(ConexionCliente emisor, String mensaje) {
        System.out.println(
                Colores.CIAN
                + "[CHAT] " + emisor.getDireccion() + ": " + mensaje
                + Colores.RESET
        );

        for (ConexionCliente cliente : clientes) {
            cliente.enviar(mensaje);
        }
    }

    @Override
    public void close() {
        if (!ejecutando) {
            return;
        }
        ejecutando = false;

        try {
            server.close();
        } catch (IOException e) {
            System.err.println("Error cerrando el servidor.");
        }

        for (ConexionCliente cliente : clientes) {
            cliente.cerrar();
        }
        clientes.clear();

        System.out.println(Colores.ROJO + "Servidor detenido." + Colores.RESET);
    }
}
