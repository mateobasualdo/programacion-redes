package com.inventario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Inventario {

    // ─── Colores ANSI ────────────────────────────────────────────────────────
    static final String RESET    = "\033[0m";
    static final String BOLD     = "\033[1m";
    static final String ROJO     = "\033[31m";
    static final String VERDE    = "\033[32m";
    static final String AMARILLO = "\033[33m";
    static final String CYAN     = "\033[36m";
    static final String BLANCO   = "\033[97m";
    static final String MAGENTA  = "\033[35m";
    static final String AZUL     = "\033[34m";

    static final String ARCHIVO = "Inventario.dat";
    static final Scanner sc     = new Scanner(System.in);

    // ════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        crearArchivoSiNoExiste();
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("   Seleccione una opcion: ");
            switch (opcion) {
                case 1:
                    agregarProducto();
                    break;
                case 2:
                    mostrarProductos();
                    break;
                case 3:
                    editarProducto();
                    break;
                case 4:
                    eliminarProducto();
                    break;
                case 5:
                    salir();
                    break;
                default:
                    mensajeError("Opcion invalida. Intente de nuevo.");
                    break;
            }
        } while (opcion != 5);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MENU PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════
    static void mostrarMenu() {
        System.out.println();
        System.out.println(BOLD + CYAN + "╔══════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + CYAN + "║     " + BLANCO + "SISTEMA DE INVENTARIO v1.0" + CYAN + "     ║" + RESET);
        System.out.println(BOLD + CYAN + "╠══════════════════════════════════════╣" + RESET);
        System.out.println(CYAN + "║" + RESET + "  " + VERDE    + "[1]" + RESET + "  Agregar producto              " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + RESET + "  " + AZUL     + "[2]" + RESET + "  Mostrar productos             " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + RESET + "  " + AMARILLO + "[3]" + RESET + "  Editar producto               " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + RESET + "  " + ROJO     + "[4]" + RESET + "  Eliminar producto             " + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + RESET + "  " + MAGENTA  + "[5]" + RESET + "  Salir                         " + CYAN + "║" + RESET);
        System.out.println(BOLD + CYAN + "╚══════════════════════════════════════╝" + RESET);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  1. AGREGAR PRODUCTO
    // ════════════════════════════════════════════════════════════════════════
    static void agregarProducto() {
        encabezadoSeccion("AGREGAR PRODUCTO", VERDE);

        String nombre       = leerTextoObligatorio("   Nombre del producto : ");
        float  precioCompra = leerFlotante("   Precio de compra   : ");
        float  precioVenta  = leerFlotante("   Precio de venta    : ");
        int    stock        = leerEntero("   Stock              : ");

        String linea = nombre + ";" + precioCompra + ";" + precioVenta + ";" + stock;
        escribirLinea(linea);
        mensajeExito("Producto '" + nombre + "' registrado correctamente.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  2. MOSTRAR PRODUCTOS
    // ════════════════════════════════════════════════════════════════════════
    static void mostrarProductos() {
        encabezadoSeccion("LISTADO DE PRODUCTOS", AZUL);

        List<String[]> productos = leerTodosLosRegistros();

        if (productos.isEmpty()) {
            mensajeError("No hay productos registrados.");
            return;
        }

        String fmt = "  %-4s  %-22s  %-14s  %-14s  %-8s";
        System.out.println(BOLD + CYAN +
            String.format(fmt, "N.", "NOMBRE", "P.COMPRA", "P.VENTA", "STOCK") + RESET);
        System.out.println(CYAN + "  " + linea(68) + RESET);

        for (int i = 0; i < productos.size(); i++) {
            String[] c = productos.get(i);
            if (c.length < 4) continue;
            String fila = String.format(fmt,
                (i + 1),
                truncar(c[0], 22),
                "$ " + c[1],
                "$ " + c[2],
                c[3]);
            System.out.println(BLANCO + fila + RESET);
        }

        System.out.println(CYAN + "  " + linea(68) + RESET);
        System.out.println("  " + AMARILLO + "Total de productos: " + productos.size() + RESET);
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  3. EDITAR PRODUCTO
    // ════════════════════════════════════════════════════════════════════════
    static void editarProducto() {
        encabezadoSeccion("EDITAR PRODUCTO", AMARILLO);

        List<String[]> productos = leerTodosLosRegistros();
        if (productos.isEmpty()) {
            mensajeError("No hay productos para editar.");
            return;
        }

        mostrarProductos();
        int idx = leerEnteroEnRango("   Numero de producto a editar: ", 1, productos.size());
        String[] prod = productos.get(idx - 1);

        System.out.println(AMARILLO + "\n   Editando: " + BOLD + prod[0] + RESET);
        System.out.println(AMARILLO + "   (Presione ENTER para mantener el valor actual)\n" + RESET);

        String nombre  = leerTextoOpcional("   Nombre          [" + prod[0] + "]: ", prod[0]);
        float  pCompra = leerFlotanteOpcional("   Precio compra   [" + prod[1] + "]: ", parseFlotante(prod[1]));
        float  pVenta  = leerFlotanteOpcional("   Precio venta    [" + prod[2] + "]: ", parseFlotante(prod[2]));
        int    stock   = leerEnteroOpcional("   Stock           [" + prod[3] + "]: ", parseInt(prod[3]));

        String[] actualizado = {
            nombre,
            String.valueOf(pCompra),
            String.valueOf(pVenta),
            String.valueOf(stock)
        };
        productos.set(idx - 1, actualizado);
        reescribirArchivo(productos);
        mensajeExito("Producto actualizado correctamente.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  4. ELIMINAR PRODUCTO
    // ════════════════════════════════════════════════════════════════════════
    static void eliminarProducto() {
        encabezadoSeccion("ELIMINAR PRODUCTO", ROJO);

        List<String[]> productos = leerTodosLosRegistros();
        if (productos.isEmpty()) {
            mensajeError("No hay productos para eliminar.");
            return;
        }

        mostrarProductos();
        int idx = leerEnteroEnRango("   Numero de producto a eliminar: ", 1, productos.size());
        String nombre = productos.get(idx - 1)[0];

        System.out.print(ROJO + "   Confirmar eliminacion de '" + nombre + "'? (s/n): " + RESET);
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("s")) {
            productos.remove(idx - 1);
            reescribirArchivo(productos);
            mensajeExito("Producto '" + nombre + "' eliminado.");
        } else {
            System.out.println(AMARILLO + "   Operacion cancelada." + RESET);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  5. SALIR
    // ════════════════════════════════════════════════════════════════════════
    static void salir() {
        System.out.println();
        System.out.println(BOLD + MAGENTA + "  Hasta luego! Sistema de Inventario cerrado." + RESET);
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  PERSISTENCIA EN ARCHIVO
    // ════════════════════════════════════════════════════════════════════════

    static void crearArchivoSiNoExiste() {
        File f = new File(ARCHIVO);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                mensajeError("No se pudo crear el archivo: " + e.getMessage());
            }
        }
    }

    static void escribirLinea(String linea) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(ARCHIVO, true));
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            mensajeError("Error al escribir en el archivo: " + e.getMessage());
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException e) { /* ignorar */ }
            }
        }
    }

    static List<String[]> leerTodosLosRegistros() {
        List<String[]> lista = new ArrayList<String[]>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ARCHIVO));
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    lista.add(linea.split(";"));
                }
            }
        } catch (IOException e) {
            mensajeError("Error al leer el archivo: " + e.getMessage());
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException e) { /* ignorar */ }
            }
        }
        return lista;
    }

    static void reescribirArchivo(List<String[]> productos) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(ARCHIVO, false));
            for (int i = 0; i < productos.size(); i++) {
                String[] prod = productos.get(i);
                bw.write(prod[0] + ";" + prod[1] + ";" + prod[2] + ";" + prod[3]);
                bw.newLine();
            }
        } catch (IOException e) {
            mensajeError("Error al reescribir el archivo: " + e.getMessage());
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException e) { /* ignorar */ }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LECTURA Y VALIDACION DE DATOS
    // ════════════════════════════════════════════════════════════════════════

    /** Lee una linea de consola y la retorna como String */
    static String leerConsola(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine();
    }

    /** Detecta si el texto es ENTERO, DECIMAL o NO_NUMERICO */
    static String tipoNumerico(String texto) {
        texto = texto.trim();
        if (texto.matches("^-?\\d+$"))         return "ENTERO";
        if (texto.matches("^-?\\d+\\.\\d+$"))  return "DECIMAL";
        return "NO_NUMERICO";
    }

    /** Conversion segura a int */
    static int parseInt(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Conversion segura a float */
    static float parseFlotante(String texto) {
        try {
            return Float.parseFloat(texto.trim());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    /** Lee un entero con validacion; repite hasta obtener un valor valido */
    static int leerEntero(String mensaje) {
        while (true) {
            String entrada = leerConsola(mensaje);
            String tipo    = tipoNumerico(entrada);
            if (tipo.equals("ENTERO") || tipo.equals("DECIMAL")) {
                return (int) parseFlotante(entrada);
            }
            mensajeError("Valor invalido. Ingrese un numero entero.");
        }
    }

    /** Lee un entero dentro de un rango [min, max] */
    static int leerEnteroEnRango(String mensaje, int min, int max) {
        while (true) {
            int val = leerEntero(mensaje);
            if (val >= min && val <= max) return val;
            mensajeError("El numero debe estar entre " + min + " y " + max + ".");
        }
    }

    /** Lee un numero flotante con validacion */
    static float leerFlotante(String mensaje) {
        while (true) {
            String entrada = leerConsola(mensaje);
            String tipo    = tipoNumerico(entrada);
            if (tipo.equals("ENTERO") || tipo.equals("DECIMAL")) {
                return parseFlotante(entrada);
            }
            mensajeError("Valor invalido. Ingrese un numero (ej: 12.5).");
        }
    }

    /** Lee texto obligatorio; no puede estar vacio */
    static String leerTextoObligatorio(String mensaje) {
        while (true) {
            String entrada = leerConsola(mensaje).trim();
            if (!entrada.isEmpty()) return entrada;
            mensajeError("El campo no puede estar vacio.");
        }
    }

    /** Lee texto opcional; devuelve valorDefecto si el usuario presiona ENTER */
    static String leerTextoOpcional(String mensaje, String valorDefecto) {
        String entrada = leerConsola(mensaje).trim();
        return entrada.isEmpty() ? valorDefecto : entrada;
    }

    /** Lee float opcional; devuelve valorDefecto si el usuario presiona ENTER */
    static float leerFlotanteOpcional(String mensaje, float valorDefecto) {
        while (true) {
            String entrada = leerConsola(mensaje).trim();
            if (entrada.isEmpty()) return valorDefecto;
            String tipo = tipoNumerico(entrada);
            if (tipo.equals("ENTERO") || tipo.equals("DECIMAL")) {
                return parseFlotante(entrada);
            }
            mensajeError("Valor invalido. Ingrese un numero o presione ENTER para conservar.");
        }
    }

    /** Lee int opcional; devuelve valorDefecto si el usuario presiona ENTER */
    static int leerEnteroOpcional(String mensaje, int valorDefecto) {
        while (true) {
            String entrada = leerConsola(mensaje).trim();
            if (entrada.isEmpty()) return valorDefecto;
            String tipo = tipoNumerico(entrada);
            if (tipo.equals("ENTERO") || tipo.equals("DECIMAL")) {
                return (int) parseFlotante(entrada);
            }
            mensajeError("Valor invalido. Ingrese un entero o presione ENTER para conservar.");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  UTILIDADES DE PRESENTACION
    // ════════════════════════════════════════════════════════════════════════

    static void encabezadoSeccion(String titulo, String color) {
        System.out.println();
        System.out.println(BOLD + color + "  ┌─────────────────────────────────────┐" + RESET);
        System.out.println(BOLD + color + "  │   " + titulo + RESET);
        System.out.println(BOLD + color + "  └─────────────────────────────────────┘" + RESET);
        System.out.println();
    }

    static void mensajeExito(String msg) {
        System.out.println();
        System.out.println(VERDE + "  [OK]  " + msg + RESET);
        System.out.println();
    }

    static void mensajeError(String msg) {
        System.out.println(ROJO + "  [!]   " + msg + RESET);
    }

    static String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() <= max ? texto : texto.substring(0, max - 3) + "...";
    }

    static String linea(int cantidad) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cantidad; i++) {
            sb.append("─");
        }
        return sb.toString();
    }
}
