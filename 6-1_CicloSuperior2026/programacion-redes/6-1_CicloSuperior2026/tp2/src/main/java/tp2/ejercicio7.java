package tp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ejercicio7 {

    private String ruta = "C:\\Users\\Redes-04\\Desktop\\";
    File file;

    public ejercicio7(String nombre) {
        file = new File(ruta.concat(nombre));
    }

    public static void main(String[] args) {
        ejercicio7 ej = new ejercicio7("caracteres.dat");

        ej.cargarPalabras();

        System.out.println("\nFichero original:");
        ej.mostrarArchivo();

        ej.reemplazarNie();

        System.out.println("\nFichero arreglado:");
        ej.mostrarArchivo();
    }

    public void cargarPalabras() {
        Scanner sc = new Scanner(System.in);
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file, false); 
            pw = new PrintWriter(fw);

            System.out.println("Ingresá 10 palabras que contengan la letra 'ñ':");

            int contador = 0;
            while (contador < 10) {
                System.out.print("Palabra " + (contador + 1) + ": ");
                String palabra = sc.nextLine();

                if (palabra.contains("ñ") || palabra.contains("Ñ")) {
                    pw.println(palabra);
                    contador++;
                } else {
                    System.out.println("La palabra no contiene 'ñ', ingresá otra.");
                }
            }

            pw.flush();

        } catch (IOException ex) {
            Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        sc.close();
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
            Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public void reemplazarNie() {
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        PrintWriter pw = null;

        ArrayList<String> lineasModificadas = new ArrayList<String>();

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.replace("ñ", "nie-nio");
                linea = linea.replace("Ñ", "Nie-nio");
                lineasModificadas.add(linea);
            }

        } catch (IOException ex) {
            Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
            }
        }

      
        try {
            fw = new FileWriter(file, false);
            pw = new PrintWriter(fw);

            for (String linea : lineasModificadas) {
                pw.println(linea);
            }

            pw.flush();

        } catch (IOException ex) {
            Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio7.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}