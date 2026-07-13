package com.examen;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Proporciona metodos para validar datos ingresados por el usuario
 * a traves de la consola.
 * <p>
 * La validacion minima requerida es que el dato ingresado no este vacio.
 * No se permite el uso de la clase Scanner.
 */
public class Validador {

    private static final String ROJO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    /**
     * Lee un texto ingresado por el usuario y valida que no sea vacio.
     * Si el usuario ingresa una cadena vacia, se muestra un mensaje de error
     * y se solicita nuevamente el ingreso.
     * <p>
     * Utiliza BufferedReader (NO Scanner).
     *
     * @param reader  BufferedReader conectado a System.in
     * @param mensaje mensaje a mostrar al usuario
     * @return el texto ingresado (garantizado no vacio)
     */
    public static String leerNoVacio(BufferedReader reader, String mensaje) {
        String entrada = "";
        boolean valido = false;

        while (!valido) {
            try {
                System.out.print(mensaje);
                String linea = reader.readLine();

                if (linea != null) {
                    linea = linea.trim();
                }

                if (linea == null || linea.isEmpty()) {
                    System.out.println(ROJO + "  [!] El valor no puede estar vacio. Intente nuevamente." + RESET);
                } else {
                    entrada = linea;
                    valido = true;
                }
            } catch (IOException ex) {
                LogManager.registrarError("Error al leer entrada del usuario en Validador.leerNoVacio", ex);
                System.out.println(ROJO + "  [!] Error de lectura. Intente nuevamente." + RESET);
            }
        }

        return entrada;
    }

    /**
     * Lee un entero validado (no vacio y numerico). Reintenta hasta obtener uno valido.
     *
     * @param reader  BufferedReader conectado a System.in
     * @param mensaje mensaje a mostrar al usuario
     * @return el entero ingresado
     */
    public static int leerEntero(BufferedReader reader, String mensaje) {
        while (true) {
            String texto = leerNoVacio(reader, mensaje);
            try {
                return Integer.parseInt(texto.trim());
            } catch (NumberFormatException ex) {
                System.out.println(ROJO + "  [!] Debe ingresar un numero entero valido." + RESET);
            }
        }
    }
}
