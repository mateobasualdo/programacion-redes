package com.agenda.storage;

import com.agenda.crypto.CifradoAES;
import com.agenda.model.Contacto;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de persistencia segura mediante archivos.
 *
 * Regla de oro: NUNCA se escribe directamente sobre agenda.dat.
 * El flujo de escritura es siempre:
 *   1. Escribir la colección completa en agenda.tmp.
 *   2. Reemplazar atómicamente agenda.tmp → agenda.dat.
 *
 * Formato de cada línea en disco:
 *   nombre;telefono;email;[nota_privada_en_Base64_AES]
 *
 * Los campos básicos viajan en texto plano.
 * Sólo la Nota Privada se almacena cifrada (Base64 AES).
 */
public class Persistencia {

    private static final String ARCHIVO_PRINCIPAL = "agenda.dat";
    private static final String ARCHIVO_TEMPORAL  = "agenda.tmp";
    private static final String SEPARADOR         = ";";

    private final CifradoAES cifrado;

    public Persistencia(CifradoAES cifrado) {
        this.cifrado = cifrado;
    }

    // ── Escritura segura ──────────────────────────────────────────────────────

    /**
     * Persiste toda la colección en disco de forma segura.
     * Proceso: escritura en temporal → reemplazo atómico.
     */
    public void guardar(List<Contacto> contactos) throws Exception {
        Path temporal  = Paths.get(ARCHIVO_TEMPORAL);
        Path principal = Paths.get(ARCHIVO_PRINCIPAL);

        // Paso 1: escribir en archivo temporal
        try (BufferedWriter bw = Files.newBufferedWriter(temporal)) {
            for (Contacto c : contactos) {
                String notaCifrada = cifrado.cifrar(c.getNotaPrivada());
                bw.write(
                    escapar(c.getNombre())   + SEPARADOR +
                    escapar(c.getTelefono()) + SEPARADOR +
                    escapar(c.getEmail())    + SEPARADOR +
                    notaCifrada
                );
                bw.newLine();
            }
        }

        // Paso 2: reemplazo atómico temporal → principal
        Files.move(temporal, principal,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }

    // ── Lectura ───────────────────────────────────────────────────────────────

    /**
     * Carga los contactos desde agenda.dat a una lista en memoria.
     * La Nota Privada es descifrada en tiempo real durante la lectura.
     *
     * @return Lista de contactos listos para usar en memoria.
     */
    public List<Contacto> cargar() throws Exception {
        List<Contacto> lista = new ArrayList<>();
        Path archivo = Paths.get(ARCHIVO_PRINCIPAL);

        if (!Files.exists(archivo)) return lista;

        List<String> lineas = Files.readAllLines(archivo);
        for (String linea : lineas) {
            if (linea.isBlank()) continue;
            String[] partes = linea.split(SEPARADOR, 4);
            if (partes.length < 4) continue;

            String nombre   = desescapar(partes[0]);
            String telefono = desescapar(partes[1]);
            String email    = desescapar(partes[2]);
            String nota     = cifrado.descifrar(partes[3]);

            lista.add(new Contacto(nombre, telefono, email, nota));
        }

        return lista;
    }

    /**
     * Indica si existe un archivo de agenda guardado.
     */
    public boolean existeArchivo() {
        return Files.exists(Paths.get(ARCHIVO_PRINCIPAL));
    }

    // ── Escape de caracteres especiales ───────────────────────────────────────

    private String escapar(String s) {
        return s.replace("\\", "\\\\")
                .replace(";",  "\\;")
                .replace("\n", "\\n");
    }

    private String desescapar(String s) {
        return s.replace("\\n",  "\n")
                .replace("\\;",  ";")
                .replace("\\\\", "\\");
    }
}
