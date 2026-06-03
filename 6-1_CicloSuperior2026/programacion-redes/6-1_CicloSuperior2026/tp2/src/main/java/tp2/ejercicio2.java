package tp2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ejercicio2 {
	
	private String ruta = "";
	private File file;
	private PrintStream ps;
	
	public ejercicio2(String nombre) {
		file = new File(ruta.concat(nombre));
		ps = new PrintStream(System.out);
		
	}
	
	public static void main(String[] args) {
		ejercicio2 ej = new ejercicio2("ejercicio2.txt");
		
		String dato;
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Ingresa un dato ('salir' para terminar): ");
		
		while (true) {
			System.out.print("Dato: ");
			dato = sc.nextLine();
			
			if (dato.equals("salir")) {
				break;
			}
			
			if(esNumero(dato)) {
				ej.guardarDato(dato);
				System.out.println("Dato guardado");
			} else {
				System.out.println("el dato ingresado no es un numero");
			}
		}
		
		sc.close();
		System.out.println("Terminado");
	}
	
	public static boolean esNumero(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void guardarDato(String msg) {
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file, true); // true = agrega sin borrar
            pw = new PrintWriter(fw);
            pw.println(msg);
            pw.flush();

        } catch (IOException ex) {
            Logger.getLogger(ejercicio2.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            try {
                if (pw != null) pw.close();
                if (fw != null) fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ejercicio2.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }
}