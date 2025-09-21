package cms.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/clinicdb"; 
    // SQLite in your schema because you used AUTOINCREMENT instead of MySQL AUTO_INCREMENT
    // If MySQL: "jdbc:mysql://localhost:3306/clinicdb"
    
    private static final String USER = "root"; // remove for SQLite
    private static final String PASSWORD = "password"; // remove for SQLite

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load Driver
                 Class.forName("com.mysql.cj.jdbc.Driver"); // For MySQL
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.out.println("JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }
}