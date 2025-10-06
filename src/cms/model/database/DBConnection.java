package cms.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/clinicdb";

    private static final String USER = "root";
    private static final String PASSWORD = "password";

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // For modern JDBC drivers, Class.forName() is optional.
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (Exception e) {
                System.out.println("JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }
}