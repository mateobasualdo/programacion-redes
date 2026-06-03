package tp2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ejercicio1 {
	
	private String ruta = "";
	private File file;
	private PrintStream ps;
	
	public ejercicio1(String nombre) {
		file = new File(ruta.concat(nombre));
		ps = new PrintStream(System.out);
		
	}
	
	public static void main(String[] args) {
		ejercicio1 ej = new ejercicio1("prueba1.txt");
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Ingresa un dato: ");
		String dato = sc.nextLine();
		sc.close();
		
		ej.guardarDato(dato);
	}
		
	public void guardarDato(String msg) {
		FileWriter fw = null;
		PrintWriter pw = null;
		
		try {
			if (!file.exists()) {
				file.createNewFile();
		}
		
		fw = new FileWriter(file, false);
		pw = new PrintWriter(fw);
		pw.println(msg);
        pw.flush();
	
	}catch (IOException ex) {
		Logger.getLogger(ejercicio1.class.getName()).log(Level.WARNING, null, ex);
	
	}finally{
		try {
			if (pw != null) pw.close();
			if (fw != null) fw.close();
		} catch (IOException ex) {
			Logger.getLogger(ejercicio1.class.getName()).log(Level.WARNING, null, ex);
			}
		}
	}
}