package com.examen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestiona la coleccion de partidas cargadas desde el archivo CSV estandarizado.
 * Provee metodos para cargar, mostrar, analizar y guardar los datos.
 */
public class GestorPartidas {


    private static final String ENCABEZADOS =
            "fecha ; gano ; primerTorreta ; primeraSangre ; asesinatos ; muertes ; asistencias";


    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";   
    private static final String VERDE = "\u001B[32m";  
    private static final String BLANCO = "\u001B[97m"; 
    private static final String AMARILLO = "\u001B[33m";
    private static final String ROJO = "\u001B[31m";

    private ArrayList<Partida> partidas;

    /**
     * Crea un gestor vacio. Inicializa la lista de partidas.
     */
    public GestorPartidas() {
        this.partidas = new ArrayList<>();
    }

    /**
     * Carga los datos desde el archivo CSV estandarizado.
     * Lee cada linea, la parsea y crea objetos Partida.
     * La primera linea (encabezados) debe ignorarse.
     * <p>
     * Formato esperado: dd/MM ; 1 ; 0 ; 1 ; 4 ; 7 ; 5
     * <p>
     * Los errores deben registrarse en crash.log sin mostrar en consola.
     *
     * @param rutaCsv ruta del archivo CSV a cargar
     */
    public void cargar(String rutaCsv) {
        File f = new File(rutaCsv);
        if (!f.exists()) {
            return;
        }

        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) {         
                    primera = false;
                    continue;
                }
                if (linea.trim().isEmpty()) {
                    continue;
                }

