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

public class ejercicio8 {

    private String ruta = "C:\\Users\\Redes-04\\Desktop\\";
    File file;

    public ejercicio8(String nombre) {
        file = new File(ruta.concat(nombre));
    }

    public static void main(String[] args) {
        ejercicio8 ej = new ejercicio8("index.html");

        System.out.println("Archivo original:");
        ej.mostrarArchivo();

        ej.borrarLorem();

        System.out.println("\nArchivo sin Lorem:");
        ej.mostrarArchivo();
    }

    public void borrarLorem() {
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
                if (!linea.toLowerCase().contains("lorem")) {
                    lineasFiltradas.add(linea);
                } else {
                    System.out.println("Línea eliminada: " + linea.trim());
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ejercicio8.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio8.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        try {
            fw = new FileWriter(file, false);
            pw = new PrintWriter(fw);

            for (String linea : lineasFiltradas) {
                pw.println(linea);
            }

            pw.flush();
            System.out.println("\nArchivo actualizado correctamente.");

        } catch (IOException ex) {
            Logger.getLogger(ejercicio8.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio8.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public void mostrarArchivo() {
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
            Logger.getLogger(ejercicio8.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio8.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}