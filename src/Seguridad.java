import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/** Cifrado simetrico AES-256/CBC con IV aleatorio. Clave compartida entre Cliente y Servidor. */
public final class Seguridad {

    private static final String CLAVE_COMPARTIDA = "TP-Sockets-Ecommerce-2026";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final SecretKeySpec CLAVE;

    static {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(CLAVE_COMPARTIDA.getBytes(StandardCharsets.UTF_8));
            CLAVE = new SecretKeySpec(hash, "AES");
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Seguridad() {}

    /** Devuelve Base64(IV || AES(texto)). */
    public static String cifrar(String texto) throws GeneralSecurityException {
        byte[] iv = new byte[16];
        RANDOM.nextBytes(iv);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, CLAVE, new IvParameterSpec(iv));
        byte[] datos = c.doFinal(texto.getBytes(StandardCharsets.UTF_8));
        byte[] salida = new byte[iv.length + datos.length];
        System.arraycopy(iv, 0, salida, 0, iv.length);
        System.arraycopy(datos, 0, salida, iv.length, datos.length);
        return Base64.getEncoder().encodeToString(salida);
    }

    public static String descifrar(String base64) throws GeneralSecurityException {
        byte[] todo = Base64.getDecoder().decode(base64);
        byte[] iv = Arrays.copyOfRange(todo, 0, 16);
        byte[] datos = Arrays.copyOfRange(todo, 16, todo.length);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, CLAVE, new IvParameterSpec(iv));
        return new String(c.doFinal(datos), StandardCharsets.UTF_8);
    }
}
