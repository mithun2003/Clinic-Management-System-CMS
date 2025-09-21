package cms.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Create a hash
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Check entered password against DB hash
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}