                try {
                    Partida p = parsearLinea(linea);
                    if (p != null) {
                        partidas.add(p);
                    }
                } catch (Exception ex) {
                    LogManager.registrarError("Linea corrupta al cargar CSV: " + linea, ex);
                }
            }
        } catch (IOException ex) {
            LogManager.registrarError("Error al cargar el archivo CSV: " + rutaCsv, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                LogManager.registrarError("Error al cerrar flujos en cargar()", ex);
            }
        }
    }

    /**
     * Convierte una linea del CSV en un objeto Partida.
     * Soporta el separador ' ; ' (con o sin espacios).
     */
    private Partida parsearLinea(String linea) {
        String[] campos = linea.split(";");
        if (campos.length < 7) {
            return null;
        }

        String fecha = campos[0].trim();
        boolean gano = campos[1].trim().equals("1");
        boolean primerTorreta = campos[2].trim().equals("1");
        boolean primeraSangre = campos[3].trim().equals("1");
        int asesinatos = Integer.parseInt(campos[4].trim());
        int muertes = Integer.parseInt(campos[5].trim());
        int asistencias = Integer.parseInt(campos[6].trim());

        return new Partida(fecha, gano, primerTorreta, primeraSangre,
                asesinatos, muertes, asistencias);
    }

    /**
     * Muestra todas las partidas cargadas en consola de forma ordenada,
     * en formato de tabla, con encabezados coloreados y filas alternando
     * entre verde y blanco. No se muestran los punto y coma.
     * Si no hay partidas, muestra un mensaje indicandolo.
     */
    public void mostrarTodos() {
        if (partidas.isEmpty()) {
            System.out.println(AMARILLO + "\n  No hay partidas cargadas para mostrar.\n" + RESET);
            return;
        }

        String formato = "%-4s %-8s %-6s %-8s %-8s %-10s %-8s %-11s%n";

        System.out.println();
        System.out.printf(CYAN + formato + RESET,
                "#", "Fecha", "Gano", "1raTorre", "1raSangre", "Asesinatos", "Muertes", "Asistencias");
        System.out.println(CYAN + "  ------------------------------------------------------------------------" + RESET);

        for (int i = 0; i < partidas.size(); i++) {
            Partida p = partidas.get(i);
            String color = (i % 2 == 0) ? VERDE : BLANCO;

            System.out.printf(color + formato + RESET,
                    String.valueOf(i),
                    p.getFecha(),
                    (p.isGano() ? "Si" : "No"),
                    (p.isPrimerTorreta() ? "Si" : "No"),
                    (p.isPrimeraSangre() ? "Si" : "No"),
                    String.valueOf(p.getAsesinatos()),
                    String.valueOf(p.getMuertes()),
                    String.valueOf(p.getAsistencias()));
        }
        System.out.println();
    }

    /**
     * Analiza las partidas y determina en que mes hubo mas victorias.
     * La fecha tiene formato dd/MM (el mes esta despues de la barra).
     * <p>
     * En caso de empate, devuelve cualquiera de los meses.
     *
     * @return String con numero de mes (dos digitos, ej: "04").
     *         Si no hay partidas, retorna "SIN DATOS".
     */
    public String mesMasVictorias() {
        if (partidas.isEmpty()) {
            return "SIN DATOS";
        }

        Map<String, Integer> victoriasPorMes = new HashMap<>();

        for (Partida p : partidas) {
            if (!p.isGano()) {
                continue;
            }
            String mes = extraerMes(p.getFecha());
            if (mes == null) {
                continue;
            }
            victoriasPorMes.put(mes, victoriasPorMes.getOrDefault(mes, 0) + 1);
        }

        if (victoriasPorMes.isEmpty()) {
            return "SIN DATOS";
        }

        String mejorMes = "SIN DATOS";
        int maximo = -1;
        for (Map.Entry<String, Integer> e : victoriasPorMes.entrySet()) {
            if (e.getValue() > maximo) {
                maximo = e.getValue();
                mejorMes = e.getKey();
            }
        }
        return mejorMes;
    }

    /**
     * Extrae el mes (dos digitos) de una fecha con formato dd/MM.
     */
    private String extraerMes(String fecha) {
        if (fecha == null) {
            return null;
        }
        String[] partes = fecha.split("/");
        if (partes.length < 2) {
            return null;
        }
        return partes[1].trim();
    }

    /**
     * Guarda todas las partidas en el archivo CSV.
     * Primero escribe la linea de encabezados y luego cada partida.
     * Si el archivo ya existe, se sobrescribe.
     *
     * @param rutaCsv ruta del archivo CSV donde guardar
     */
    public void guardar(String rutaCsv) {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(rutaCsv, false); 
            pw = new PrintWriter(fw);

            pw.println(ENCABEZADOS);
            for (Partida p : partidas) {
                pw.println(p.toString());
            }
            pw.flush();
        } catch (IOException ex) {
            LogManager.registrarError("Error al guardar el archivo CSV: " + rutaCsv, ex);
        } finally {
            if (pw != null) pw.close();
            try {
                if (fw != null) fw.close();
            } catch (IOException ex) {
                LogManager.registrarError("Error al cerrar flujos en guardar()", ex);
            }
        }
    }

    /**
     * Agrega una partida ya construida a la lista en memoria.
     *
     * @param p la partida a agregar
     */
    public void agregar(Partida p) {
        if (p != null) {
            partidas.add(p);
        }
    }

    /**
     * Elimina una partida de la lista segun el indice indicado.
     * Valida que el indice sea valido antes de eliminar.
     * Si el indice es invalido, muestra un mensaje de error.
     *
     * @param indice posicion de la partida a eliminar (base 0)
     */
    public void eliminar(int indice) {
        if (indice < 0 || indice >= partidas.size()) {
            System.out.println(ROJO + "  [!] Indice invalido. No existe una partida en esa posicion." + RESET);
            return;
        }
        partidas.remove(indice);
        System.out.println(VERDE + "  [OK] Partida eliminada correctamente." + RESET);
    }

    /**
     * @return cantidad de partidas cargadas
     */
    public int cantidadPartidas() {
        return partidas.size();
    }

    /**
     * @return la lista interna de partidas
     */
    public List<Partida> getPartidas() {
        return partidas;
    }
}
