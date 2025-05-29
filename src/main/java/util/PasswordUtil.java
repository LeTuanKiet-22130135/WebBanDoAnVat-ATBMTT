package util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    // BCrypt cost factor (higher = more secure but slower)
    private static final int COST_FACTOR = 12;

    /**
     * Hash a password using BCrypt
     * 
     * @param password The plain text password to hash
     * @return The BCrypt hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(COST_FACTOR, password.toCharArray());
    }

    /**
     * Verify if a plain text password matches a BCrypt hashed password
     * 
     * @param password The plain text password to check
     * @param hashedPassword The BCrypt hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    public static boolean comparePassword(String password, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}
