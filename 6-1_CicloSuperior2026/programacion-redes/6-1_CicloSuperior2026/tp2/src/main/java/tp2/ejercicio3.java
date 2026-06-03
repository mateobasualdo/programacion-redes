package tp2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ejercicio3 {

    private String ruta = "C:\\Users\\Redes-04\\Desktop\\";
    File file;

    public ejercicio3(String nombre) {
        file = new File(ruta.concat(nombre));
    }

    public static void main(String[] args) {
        ejercicio3 ej = new ejercicio3("números.txt");
        ej.guardarPares();
    }

    public void guardarPares() {
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file, false); 
            pw = new PrintWriter(fw);

            for (int i = 0; i <= 1000; i++) {
                if (i % 2 == 0) {      
                    pw.println(i);
                }
            }

            pw.flush();
            System.out.println("Archivo creado en: " + file.getAbsolutePath());

        } catch (IOException ex) {
            Logger.getLogger(ejercicio3.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio3.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}