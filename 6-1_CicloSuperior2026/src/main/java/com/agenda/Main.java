package com.agenda;

import com.agenda.crypto.CifradoAES;
import com.agenda.model.Contacto;
import com.agenda.storage.Persistencia;
import com.agenda.ui.Consola;

import java.util.List;

/**
 * Punto de entrada de la Agenda Personal Cifrada.
 *
 * Flujo de inicialización:
 *  1. Cargar / generar la clave AES desde agenda.key.
 *  2. Leer agenda.dat y poblar la colección en memoria.
 *  3. Lanzar la interfaz de consola en bucle continuo.
 */
public class Main {

    public static void main(String[] args) {
        try {
            // 1. Capa de cifrado: inicializa o reutiliza la clave AES
            CifradoAES cifrado = new CifradoAES();

            // 2. Capa de persistencia: carga los contactos en memoria
            Persistencia persistencia = new Persistencia(cifrado);
            List<Contacto> agenda     = persistencia.cargar();

            notificarCarga(agenda.size(), persistencia.existeArchivo());

            // 3. Capa de presentación: menú interactivo con colores ANSI
            Consola consola = new Consola(agenda, persistencia);
            consola.iniciar();

        } catch (Exception e) {
            System.err.println("\u001B[41m\u001B[37m\u001B[1m ERROR CRÍTICO \u001B[0m "
                    + "\u001B[31m" + e.getMessage() + "\u001B[0m");
            System.exit(1);
        }
    }

    private static void notificarCarga(int total, boolean existia) {
        String BG_DARK = "\u001B[100m";
        String WHITE   = "\u001B[37m";
        String BOLD    = "\u001B[1m";
        String YELLOW  = "\u001B[33m";
        String RESET   = "\u001B[0m";

        if (!existia) {
            System.out.println(YELLOW + "  (No se encontró agenda.dat — agenda nueva iniciada)\n" + RESET);
        } else {
            System.out.println(BG_DARK + WHITE + " ✔ Agenda cargada: "
                    + BOLD + total + RESET + BG_DARK + WHITE + " contacto(s) " + RESET + "\n");
        }
    }
}
