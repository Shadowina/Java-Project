package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordHasher {
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";

    public static String hashPassword(String password) {
        try {
            // Simple SHA-256 hash for compatibility with existing passwords
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Generate hash of the input password
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashedBytes = md.digest(password.getBytes());
            String newHash = Base64.getEncoder().encodeToString(hashedBytes);

            // Compare with stored hash
            return storedHash.equals(newHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}