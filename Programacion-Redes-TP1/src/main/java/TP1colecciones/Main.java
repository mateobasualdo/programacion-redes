package TP1colecciones;

import java.util.*;

public class Main {

	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		ejercicio12(sc);
		
		sc.close();
	}

	// EJERCICIO 1
	
	public static ArrayList<String> ejercicio1(Scanner sc) {
		ArrayList<String> lista = new ArrayList<>();
		System.out.println("Ingrese 5 nombres:");
		for (int i = 0; i < 5; i++) {
			lista.add(sc.nextLine());
		}
		System.out.println("Lista: " + lista);
		System.out.println("Cantidad: " + lista.size());
		System.out.println("Primero: " + lista.get(0) + " | Último: " + lista.get(lista.size() - 1));
		
		System.out.print("Mayúsculas: ");
		lista.forEach(n -> System.out.print(n.toUpperCase() + " "));
		System.out.println();
		return lista;
	}
	
	// EJERCICIO 2

	public static void ejercicio2(Scanner sc, ArrayList<String> lista) {
		System.out.println("\nNombre a buscar:");
		String busca = sc.nextLine();
		if (lista.contains(busca)) {
			System.out.println(GREEN + "Existe. Índice: " + lista.indexOf(busca) + RESET);
		} else {
			System.out.println(RED + "El nombre no existe en la lista." + RESET);
		}
	}
	
	// EJERCICIO 3

	public static void ejercicio3(ArrayList<String> lista) {
		if (lista.size() >= 3) {
			lista.set(2, "ValorModificado");
			System.out.println("Tercero reemplazado. Borrando el primero...");
			lista.remove(0);
			System.out.println("Lista final: " + lista);
		}
	}
	
	// EJERCICIO 4 

	public static void ejercicio4(ArrayList<String> lista) {
		System.out.println("\n--- Recorridos ---");
		// 1. For clásico (Error típico: i <= size causa IndexOutOfBounds)
		for (int i = 0; i < lista.size(); i++) {
			System.out.println(BLUE + "Índice " + i + ": " + lista.get(i) + RESET);
		}
		// 2. For-each
		for (String s : lista) {
			System.out.println(YELLOW + s + RESET);
		}
		// 3. Iterator
		Iterator<String> it = lista.iterator();
		while (it.hasNext()) {
			System.out.println(GREEN + it.next() + RESET);
		}
	}

	// EJERCICIO 5

	public static void ejercicio5(Scanner sc) {
		System.out.println("\nIngrese una frase:");
		String frase = sc.nextLine();
		ArrayList<String> palabras = new ArrayList<>(Arrays.asList(frase.split(" ")));
		
		String larga = "";
		for (String p : palabras) {
			if (p.length() > larga.length()) larga = p;
		}
		
		System.out.println("Palabras: " + palabras.size());
		System.out.println("Más larga: " + larga);
		
    // EJERCICIO 6
		System.out.println("Normalizando...");
		for (int i = 0; i < palabras.size(); i++) {
			String p = palabras.get(i).toLowerCase().trim();
			p = p.replaceAll("[aeiouAEIOU]", "*");
			palabras.set(i, p);
		}
		System.out.println("Resultado: " + palabras);
	}

	// EJERCICIOS 7 8 y 9

	public static void ejercicio7y8y9(Scanner sc) {
		HashMap<String, String> dicc = new HashMap<>();
		for (int i = 0; i < 3; i++) { // 3 para ejemplo rápido
			System.out.print("Palabra (ES): "); String es = sc.nextLine();
			System.out.print("Traducción (EN): "); String en = sc.nextLine();
			dicc.put(es, en);
		}

		System.out.println("Claves: " + dicc.keySet());
		System.out.println("Valores: " + dicc.values());

		System.out.println("\nFrase a traducir:");
		String[] frase = sc.nextLine().split(" ");
		for (String pal : frase) {
			System.out.print(dicc.getOrDefault(pal, "[???]") + " ");
		}
		System.out.println();
	}

	// EJERCICIO 10

	public static void ejercicio10(Scanner sc) {
		System.out.println("\nIngrese frase para contar:");
		String[] palabras = sc.nextLine().split(" ");
		HashMap<String, Integer> contador = new HashMap<>();

		for (String p : palabras) {
			contador.put(p, contador.getOrDefault(p, 0) + 1);
		}
		System.out.println("Frecuencia: " + contador);
	}

	// EJERCICIO 11

	public static void ejercicio11(Scanner sc) {
		ArrayList<Integer> nums = new ArrayList<>();
		System.out.println("Ingrese números (0 para terminar):");
		int n;
		while ((n = sc.nextInt()) != 0) nums.add(n);
		
		HashSet<Integer> sinRepetir = new HashSet<>(nums);
		System.out.println("Original: " + nums);
		System.out.println("Sin repetidos: " + sinRepetir);
	}

	// EJERCICIO 12

	public static void ejercicio12(Scanner sc) {
		ArrayList<String> nombres = new ArrayList<>();
		HashMap<String, Integer> notas = new HashMap<>();
		
		while(true) {
			System.out.println("\n1. Agregar 2. Mostrar 3. Buscar 4. Promedio 5. Salir");
			int op = sc.nextInt(); sc.nextLine();
			
			if(op == 5) break;
			
			switch(op) {
				case 1:
					System.out.print("Nombre: "); String nom = sc.nextLine();
					System.out.print("Nota: "); int nota = sc.nextInt();
					nombres.add(nom);
					notas.put(nom, nota);
					break;
				case 2:
					for(String s : nombres) {
						int nt = notas.get(s);
						String color = (nt >= 6) ? GREEN : RED;
						System.out.println(color + s + ": " + nt + RESET);
					}
					break;
				case 3:
					System.out.print("Buscar nombre: "); String b = sc.nextLine();
					if(notas.containsKey(b)) System.out.println("Nota: " + notas.get(b));
					else System.out.println(RED + "No encontrado" + RESET);
					break;
				case 4:
					double sum = 0;
					for(int v : notas.values()) sum += v;
					System.out.println("Promedio general: " + (sum / notas.size()));
					break;
			}
		}
	}
}
