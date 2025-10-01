package cms.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A simple, centralized logging utility for the application.
 * Errors are logged to a file named 'app-errors.log'.
 */
public class LoggerUtil {

    private static final Logger logger = Logger.getLogger("ClinicManagementSystemLogger");
    private static FileHandler fileHandler;

    static {
        try {
            // Configure the logger to write to a file
            // %h/app-errors.log will place the log in the user's home directory
            fileHandler = new FileHandler("clinic-app-errors.log", true); // 'true' for appending
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.WARNING); // Log warnings and severe errors

        } catch (IOException e) {
            // If the logger itself fails, print to console as a last resort
            System.err.println("CRITICAL: Could not initialize logger.");
            e.printStackTrace();
        }
    }

    /**
     * Logs a severe error message along with the exception's stack trace.
     * @param message A descriptive message about the context of the error.
     * @param thrown  The exception that was caught.
     */
    public static void logError(String message, Throwable thrown) {
        logger.log(Level.SEVERE, message, thrown);
    }
    
    /**
     * Logs a warning message.
     * @param message The warning message.
     */
    public static void logWarning(String message) {
        logger.log(Level.WARNING, message);
    }
}