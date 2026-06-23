package com.agenda.ui;

import com.agenda.model.Contacto;
import com.agenda.storage.Persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Capa de presentación: menú en bucle continuo con colores ANSI.
 *
 * Segmentación visual por colores:
 *  - Verde   → éxitos y datos de nombre
 *  - Amarillo → alertas y teléfono
 *  - Rojo    → errores y eliminación
 *  - Cyan    → encabezados y email
 *  - Magenta → nota privada
 *  - Azul    → títulos de sección
 */
public class Consola {

    // ── Códigos ANSI ──────────────────────────────────────────────────────────
    private static final String RESET    = "\u001B[0m";
    private static final String BOLD     = "\u001B[1m";
    private static final String RED      = "\u001B[31m";
    private static final String GREEN    = "\u001B[32m";
    private static final String YELLOW   = "\u001B[33m";
    private static final String BLUE     = "\u001B[34m";
    private static final String MAGENTA  = "\u001B[35m";
    private static final String CYAN     = "\u001B[36m";
    private static final String WHITE    = "\u001B[37m";
    private static final String BG_BLUE    = "\u001B[44m";
    private static final String BG_GREEN   = "\u001B[42m";
    private static final String BG_RED     = "\u001B[41m";
    private static final String BG_YELLOW  = "\u001B[43m";
    private static final String BG_MAGENTA = "\u001B[45m";
    private static final String BG_DARK    = "\u001B[100m";

    // ── Dependencias ──────────────────────────────────────────────────────────
    private final List<Contacto> agenda;
    private final Persistencia   persistencia;
    private final Scanner        scanner;

    public Consola(List<Contacto> agenda, Persistencia persistencia) {
        this.agenda       = agenda;
        this.persistencia = persistencia;
        this.scanner      = new Scanner(System.in);
    }

    // ════════════════════════════════════════════════════════════════════════
    // BUCLE PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════

