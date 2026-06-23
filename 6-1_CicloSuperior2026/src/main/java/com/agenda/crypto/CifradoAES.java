package com.agenda.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Servicio de cifrado selectivo mediante AES (javax.crypto.Cipher).
 *
 * Responsabilidades:
 *  - Generar y persistir la clave AES en agenda.key (primera ejecución).
 *  - Cargar la clave existente en ejecuciones posteriores.
 *  - Exponer los métodos cifrar() y descifrar() para uso exclusivo
 *    del campo "Nota Privada".
 */
public class CifradoAES {

    private static final String ALGORITMO       = "AES";
    private static final String ARCHIVO_CLAVE   = "agenda.key";
    private static final int    BITS_CLAVE      = 128;

    private final SecretKey clave;

    /**
     * Inicializa el servicio: carga la clave existente o genera una nueva.
     */
    public CifradoAES() throws Exception {
        Path rutaClave = Paths.get(ARCHIVO_CLAVE);

        if (Files.exists(rutaClave)) {
            byte[] bytes = Files.readAllBytes(rutaClave);
            this.clave = new SecretKeySpec(bytes, ALGORITMO);
        } else {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITMO);
            kg.init(BITS_CLAVE);
            this.clave = kg.generateKey();
            Files.write(rutaClave, this.clave.getEncoded());
        }
    }

    /**
     * Cifra un texto en claro y devuelve su representación en Base64.
     *
     * @param textoClar Texto a cifrar (Nota Privada en plano).
     * @return Cadena Base64 con el contenido cifrado.
     */
    public String cifrar(String textoClar) throws Exception {
        if (textoClar == null || textoClar.isEmpty()) return "";
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        byte[] cifrado = cipher.doFinal(textoClar.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(cifrado);
    }

    /**
     * Descifra una cadena Base64 previamente generada por cifrar().
     *
     * @param textoCifrado Cadena Base64 proveniente del archivo.
     * @return Texto original en claro.
     */
    public String descifrar(String textoCifrado) throws Exception {
        if (textoCifrado == null || textoCifrado.isEmpty()) return "";
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, clave);
        byte[] bytes = Base64.getDecoder().decode(textoCifrado);
        return new String(cipher.doFinal(bytes), "UTF-8");
    }

    /**
     * Informa si la clave fue creada en esta ejecución o ya existía.
     */
    public boolean esClavaNueva() {
        return !Paths.get(ARCHIVO_CLAVE).toFile().exists();
    }
}
