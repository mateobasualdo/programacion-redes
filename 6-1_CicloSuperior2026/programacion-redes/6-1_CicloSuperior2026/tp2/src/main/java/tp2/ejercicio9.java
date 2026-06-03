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

public class ejercicio9 {

    private String ruta = "C:\\Users\\Redes-04\\Desktop\\";
    File file;
    Scanner sc = new Scanner(System.in);

    public ejercicio9(String nombre) {
        file = new File(ruta.concat(nombre));
    }

    public static void main(String[] args) {
        ejercicio9 ej = new ejercicio9("clima.txt");

        int opcion = 0;

        while (opcion != 4) {
            System.out.println("\n===== MENU CLIMA =====");
            System.out.println("1. Cargar dato de clima");
            System.out.println("2. Mostrar todos los datos");
            System.out.println("3. Borrar registro por fecha");
            System.out.println("4. Salir");
            System.out.print("Elegí una opción: ");

            try {
                opcion = Integer.parseInt(ej.sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida, ingresá un número.");
                continue;
            }

            switch (opcion) {
                case 1:
                    ej.cargarDato();
                    break;
                case 2:
                    ej.mostrarDatos();
                    break;
                case 3:
                    ej.borrarRegistro();
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }

        ej.sc.close();
    }

  
    public void cargarDato() {
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            System.out.print("Fecha (ej: 03/06/2026): ");
            String fecha = sc.nextLine().trim();

            System.out.print("Temperatura (°C): ");
            String temp = sc.nextLine().trim();

            System.out.print("Humedad (%): ");
            String humedad = sc.nextLine().trim();

            System.out.print("Descripción (ej: soleado, nublado): ");
            String descripcion = sc.nextLine().trim();

        
            String registro = fecha + "|" + temp + "|" + humedad + "|" + descripcion;

            fw = new FileWriter(file, true); 
            pw = new PrintWriter(fw);
            pw.println(registro);
            pw.flush();

            System.out.println("Dato guardado correctamente.");

        } catch (IOException ex) {
            Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public void mostrarDatos() {
        FileReader fr = null;
        BufferedReader br = null;

        try {
            if (!file.exists() || file.length() == 0) {
                System.out.println("No hay datos cargados.");
                return;
            }

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            System.out.println("\n--- Registros de clima ---");

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|"); 
                System.out.println("Fecha: "       + partes[0]);
                System.out.println("Temperatura: " + partes[1] );
                System.out.println("Humedad: "     + partes[2] );
                System.out.println("Descripción: " + partes[3]);
                System.out.println("-------------------------");
            }

        } catch (IOException ex) {
            Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public void borrarRegistro() {
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        PrintWriter pw = null;

        ArrayList<String> lineasFiltradas = new ArrayList<String>();
        boolean encontrado = false;

        System.out.print("Ingresá la fecha del registro a borrar (ej: 03/06/2026): ");
        String fechaBuscar = sc.nextLine().trim();

        try {
            if (!file.exists() || file.length() == 0) {
                System.out.println("No hay datos cargados.");
                return;
            }

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes[0].equals(fechaBuscar)) {
                    encontrado = true; 
                } else {
                    lineasFiltradas.add(linea);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        if (!encontrado) {
            System.out.println("No se encontró un registro con esa fecha.");
            return;
        }

  
        try {
            fw = new FileWriter(file, false);
            pw = new PrintWriter(fw);

            for (String linea : lineasFiltradas) {
                pw.println(linea);
            }

            pw.flush();
            System.out.println("Registro borrado correctamente.");

        } catch (IOException ex) {
            Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio9.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}