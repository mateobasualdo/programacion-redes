package clienteUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClienteUDP {

    public static void main(String[] args) {
        final int PUERTO_SERVIDOR = 9876;
        DatagramSocket socket = null;
        Scanner teclado = new Scanner(System.in);

        try {
            socket = new DatagramSocket();
            InetAddress direccion = InetAddress.getByName("localhost");

            boolean continuar = true;
            while (continuar) {
                System.out.print("Ingrese un mensaje (FIN para terminar): ");
                String texto = teclado.nextLine();

                byte[] datos = texto.getBytes();
                DatagramPacket paquete = new DatagramPacket(datos, datos.length, direccion, PUERTO_SERVIDOR);
                socket.send(paquete);

                if (texto.equals("FIN")) {
                    System.out.println("Se notificó FIN al servidor. Cerrando cliente.");
                    continuar = false;
                }
            }
        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
            teclado.close();
        }
    }
}
