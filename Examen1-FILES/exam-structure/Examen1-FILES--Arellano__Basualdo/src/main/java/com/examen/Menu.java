package com.examen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Menu principal de la aplicacion.
 * Presenta opciones al usuario y ejecuta las acciones correspondientes.
 * <p>
 * NO se permite usar la clase Scanner. Se utiliza BufferedReader + InputStreamReader.
 * <p>
 * Opciones:
 * 1 - Mostrar todas las partidas
 * 2 - Eliminar 1 partida elegida por el usuario
 * 3 - Ver mes con mayor cantidad de victorias
 * 4 - Ingresar una nueva partida
 * 5 - Ver estadisticas (mejor mes + KDA general)
 * 6 - Guardar y salir
 */
public class Menu {

    // Codigos ANSI.
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARILLO = "\u001B[33m";
    private static final String ROJO = "\u001B[31m";
    private static final String NEGRITA = "\u001B[1m";

    private GestorPartidas gestor;
    private BufferedReader reader;
    private String rutaCsv;

    /**
     * Crea el menu asociado a un gestor de partidas.
     * Inicializa el BufferedReader para leer desde la consola.
     *
     * @param gestor el gestor de partidas
     */
    public Menu(GestorPartidas gestor) {
        this.gestor = gestor;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.rutaCsv = "juegos.csv";
    }

    /**
     * Crea el menu indicando la ruta del CSV destino donde guardar al salir.
     *
     * @param gestor  el gestor de partidas
     * @param rutaCsv ruta del CSV que se sobrescribe al guardar
     */
    public Menu(GestorPartidas gestor, String rutaCsv) {
        this(gestor);
        this.rutaCsv = rutaCsv;
    }

    /**
     * Inicia el bucle principal del menu.
     * Muestra las opciones, solicita la eleccion al usuario y ejecuta la accion.
     * El bucle se repite hasta que el usuario elija salir.
     */
    public void iniciar() {
        boolean salir = false;

        while (!salir) {
            mostrarOpciones();
            String opcion = Validador.leerNoVacio(reader, CYAN + "  Elija una opcion: " + RESET);

            switch (opcion) {
                case "1":
                    gestor.mostrarTodos();
                    break;
                case "2":
                    opcionEliminar();
                    break;
                case "3":
                    opcionMesMasVictorias();
                    break;
                case "4":
                    opcionIngresarPartida();
                    break;
                case "5":
                    opcionEstadisticas();
                    break;
                case "6":
                    gestor.guardar(rutaCsv);
                    System.out.println(VERDE + "\n  Datos guardados. Hasta la proxima!\n" + RESET);
                    salir = true;
                    break;
                default:
                    System.out.println(ROJO + "  [!] Opcion invalida. Ingrese un numero del 1 al 6." + RESET);
            }
        }
    }

    /**
     * Imprime el menu principal tabulado y coloreado.
     */
    private void mostrarOpciones() {
        System.out.println();
        System.out.println(CYAN + NEGRITA + "  ============================================" + RESET);
        System.out.println(CYAN + NEGRITA + "        HISTORIAL DE PARTIDAS - LoL" + RESET);
        System.out.println(CYAN + NEGRITA + "  ============================================" + RESET);
        System.out.println(VERDE  + "    1." + RESET + "  Mostrar todas las partidas");
        System.out.println(VERDE  + "    2." + RESET + "  Eliminar una partida");
        System.out.println(VERDE  + "    3." + RESET + "  Mes con mas victorias");
        System.out.println(VERDE  + "    4." + RESET + "  Ingresar una nueva partida");
        System.out.println(VERDE  + "    5." + RESET + "  Estadisticas del jugador (KDA)");
        System.out.println(ROJO   + "    6." + RESET + "  Guardar y salir");
        System.out.println(CYAN + NEGRITA + "  ============================================" + RESET);
    }

    /**
     * Solicita el indice a eliminar y delega en el gestor.
     */
    private void opcionEliminar() {
        if (gestor.cantidadPartidas() == 0) {
            System.out.println(AMARILLO + "  No hay partidas para eliminar." + RESET);
            return;
        }
        gestor.mostrarTodos();
        int indice = Validador.leerEntero(reader,
                CYAN + "  Ingrese el # de la partida a eliminar: " + RESET);
        gestor.eliminar(indice);
    }

    /**
     * Muestra el mes con mas victorias.
     */
    private void opcionMesMasVictorias() {
        String mes = gestor.mesMasVictorias();
        if (mes.equals("SIN DATOS")) {
            System.out.println(AMARILLO + "\n  No hay datos suficientes para calcular el mejor mes.\n" + RESET);
        } else {
            System.out.println(VERDE + "\n  El mes con mas victorias es: " + NEGRITA + mes + RESET + "\n");
        }
    }

    /**
     * Carga manualmente una nueva partida en memoria.
     */
    private void opcionIngresarPartida() {
        System.out.println(CYAN + "\n  --- Nueva partida ---" + RESET);
        String fecha = Validador.leerNoVacio(reader, "  Fecha (dd/MM): ");
        boolean gano = leerSiNo("  Gano la partida? (s/n): ");
        boolean primerTorreta = leerSiNo("  Derribo la primer torreta? (s/n): ");
        boolean primeraSangre = leerSiNo("  Obtuvo la primera sangre? (s/n): ");
        int asesinatos = Validador.leerEntero(reader, "  Asesinatos: ");
        int muertes = Validador.leerEntero(reader, "  Muertes: ");
        int asistencias = Validador.leerEntero(reader, "  Asistencias: ");

        gestor.agregar(new Partida(fecha, gano, primerTorreta, primeraSangre,
                asesinatos, muertes, asistencias));
        System.out.println(VERDE + "  [OK] Partida agregada en memoria." + RESET);
    }

    /**
     * Muestra estadisticas: mejor mes y KDA general.
     * KDA = (Asesinatos + Asistencias) / Muertes.
     * Si Muertes == 0 se considera "KDA Perfecto" (sin division por cero).
     */
    private void opcionEstadisticas() {
        List<Partida> lista = gestor.getPartidas();
        if (lista.isEmpty()) {
            System.out.println(AMARILLO + "\n  No hay datos para calcular estadisticas.\n" + RESET);
            return;
        }

        int totalAsesinatos = 0;
        int totalMuertes = 0;
        int totalAsistencias = 0;
        for (Partida p : lista) {
            totalAsesinatos += p.getAsesinatos();
            totalMuertes += p.getMuertes();
            totalAsistencias += p.getAsistencias();
        }

        System.out.println(CYAN + "\n  --- Estadisticas del jugador ---" + RESET);
        System.out.println("  Mejor mes (mas victorias): " + VERDE + gestor.mesMasVictorias() + RESET);

        if (totalMuertes == 0) {
            System.out.println("  KDA general: " + VERDE + NEGRITA + "PERFECTO (0 muertes)" + RESET);
        } else {
            double kda = (double) (totalAsesinatos + totalAsistencias) / totalMuertes;
            System.out.printf("  KDA general: " + VERDE + "%.2f" + RESET + "%n", kda);
        }
        System.out.println();
    }

    /**
     * Lee una respuesta si/no del usuario y la traduce a boolean.
     */
    private boolean leerSiNo(String mensaje) {
        String r = Validador.leerNoVacio(reader, mensaje).trim().toLowerCase();
        return r.equals("s") || r.equals("si") || r.equals("1");
    }
}
