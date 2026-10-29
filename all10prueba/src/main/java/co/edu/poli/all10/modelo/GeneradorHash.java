package co.edu.poli.all10.modelo;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Genera la sal y aplica el hash (PBKDF2 con HMAC-SHA256) a las contraseñas.
 * La contraseña en texto plano nunca se guarda; solo se guardan hash y sal.
 */
public class GeneradorHash {

    private static final int ITERACIONES = 600_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;

    /** Devuelve una sal aleatoria nueva, codificada en Base64. */
    public String generarSal() {
        byte[] sal = new byte[BYTES_SAL];
        new SecureRandom().nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal);
    }

    /** Devuelve el hash de la contraseña con la sal dada, codificado en Base64. */
    public String hashear(String contrasena, String sal) {
        PBEKeySpec spec = new PBEKeySpec(
                contrasena.toCharArray(),
                Base64.getDecoder().decode(sal),
                ITERACIONES,
                BITS_HASH);
        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = fabrica.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        } finally {
            spec.clearPassword();
        }
    }
}