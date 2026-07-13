package com.examen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sistema de registro de errores que escribe en el archivo crash.log.
 * <p>
 * Ningun error debe mostrarse por consola, todos deben registrarse aqui.
 * Utiliza FileWriter y PrintWriter para escribir en el archivo.
 */
public class LogManager {

    private static final String RUTA_LOG = "crash.log";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Inicializa el sistema de log.
     * Crea o verifica que el archivo crash.log existe y esta listo para escribir.
     * Este metodo debe llamarse una unica vez al iniciar el programa.
     */
    public static void inicializar() {
        try {
            File f = new File(RUTA_LOG);
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (IOException ex) {
           
        }
    }

    /**
     * Registra un error en crash.log.
     * Escribe la fecha/hora, el mensaje de contexto y el stack trace de la excepcion.
     *
     * @param mensaje descripcion del contexto donde ocurrio el error
     * @param e       la excepcion capturada
     */
    public static void registrarError(String mensaje, Exception e) {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(RUTA_LOG, true); 
            pw = new PrintWriter(fw);

            pw.println("[" + LocalDateTime.now().format(FMT) + "] ERROR: " + mensaje);
            if (e != null) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                pw.println(sw.toString());
            }
            pw.flush();
        } catch (IOException ex) {
            
        } finally {
            if (pw != null) pw.close();
            try {
                if (fw != null) fw.close();
            } catch (IOException ex) {
          
            }
        }
    }

    /**
     * Registra un mensaje informativo en crash.log.
     *
     * @param mensaje mensaje informativo a registrar
     */
    public static void registrarInfo(String mensaje) {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(RUTA_LOG, true);
            pw = new PrintWriter(fw);

            pw.println("[" + LocalDateTime.now().format(FMT) + "] INFO: " + mensaje);
            pw.flush();
        } catch (IOException ex) {
       
        } finally {
            if (pw != null) pw.close();
            try {
                if (fw != null) fw.close();
            } catch (IOException ex) {
 
            }
        }
    }
}

