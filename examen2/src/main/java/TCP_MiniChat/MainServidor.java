package TCP_MiniChat;

import java.io.IOException;

//TU-CODIGO-UNICO: 64285A9D

public class MainServidor {

    public static final int PUERTO = 7777;

    public static void main(String[] args) {

        System.out.println(
                Colores.CIAN
                + "====================================\n"
                + "       MINI CHAT - SERVIDOR LOCAL\n"
                + "===================================="
                + Colores.RESET
        );

        try (ServidorLocal servidor = new ServidorLocal(PUERTO)) {

            Runtime.getRuntime().addShutdownHook(
                    new Thread(servidor::close, "CIERRE-SERVIDOR")
            );

            servidor.iniciar();

        } catch (IOException e) {
            System.err.println(
                    Colores.ROJO
                    + "No se pudo iniciar el servidor: " + e.getMessage()
                    + Colores.RESET
            );
        }
    }
}
