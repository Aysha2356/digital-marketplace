package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing passwords using SHA-256.
 * Used in Module 1 (Registration) and Module 2 (Login).
 *
 * Note: For production, consider BCrypt (via external library) for stronger hashing.
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param plainText The raw password entered by the user
     * @return Hexadecimal SHA-256 hash string
     */
    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if a plain-text password matches a stored hash.
     *
     * @param plainText   Raw password from the login form
     * @param storedHash  Hash stored in the database
     * @return true if they match
     */
    public static boolean verify(String plainText, String storedHash) {
        return hash(plainText).equals(storedHash);
    }
}

