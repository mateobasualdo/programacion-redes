package tp0;

import java.io.*;

public class Utils {

    private static BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
    private static PrintWriter salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);

    public static String leer() throws IOException {
        return entrada.readLine();
    }

    public static void mostrar(String texto) {
        salida.println(texto);
    }
}