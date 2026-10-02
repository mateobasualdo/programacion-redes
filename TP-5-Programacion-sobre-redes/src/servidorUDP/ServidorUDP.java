package servidorUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServidorUDP {

    public static void main(String[] args) {
        final int PUERTO = 9876;
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(PUERTO);
            System.out.println("Servidor UDP escuchando en el puerto " + PUERTO + "...");

            byte[] buffer = new byte[1024];
            boolean continuar = true;

            while (continuar) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                String ip = paquete.getAddress().getHostAddress();
                int puerto = paquete.getPort();

                System.out.println("Mensaje: " + mensaje + " | Origen: " + ip + ":" + puerto);

                if (mensaje.equals("FIN")) {
                    System.out.println("El cliente envió FIN. Cerrando servidor.");
                    continuar = false;
                }
            }
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
