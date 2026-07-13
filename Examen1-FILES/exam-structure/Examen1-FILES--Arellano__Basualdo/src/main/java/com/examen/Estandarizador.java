package com.examen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Se encarga de leer el archivo original juegos.dat y reestructurarlo
 * a un formato estandarizado CSV con separador " ; ".
 * Luego elimina el archivo original.
 * <p>
 * El archivo original usa '+' como separador.
 * Ejemplo de linea original: 20/04+1+0+1+4+7+5
 * Ejemplo de linea estandarizada: 20/04 ; 1 ; 0 ; 1 ; 4 ; 7 ; 5
 */
public class Estandarizador {

    private static final String SEPARADOR = " ; ";

    /**
     * Lee el archivo original (juegos.dat), reemplaza el caracter '+'
     * por el separador " ; ", guarda el resultado como juegos.csv
     * y elimina el archivo original juegos.dat.
     * <p>
     * Si ocurre cualquier error, se debe capturar la excepcion y
     * registrarla en crash.log mediante LogManager.
     * El error NO debe mostrarse por consola.
     *
     * @param rutaOriginal ruta completa del archivo juegos.dat a procesar
     */
    public void estandarizar(String rutaOriginal) {
        File original = new File(rutaOriginal);

        LogManager.registrarInfo("estandarizar() -> working dir: " + System.getProperty("user.dir"));
        LogManager.registrarInfo("estandarizar() -> buscando .dat en: " + original.getAbsolutePath());
        LogManager.registrarInfo("estandarizar() -> existe el .dat? " + original.exists());

        if (!original.exists()) {
            LogManager.registrarInfo("estandarizar() -> el .dat NO existe, se aborta la estandarizacion.");
            return;
        }

      
        String rutaCsv = derivarRutaCsv(rutaOriginal);
        LogManager.registrarInfo("estandarizar() -> se creara el csv en: " + new File(rutaCsv).getAbsolutePath());

        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            fr = new FileReader(original);
            br = new BufferedReader(fr);

            fw = new FileWriter(rutaCsv, false); 
            pw = new PrintWriter(fw);

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
     
                String estandarizada = linea.replace("+", SEPARADOR);
                pw.println(estandarizada);
            }
            pw.flush();

        } catch (IOException ex) {
            LogManager.registrarError("Error al estandarizar el archivo: " + rutaOriginal, ex);
            return;
        } finally {
            if (pw != null) pw.close();
            try {
                if (fw != null) fw.close();
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                LogManager.registrarError("Error al cerrar flujos en estandarizar()", ex);
            }
        }

        try {
            if (original.exists()) {
                original.delete();
            }
        } catch (Exception ex) {
            LogManager.registrarError("Error al eliminar el archivo original: " + rutaOriginal, ex);
        }
    }

    /**
     * Deriva la ruta del CSV a partir de la del .dat, cambiando la extension.
     * Ej: /ruta/juegos.dat -> /ruta/juegos.csv
     */
    private String derivarRutaCsv(String rutaOriginal) {
        int punto = rutaOriginal.lastIndexOf('.');
        if (punto == -1) {
            return rutaOriginal + ".csv";
        }
        return rutaOriginal.substring(0, punto) + ".csv";
    }

    /**
     * Genera una nueva clave simetrica AES de 256 bits.
     * Este metodo solo debe llamarse la primera vez que se ejecuta el programa.
     */
    public static SecretKey generarClaveAES() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(256);
            return gen.generateKey();
        } catch (Exception ex) {
            LogManager.registrarError("Error al generar la clave AES", ex);
        }
        return null;
    }

    /**
     * Convierte la clave AES a texto (Base64) y la guarda en un archivo.
     * @param clave La clave generada que se desea guardar.
     * @param rutaArchivo Ruta donde se guardara (ej: "AES.key").
     */
    public static void guardarClave(SecretKey clave, String rutaArchivo) {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {

            String claveEnTexto = Base64.getEncoder().encodeToString(clave.getEncoded());

            fw = new FileWriter(rutaArchivo, false);
            pw = new PrintWriter(fw);
            pw.print(claveEnTexto);
            pw.flush();
        } catch (Exception ex) {
            LogManager.registrarError("Error al guardar la clave en el archivo: " + rutaArchivo, ex);
        } finally {
            if (pw != null) pw.close();
            try {
                if (fw != null) fw.close();
            } catch (IOException ex) {
                LogManager.registrarError("Error al cerrar el flujo en guardarClave()", ex);
            }
        }
    }

    /**
     * Lee el archivo de texto y reconstruye la clave AES para poder usarla.
     * @param rutaArchivo Ruta del archivo donde esta guardada la clave (ej: "AES.key").
     * @return El objeto SecretKey reconstruido, o null si falla.
     */
    public static SecretKey recuperarClave(String rutaArchivo) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
          
            fr = new FileReader(rutaArchivo);
            br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            String textoLeido = sb.toString().trim();

            byte[] bytesClave = Base64.getDecoder().decode(textoLeido);

         
            return new SecretKeySpec(bytesClave, 0, bytesClave.length, "AES");
        } catch (Exception ex) {
            LogManager.registrarError("Error al recuperar la clave del archivo: " + rutaArchivo, ex);
        } finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (IOException ex) {
                LogManager.registrarError("Error al cerrar el flujo en recuperarClave()", ex);
            }
        }
        return null;
    }

    /**
     * Encripta un texto (ej: el contenido del CSV) usando la clave proporcionada.
     * Se usa AES/CBC/PKCS5Padding con un IV aleatorio que se antepone al texto
     * cifrado, y el resultado se devuelve en Base64.
     *
     * @param datos El texto plano a encriptar.
     * @param clave La clave AES.
     * @return El texto encriptado convertido a formato Base64 para guardarlo seguro.
     */
    public static String encriptar(String datos, SecretKey clave) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, clave, ivSpec);
            byte[] cifrado = cipher.doFinal(datos.getBytes(StandardCharsets.UTF_8));

            byte[] resultado = new byte[iv.length + cifrado.length];
            System.arraycopy(iv, 0, resultado, 0, iv.length);
            System.arraycopy(cifrado, 0, resultado, iv.length, cifrado.length);

            return Base64.getEncoder().encodeToString(resultado);
        } catch (Exception ex) {
            LogManager.registrarError("Error al encriptar los datos", ex);
        }
        return null;
    }

    /**
     * Desencripta un texto en Base64 para recuperar los datos originales.
     * @param datosEncriptados El texto encriptado (leido del archivo).
     * @param clave La clave AES.
     * @return El texto plano original (contenido del CSV), o null si falla.
     */
    public static String desencriptar(String datosEncriptados, SecretKey clave) {
        try {
            byte[] todo = Base64.getDecoder().decode(datosEncriptados);

            byte[] iv = new byte[16];
            byte[] cifrado = new byte[todo.length - 16];
            System.arraycopy(todo, 0, iv, 0, 16);
            System.arraycopy(todo, 16, cifrado, 0, cifrado.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, clave, new IvParameterSpec(iv));

            byte[] plano = cipher.doFinal(cifrado);
            return new String(plano, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            LogManager.registrarError("Error al desencriptar los datos", ex);
        }
        return null;
    }
}

