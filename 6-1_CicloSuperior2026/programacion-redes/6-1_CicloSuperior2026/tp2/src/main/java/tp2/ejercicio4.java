package tp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ejercicio4 {

   
    private String ruta = "C:\\Users\\Redes-04\\Desktop\\";
    File file;

    public ejercicio4(String nombre) {
        file = new File(ruta.concat(nombre));
    }

    public static void main(String[] args) {
        ejercicio4 ej = new ejercicio4("números.txt");
        ej.leerArchivo();
    }

    public void leerArchivo() {
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(file);   
            br = new BufferedReader(fr); 

            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }

        } catch (IOException ex) {
            Logger.getLogger(ejercicio4.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio4.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}