    public void iniciar() {
        limpiarPantalla();
        mostrarBanner();

        while (true) {
            mostrarMenu();
            print(CYAN + BOLD + "  Seleccione una opción: " + RESET, false);
            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> flujoAgregar();
                    case "2" -> flujoMostrarTodos();
                    case "3" -> flujosBuscar();
                    case "4" -> flujoEditar();
                    case "5" -> flujoEliminar();
                    case "6" -> {
                        print("\n" + BG_GREEN + WHITE + BOLD
                                + " ✔ Agenda guardada. ¡Hasta pronto! " + RESET + "\n");
                        return;
                    }
                    default -> print("\n" + BG_YELLOW + " ⚠ " + RESET + YELLOW
                            + " Opción inválida. Ingrese un número del 1 al 6.\n" + RESET);
                }
            } catch (Exception e) {
                print(BG_RED + WHITE + BOLD + " ERROR " + RESET + " "
                        + RED + e.getMessage() + RESET + "\n");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // FLUJOS DE OPERACIÓN (CRUD)
    // ════════════════════════════════════════════════════════════════════════

    private void flujoAgregar() throws Exception {
        seccion("AGREGAR CONTACTO", BG_GREEN);
        String nombre   = leerObligatorio(GREEN   + "  Nombre        : " + RESET);
        String telefono = leerObligatorio(YELLOW  + "  Teléfono      : " + RESET);
        String email    = leerObligatorio(CYAN    + "  Email         : " + RESET);
        String nota     = leerObligatorio(MAGENTA + "  Nota Privada  : " + RESET);

        agenda.add(new Contacto(nombre, telefono, email, nota));
        persistencia.guardar(agenda);
        print(BG_GREEN + WHITE + BOLD + " ✔ Contacto agregado exitosamente. " + RESET + "\n");
    }

    private void flujoMostrarTodos() {
        seccion("TODOS LOS CONTACTOS", BG_BLUE);
        if (agenda.isEmpty()) {
            print(YELLOW + "  (No hay contactos registrados)\n" + RESET);
            return;
        }
        imprimirEncabezadoTabla();
        for (int i = 0; i < agenda.size(); i++) {
            imprimirFila(i + 1, agenda.get(i));
        }
        imprimirCierreTabla();
        print(CYAN + "  Total: " + BOLD + agenda.size() + RESET + CYAN + " contacto(s)\n" + RESET);
    }

    private void flujosBuscar() {
        seccion("BUSCAR CONTACTO", BG_MAGENTA);
        print(WHITE + "  Ingrese nombre o email a buscar: " + RESET, false);
        String termino = scanner.nextLine().trim().toLowerCase();

        List<Contacto> resultados = new ArrayList<>();
        for (Contacto c : agenda) {
            if (c.getNombre().toLowerCase().contains(termino)
                    || c.getEmail().toLowerCase().contains(termino)) {
                resultados.add(c);
            }
        }

        if (resultados.isEmpty()) {
            print(YELLOW + "  Sin resultados para: " + BOLD + termino + RESET + "\n");
        } else {
            print(GREEN + "  " + BOLD + resultados.size() + RESET + GREEN + " resultado(s):\n" + RESET);
            imprimirEncabezadoTabla();
            for (int i = 0; i < resultados.size(); i++) {
                imprimirFila(i + 1, resultados.get(i));
            }
            imprimirCierreTabla();
        }
    }

    private void flujoEditar() throws Exception {
        seccion("EDITAR CONTACTO", BG_YELLOW);
        mostrarListaNumerada();
        if (agenda.isEmpty()) return;

        int idx = seleccionarIndice();
        if (idx < 0) return;

        Contacto c = agenda.get(idx);
        print(CYAN + "\n  Deje en blanco para conservar el valor actual.\n" + RESET);

        String nombre   = leerOpcional(GREEN   + "  Nombre    [" + c.getNombre()   + "]: " + RESET, c.getNombre());
        String telefono = leerOpcional(YELLOW  + "  Teléfono  [" + c.getTelefono() + "]: " + RESET, c.getTelefono());
        String email    = leerOpcional(CYAN    + "  Email     [" + c.getEmail()    + "]: " + RESET, c.getEmail());
        String nota     = leerOpcional(MAGENTA + "  Nota Priv.[" + ocultarNota(c.getNotaPrivada()) + "]: " + RESET, c.getNotaPrivada());

        agenda.set(idx, new Contacto(nombre, telefono, email, nota));
        persistencia.guardar(agenda);
        print(BG_GREEN + WHITE + BOLD + " ✔ Contacto actualizado correctamente. " + RESET + "\n");
    }

    private void flujoEliminar() throws Exception {
        seccion("ELIMINAR CONTACTO", BG_RED);
        mostrarListaNumerada();
        if (agenda.isEmpty()) return;

        int idx = seleccionarIndice();
        if (idx < 0) return;

        Contacto c = agenda.get(idx);
        print(RED + "\n  ¿Confirma eliminar a " + BOLD + c.getNombre()
                + RESET + RED + "? (s/n): " + RESET, false);
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("s") || confirm.equals("si") || confirm.equals("sí")) {
            agenda.remove(idx);
            persistencia.guardar(agenda);
            print(BG_RED + WHITE + BOLD + " ✔ Contacto eliminado. " + RESET + "\n");
        } else {
            print(YELLOW + "  Operación cancelada.\n" + RESET);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // COMPONENTES DE INTERFAZ
    // ════════════════════════════════════════════════════════════════════════

    private void mostrarBanner() {
        print(CYAN + BOLD
            + "  ╔══════════════════════════════════════════════════╗\n"
            + "  ║         AGENDA PERSONAL CIFRADA  v1.0           ║\n"
            + "  ║   Cifrado AES · Persistencia Segura · Colores   ║\n"
            + "  ╚══════════════════════════════════════════════════╝"
            + RESET + "\n");
    }

    private void mostrarMenu() {
        print(BG_DARK + WHITE + BOLD + "  ╔══════ MENÚ PRINCIPAL ════════╗  " + RESET);
        print(BG_DARK + WHITE + "  ║  " + GREEN   + BOLD + "1" + RESET + BG_DARK + WHITE + "  Agregar contacto         ║  " + RESET);
        print(BG_DARK + WHITE + "  ║  " + BLUE    + BOLD + "2" + RESET + BG_DARK + WHITE + "  Mostrar todos           ║  " + RESET);
        print(BG_DARK + WHITE + "  ║  " + MAGENTA + BOLD + "3" + RESET + BG_DARK + WHITE + "  Buscar contacto         ║  " + RESET);
        print(BG_DARK + WHITE + "  ║  " + YELLOW  + BOLD + "4" + RESET + BG_DARK + WHITE + "  Editar contacto         ║  " + RESET);
        print(BG_DARK + WHITE + "  ║  " + RED     + BOLD + "5" + RESET + BG_DARK + WHITE + "  Eliminar contacto       ║  " + RESET);
        print(BG_DARK + WHITE + "  ║  " + CYAN    + BOLD + "6" + RESET + BG_DARK + WHITE + "  Salir                   ║  " + RESET);
        print(BG_DARK + WHITE + BOLD + "  ╚══════════════════════════════╝  " + RESET + "\n");
    }

    private void imprimirEncabezadoTabla() {
        print(CYAN + BOLD
            + "  ┌─────┬──────────────────────┬─────────────────┬───────────────────────┬────────────────────────────┐\n"
            + "  │  #  │       NOMBRE         │    TELÉFONO     │         EMAIL         │       NOTA PRIVADA         │\n"
            + "  ├─────┼──────────────────────┼─────────────────┼───────────────────────┼────────────────────────────┤"
            + RESET);
    }

    private void imprimirFila(int idx, Contacto c) {
        print("  │ " + YELLOW + BOLD + padR(String.valueOf(idx), 3) + RESET + " │ "
            + GREEN   + padR(truncar(c.getNombre(),   20), 20) + RESET + " │ "
            + CYAN    + padR(truncar(c.getTelefono(), 15), 15) + RESET + " │ "
            + BLUE    + padR(truncar(c.getEmail(),    21), 21) + RESET + " │ "
            + MAGENTA + padR(truncar(c.getNotaPrivada(), 26), 26) + RESET + " │");
    }

    private void imprimirCierreTabla() {
        print(CYAN
            + "  └─────┴──────────────────────┴─────────────────┴───────────────────────┴────────────────────────────┘"
            + RESET + "\n");
    }

    private void mostrarListaNumerada() {
        if (agenda.isEmpty()) {
            print(YELLOW + "  (No hay contactos registrados)\n" + RESET);
            return;
        }
        print(CYAN + BOLD + "\n  Contactos disponibles:\n" + RESET);
        for (int i = 0; i < agenda.size(); i++) {
            Contacto c = agenda.get(i);
            print("  " + YELLOW + BOLD + (i + 1) + RESET + ". "
                + GREEN + c.getNombre() + RESET + " — "
                + CYAN  + c.getTelefono() + RESET
                + " | " + BLUE + c.getEmail() + RESET);
        }
        print("");
    }

    private void seccion(String titulo, String colorFondo) {
        print("\n" + colorFondo + WHITE + BOLD + " ══ " + titulo + " ══ " + RESET + "\n");
    }

    // ════════════════════════════════════════════════════════════════════════
    // UTILIDADES DE ENTRADA
    // ════════════════════════════════════════════════════════════════════════

    private int seleccionarIndice() {
        print(WHITE + "  Ingrese el número del contacto: " + RESET, false);
        try {
            int n = Integer.parseInt(scanner.nextLine().trim());
            if (n < 1 || n > agenda.size()) {
                print(BG_YELLOW + " ⚠ " + RESET + YELLOW + " Número fuera de rango.\n" + RESET);
                return -1;
            }
            return n - 1;
        } catch (NumberFormatException e) {
            print(BG_YELLOW + " ⚠ " + RESET + YELLOW + " Entrada inválida.\n" + RESET);
            return -1;
        }
    }

    private String leerObligatorio(String etiqueta) {
        while (true) {
            print(etiqueta, false);
            String val = scanner.nextLine().trim();
            if (!val.isEmpty()) return val;
            print(BG_YELLOW + " ⚠ " + RESET + YELLOW + " Campo obligatorio.\n" + RESET);
        }
    }

    private String leerOpcional(String etiqueta, String porDefecto) {
        print(etiqueta, false);
        String val = scanner.nextLine().trim();
        return val.isEmpty() ? porDefecto : val;
    }

    // ════════════════════════════════════════════════════════════════════════
    // UTILIDADES DE FORMATO
    // ════════════════════════════════════════════════════════════════════════

    private String truncar(String s, int max) {
        if (s == null || s.isEmpty()) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    private String padR(String s, int ancho) {
        if (s == null) s = "";
        return s.length() >= ancho ? s : s + " ".repeat(ancho - s.length());
    }

    private String ocultarNota(String nota) {
        if (nota == null || nota.length() <= 3) return "***";
        return nota.substring(0, 2) + "*".repeat(Math.min(nota.length() - 2, 6));
    }

    private void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void print(String msg) {
        System.out.println(msg);
    }

    private void print(String msg, boolean newline) {
        if (newline) System.out.println(msg);
        else System.out.print(msg);
    }
}
