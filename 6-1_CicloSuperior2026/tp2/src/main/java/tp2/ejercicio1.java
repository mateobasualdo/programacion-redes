package tp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
		// TODO Auto-generated method stub

	}

}
