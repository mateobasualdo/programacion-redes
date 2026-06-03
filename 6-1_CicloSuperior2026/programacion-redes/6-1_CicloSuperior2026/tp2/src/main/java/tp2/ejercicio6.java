package tp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ejercicio6 {

    private String rutaOrigen  = "C:\\Users\\Redes-04\\Desktop\\";
    private String rutaDestino = "C:\\Users\\Redes-04\\Documents\\";

    File fileOrigen;
    File fileDestino;

    public ejercicio6() {
        fileOrigen  = new File(rutaOrigen.concat("números.txt"));
        fileDestino = new File(rutaDestino.concat("primos.dat"));
    }

    public static void main(String[] args) {
        ejercicio6 ej = new ejercicio6();
        ej.guardarPrimos();
    }

    public boolean esPrimo(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public void guardarPrimos() {
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            if (!fileDestino.exists()) {
                fileDestino.createNewFile();
            }

            fr = new FileReader(fileOrigen);
            br = new BufferedReader(fr);
            fw = new FileWriter(fileDestino, false);
            pw = new PrintWriter(fw);

            String linea;
            int contador = 0;

            while ((linea = br.readLine()) != null) {
                int numero = Integer.parseInt(linea.trim());
                if (esPrimo(numero)) {
                    pw.println(numero);
                    contador++;
                }
            }

            pw.flush();
            System.out.println("Primos guardados: " + contador);
            System.out.println("Archivo en: " + fileDestino.getAbsolutePath());

        } catch (IOException ex) {
            Logger.getLogger(ejercicio6.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio6.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}