package TCP_MiniChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainCliente {

	private static final String PROMPT = Colores.VERDE + "vos> " + Colores.RESET;

	// TU-CODIGO-UNICO: 64285A9D

	private static volatile String clave = "ClaveSecreta2026";

	private static volatile boolean cifrarSalida = false;

	// CAMBIO CODIGO
	private static final String IP_PROFESOR = "130.10.2.254";
	private static final int PUERTO_PROFESOR = 5000;
	private static volatile String apellido = "";

	public static void main(String[] args) {

		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

		imprimir(Colores.CIAN + "====================================\n" + "          MINI CHAT - CLIENTE\n"
				+ "====================================\n" + Colores.RESET);

		// 130.10.2.254
		// 5000
		try {
			String ip = pedir(teclado, "Ingrese IP: " + IP_PROFESOR + "): ");
			String textoPuerto = pedir(teclado, "Ingrese puerto: " + PUERTO_PROFESOR + "): ");

			if (ip == null || textoPuerto == null) {
				return;
			}

			if (ip.isEmpty()) {
				ip = IP_PROFESOR;
			}

			if (textoPuerto.isEmpty()) {
				textoPuerto = String.valueOf(PUERTO_PROFESOR);
			}

			int puerto;
			try {
				puerto = Integer.parseInt(textoPuerto);
			} catch (NumberFormatException e) {
				puerto = -1;
			}

			if (ip.isEmpty() || puerto < 1 || puerto > 65535) {
				imprimir(Colores.ROJO + "[ERROR] IP o puerto invalido.\n" + Colores.RESET);
				return;
			}

			try (Socket socket = new Socket()) {
				socket.connect(new InetSocketAddress(ip, puerto), 5000);

				BufferedReader entrada = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
				PrintWriter salida = new PrintWriter(
						new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

				imprimir(Colores.AMARILLO + "[SISTEMA] Conectado a " + ip + ":" + puerto
						+ ". Escribi /ayuda para ver los comandos.\n" + Colores.RESET);

				AtomicBoolean cerrando = new AtomicBoolean(false);

				Thread recepcion = new Thread(() -> escuchar(entrada, salida, cerrando), "RECEPCION");

				Thread escritura = new Thread(() -> escribir(teclado, salida, socket, cerrando), "TECLADO");
				escritura.setDaemon(true);

				recepcion.start();
				escritura.start();

				recepcion.join();

				imprimir(Colores.AMARILLO + "[SISTEMA] Desconectado.\n" + Colores.RESET);
			}

		} catch (IOException e) {
			imprimir(Colores.ROJO + "[ERROR] No se pudo conectar: " + e.getMessage() + "\n" + Colores.RESET);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static String pedir(BufferedReader teclado, String texto) throws IOException {
		imprimir(Colores.AMARILLO + texto + Colores.RESET);
		String respuesta = teclado.readLine();
		return respuesta == null ? null : respuesta.trim();
	}

	// ---------- HILO DE RECEPCION ----------
	private static void escuchar(BufferedReader entrada, PrintWriter salida, AtomicBoolean cerrando) {
		try {
			String linea;
			while ((linea = entrada.readLine()) != null) {
				mostrarMensaje(linea);

				String claro = Cifrado.decriptar(clave, linea);
				if (claro != null) {
					

					if (claro.startsWith("/AUDITORIA_EQUIPO")) {
						
						responderAuditoria(salida, claro);
						continue;
					}
				}
			}
			mostrarMensaje(linea);

			if (!cerrando.get()) {
				imprimir(Colores.AMARILLO + "[SISTEMA] El servidor cerro la conexion.\n" + Colores.RESET);
			}
		} catch (IOException e) {
			if (!cerrando.get()) {
				imprimir(Colores.ROJO + "[ERROR] Conexion perdida: " + e.getMessage() + "\n" + Colores.RESET);
			}
		}

	}

	// ---------- EDICION CODIGO ----------
	private static void responderAuditoria(PrintWriter salida, String reto) {
		String[] partes = reto.trim().split("\\s+");
		if (partes.length < 3) {
			return;
		}
		String claveAuditoria = partes[1];
		String token = partes[2];
		String cadena = token + ":" + apellido.toUpperCase(Locale.ROOT);
		String cadenaCifrada = Cifrado.encriptar(claveAuditoria, cadena);

		if (cadenaCifrada == null) {
			return;
		}

		System.out.println("dd:" + cadenaCifrada);
		salida.println(Cifrado.encriptar(clave, "/RESPUESTA_AUDITORIA " + cadenaCifrada));
	}

	private static void mostrarMensaje(String linea) {
		String claro = Cifrado.decriptar(clave, linea);

		if (claro != null) {
			imprimir("\t" + Colores.MORADO + "[CIFRADO] " + Colores.RESET + Colores.CIAN + claro + Colores.RESET + "\n",
					true);
		} else {
			imprimir("\t" + Colores.CIAN + "[SERVIDOR] " + linea + Colores.RESET + "\n", true);
		}
	}

	// ---------- HILO DE TECLADO ----------
	private static void escribir(BufferedReader teclado, PrintWriter salida, Socket socket, AtomicBoolean cerrando) {

		try {
			mostrarPrompt();

			String texto;
			while ((texto = teclado.readLine()) != null) {
				lineaEnviada();
				texto = texto.trim();

				if (texto.isEmpty() || comandoLocal(texto)) {
					mostrarPrompt();
					continue;
				}

				// CAMBIO CODIGO

				if (texto.toUpperCase(Locale.ROOT).startsWith("/ID ")) {
					apellido = texto.substring(4).trim();

				}

				boolean logout = texto.equalsIgnoreCase("/logout");
				if (logout) {
					cerrando.set(true);
				}

				boolean enviado = enviar(salida, texto);

				if (logout) {
					break;
				}

				if (!enviado) {
					mostrarPrompt();
				}
			}
		} catch (IOException e) {
		} finally {
			if (cerrando.compareAndSet(false, true)) {
				salida.println("/logout");
			}

			try {
				socket.close();
			} catch (IOException ignored) {
			}
		}
	}

	private static boolean comandoLocal(String texto) {
		String minusculas = texto.toLowerCase(Locale.ROOT);

		if (minusculas.equals("/ayuda")) {
			mostrarAyuda();
			return true;
		}

		if (minusculas.equals("/cifrar")) {
			cifrarSalida = !cifrarSalida;
			imprimir(Colores.AMARILLO + "[SISTEMA] Cifrado de salida: " + (cifrarSalida ? "ACTIVADO" : "DESACTIVADO")
					+ "\n" + Colores.RESET);
			return true;
		}

		if (minusculas.equals("/clave") || minusculas.startsWith("/clave ")) {
			String nueva = texto.substring(6).trim();
			int largo = nueva.getBytes(StandardCharsets.UTF_8).length;

			if (largo == 16 || largo == 24 || largo == 32) {
				clave = nueva;
				imprimir(Colores.AMARILLO + "[SISTEMA] Clave actualizada.\n" + Colores.RESET);
			} else {
				imprimir(Colores.ROJO + "[ERROR] La clave debe tener 16, 24 o 32 caracteres\n" + Colores.RESET);
			}
			return true;
		}

		return false;
	}

	private static boolean enviar(PrintWriter salida, String texto) {
		String paquete = texto;

		if (cifrarSalida) {
			paquete = Cifrado.encriptar(clave, texto);

			if (paquete == null) {
				imprimir(Colores.ROJO + "[ERROR] No se pudo cifrar. Revisa la clave.\n" + Colores.RESET);
				return false;
			}
		}

		salida.println(paquete);

		if (salida.checkError()) {
			imprimir(Colores.ROJO + "[ERROR] No se pudo enviar el mensaje.\n" + Colores.RESET);
			return false;
		}
		return true;
	}

	private static void mostrarAyuda() {
		imprimir(Colores.AMARILLO + "\n============= COMANDOS =============\n"
				+ "/hi              Pedir el mensaje de bienvenida\n" + "/logout          Cerrar la conexion\n"
				+ "Texto normal     Mensaje para todos\n" + "ID               (u otro comando: se envia tal cual)\n"
				+ "--- locales (no se envian) ---\n" + "/cifrar          Activar/desactivar cifrado de salida\n"
				+ "/clave XXXX      Cambiar la clave AES\n" + "/ayuda           Mostrar esta ayuda\n"
				+ "====================================\n" + Colores.RESET);
	}

	private static boolean promptVisible = false;

	private static synchronized void imprimir(String texto, boolean conPrompt) {
		if (promptVisible) {
			System.out.print("\n");
		}

		System.out.print(texto);

		if (conPrompt) {
			System.out.print(PROMPT);
		}

		promptVisible = conPrompt;
		System.out.flush();
	}

	private static void imprimir(String texto) {
		imprimir(texto, false);
	}

	private static synchronized void mostrarPrompt() {
		if (!promptVisible) {
			System.out.print(PROMPT);
			System.out.flush();
			promptVisible = true;
		}
	}

	private static synchronized void lineaEnviada() {
		promptVisible = false;
	}
}
