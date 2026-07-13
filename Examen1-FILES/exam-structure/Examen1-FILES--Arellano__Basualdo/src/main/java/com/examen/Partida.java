package com.examen;

/**
 * Representa una partida de League of Legends registrada en el archivo.
 * <p>
 * Cada partida contiene fecha, resultado, eventos importantes
 * y estadisticas del jugador.
 * <p>
 * El orden de los campos en el archivo original es:
 * fecha, gano, primerTorreta, primeraSangre, asesinatos, muertes, asistencias
 * <p>
 * Ejemplo de linea: 20/04+1+0+1+4+7+5
 */
public class Partida {

    private String fecha;
    private boolean gano;
    private boolean primerTorreta;
    private boolean primeraSangre;
    private int asesinatos;
    private int muertes;
    private int asistencias;

    /**
     * Constructor que recibe todos los datos de una partida.
     *
     * @param fecha         fecha en formato dd/MM
     * @param gano          true si el equipo gano, false si perdio
     * @param primerTorreta true si derribo la primer torreta
     * @param primeraSangre true si obtuvo la primera sangre
     * @param asesinatos    cantidad de asesinatos realizados
     * @param muertes       cantidad de muertes sufridas
     * @param asistencias   cantidad de asistencias realizadas
     */
    public Partida(String fecha, boolean gano, boolean primerTorreta,
                   boolean primeraSangre, int asesinatos, int muertes, int asistencias) {
        this.fecha = fecha;
        this.gano = gano;
        this.primerTorreta = primerTorreta;
        this.primeraSangre = primeraSangre;
        this.asesinatos = asesinatos;
        this.muertes = muertes;
        this.asistencias = asistencias;
    }

    /**
     * @return fecha en formato dd/MM
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @return true si el equipo gano la partida
     */
    public boolean isGano() {
        return gano;
    }

    /**
     * @return true si derribo la primer torreta
     */
    public boolean isPrimerTorreta() {
        return primerTorreta;
    }

    /**
     * @return true si obtuvo la primera sangre
     */
    public boolean isPrimeraSangre() {
        return primeraSangre;
    }

    /**
     * @return cantidad de asesinatos
     */
    public int getAsesinatos() {
        return asesinatos;
    }

    /**
     * @return cantidad de muertes
     */
    public int getMuertes() {
        return muertes;
    }

    /**
     * @return cantidad de asistencias
     */
    public int getAsistencias() {
        return asistencias;
    }

    /**
     * Devuelve una representacion en formato legible de la partida
     * usando " ; " como separador.
     *
     * @return String con los datos formateados para guardar en CSV
     */
    @Override
    public String toString() {
        return fecha + " ; " + (gano ? 1 : 0) + " ; " + (primerTorreta ? 1 : 0) + " ; "
                + (primeraSangre ? 1 : 0) + " ; " + asesinatos + " ; " + muertes + " ; " + asistencias;
    }
}
