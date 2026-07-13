package com.examen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.crypto.SecretKey;

/**
 * Punto de entrada principal del programa.
 * <p>
 * Orquesta la ejecucion:
 * 1. Inicializa LogManager
 * 2. Estandariza juegos.dat -> juegos.csv (si existe)
 * 3. Encripta juegos.csv -> juegos_encriptado.csv y elimina juegos.csv
 *    (o desencripta en memoria juegos_encriptado.csv si el .dat ya no existe)
 * 4. Carga las partidas en memoria en el GestorPartidas
 * 5. Inicia el menu interactivo
 * 6. Al salir, re-encripta el historial actualizado
 */
public class Main {

    private static final String RUTA_DAT = "juegos.dat";
    private static final String RUTA_CSV = "juegos.csv";
    private static final String RUTA_ENCRIPTADO = "juegos_encriptado.csv";
    private static final String RUTA_CLAVE = "AES.key";

    public static void main(String[] args) {

        LogManager.inicializar();
        SecretKey clave = obtenerClave();
        GestorPartidas gestor = new GestorPartidas();

        File dat = new File(RUTA_DAT);
        File encriptado = new File(RUTA_ENCRIPTADO);

        if (dat.exists()) {
            Estandarizador est = new Estandarizador();
            est.estandarizar(RUTA_DAT);
            gestor.cargar(RUTA_CSV);
            encriptarHistorial(gestor, clave);
            eliminarSiExiste(RUTA_CSV);

        } else if (encriptado.exists()) {

            cargarDesdeEncriptado(gestor, clave);

        } else {
            LogManager.registrarInfo("No se encontro juegos.dat ni juegos_encriptado.csv. Se inicia vacio.");
        }


        Menu menu = new Menu(gestor, RUTA_CSV);
        menu.iniciar();

        encriptarHistorial(gestor, clave);
        eliminarSiExiste(RUTA_CSV);     
    }

    /**
     * Recupera la clave AES del archivo AES.key; si no existe, genera una nueva
     * y la persiste.
     */
    private static SecretKey obtenerClave() {
        File archivoClave = new File(RUTA_CLAVE);
        if (archivoClave.exists()) {
            SecretKey k = Estandarizador.recuperarClave(RUTA_CLAVE);
            if (k != null) {
                return k;
            }
        }
        SecretKey nueva = Estandarizador.generarClaveAES();
        if (nueva != null) {
            Estandarizador.guardarClave(nueva, RUTA_CLAVE);
        }
        return nueva;
    }

    /**
     * Serializa las partidas en memoria (via gestor.guardar en un temporal),
     * encripta el contenido y lo escribe en juegos_encriptado.csv.
     */
    private static void encriptarHistorial(GestorPartidas gestor, SecretKey clave) {
        if (clave == null) {
            LogManager.registrarError("No hay clave AES disponible para encriptar.", null);
            return;
        }
        String temp = ".tmp_guardar.csv";
        gestor.guardar(temp);                      
        String contenido = leerArchivoCompleto(temp);
        eliminarSiExiste(temp);
        if (contenido == null) {
            return;
        }
        String cifrado = Estandarizador.encriptar(contenido, clave);
        if (cifrado == null) {
            return;
        }
        escribirArchivo(RUTA_ENCRIPTADO, cifrado);
    }

    /**
     * Lee juegos_encriptado.csv, lo desencripta en memoria y carga las partidas.
     */
    private static void cargarDesdeEncriptado(GestorPartidas gestor, SecretKey clave) {
        if (clave == null) {
            LogManager.registrarError("No hay clave AES para desencriptar el historial.", null);
            return;
        }
        String cifrado = leerArchivoCompleto(RUTA_ENCRIPTADO);
        if (cifrado == null) {
            return;
        }
        String plano = Estandarizador.desencriptar(cifrado.trim(), clave);
        if (plano == null) {
            return;
        }
        String temp = ".tmp_historial.csv";
        escribirArchivo(temp, plano);
        gestor.cargar(temp);
        eliminarSiExiste(temp);
    }



    private static String leerArchivoCompleto(String ruta) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(ruta);
            br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
            return sb.toString();
        } catch (Exception ex) {
            LogManager.registrarError("Error al leer el archivo: " + ruta, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (Exception ex) {
                LogManager.registrarError("Error al cerrar flujos en leerArchivoCompleto()", ex);
            }
        }
        return null;
    }

    private static void escribirArchivo(String ruta, String contenido) {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(ruta, false);
            pw = new PrintWriter(fw);
            pw.print(contenido);
            pw.flush();
        } catch (Exception ex) {
            LogManager.registrarError("Error al escribir el archivo: " + ruta, ex);
        } finally {
            if (pw != null) pw.close();
            try {
                if (fw != null) fw.close();
            } catch (Exception ex) {
                LogManager.registrarError("Error al cerrar flujos en escribirArchivo()", ex);
            }
        }
    }

    private static void eliminarSiExiste(String ruta) {
        try {
            File f = new File(ruta);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception ex) {
            LogManager.registrarError("Error al eliminar el archivo: " + ruta, ex);
        }
    }
}
