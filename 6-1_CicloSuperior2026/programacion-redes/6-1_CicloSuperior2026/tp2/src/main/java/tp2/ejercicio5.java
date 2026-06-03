package tp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ejercicio5 {

    private String ruta = "C:\\Users\\Redes-04\\Desktop\\";
    File file;

    public ejercicio5(String nombre) {
        file = new File(ruta.concat(nombre));
    }

    public static void main(String[] args) {
        ejercicio5 ej = new ejercicio5("números.txt");
        ej.borrarMultiplosDe3();
    }

    public void borrarMultiplosDe3() {
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        PrintWriter pw = null;

        ArrayList<String> lineasFiltradas = new ArrayList<String>();
     
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                int numero = Integer.parseInt(linea.trim());
                if (numero % 3 != 0) {        
                    lineasFiltradas.add(linea);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ejercicio5.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio5.class.getName()).log(Level.WARNING, null, ex);
            }
        }

       
        try {
            fw = new FileWriter(file, false);
            pw = new PrintWriter(fw);

            for (String linea : lineasFiltradas) {
                pw.println(linea);
            }

            pw.flush();
            System.out.println("Archivo actualizado. Líneas restantes: " + lineasFiltradas.size());

        } catch (IOException ex) {
            Logger.getLogger(ejercicio5.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio5.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}