import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Log de consola del servidor. Incluye el nombre del hilo para evidenciar la concurrencia. */
public final class Log {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Log() {}

    /** synchronized: evita que lineas de hilos distintos se mezclen en la consola. */
    public static synchronized void info(String msg) {
        System.out.println("[" + LocalTime.now().format(FMT) + "] ["
                + Thread.currentThread().getName() + "] " + msg);
    }
}
