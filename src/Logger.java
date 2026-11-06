import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Log con tabulaciones estructuradas y colores ANSI. Formato: HORA | ORIGEN | TIPO | mensaje. */
public final class Logger {

    public enum Tipo {
        OK("\u001B[1;32m", "OK"),
        RECIBIDO("\u001B[1;36m", "RECIBIDO"),
        ENVIADO("\u001B[1;34m", "ENVIADO"),
        CHAT("\u001B[1;35m", "CHAT"),
        CIFRADO("\u001B[2;37m", "CIFRADO"),
        INFO("\u001B[0;37m", "INFO"),
        AVISO("\u001B[1;33m", "AVISO"),
        ERROR("\u001B[1;31m", "ERROR");

        final String color;
        final String etiqueta;

        Tipo(String color, String etiqueta) {
            this.color = color;
            this.etiqueta = etiqueta;
        }
    }

    private static final String RESET = "\u001B[0m";
    private static final String GRIS = "\u001B[90m";
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private final String origen;

    public Logger(String origen) {
        this.origen = origen;
    }

    public void log(Tipo tipo, String mensaje) {
        String cabecera = String.format("%s%s%s\t| %-9s\t| %s%-8s%s\t| ",
                GRIS, LocalTime.now().format(HORA), RESET, origen, tipo.color, tipo.etiqueta, RESET);
        String sangria = String.format("%s%-12s\t| %-9s\t| %-8s\t| %s", GRIS, "", "", "", RESET);
        String[] lineas = mensaje.split("\\R", -1);
        for (int i = 0; i < lineas.length; i++) {
            OUT.println((i == 0 ? cabecera : sangria) + tipo.color + lineas[i] + RESET);
        }
    }

    public void separador(String titulo) {
        String linea = "=".repeat(78);
        OUT.println("\u001B[1;33m" + linea + RESET);
        OUT.println("\u001B[1;33m  " + titulo + RESET);
        OUT.println("\u001B[1;33m" + linea + RESET);
    }

    /** Linea de entrada del usuario (prompt) sin salto de linea. */
    public void prompt(String texto) {
        OUT.print("\u001B[1;37m" + texto + RESET);
        OUT.flush();
    }
}
