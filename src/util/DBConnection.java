package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing MySQL database connections using JDBC.
 * Used across all modules (Registration, Login, Upload, View, Download).
 */
public class DBConnection {

   private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";
private static final String URL      = "jdbc:mysql://localhost:3306/digital_marketplace?useSSL=false&serverTimezone=UTC";
private static final String USERNAME = "marketplace_user";
private static final String PASSWORD = "1234"; // Change before deployment

    // Prevent instantiation
    private DBConnection() {}

    /**
     * Returns a live JDBC connection to the MySQL database.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java.jar to classpath.", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Safely closes a connection without throwing checked exceptions.
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DBConnection] Failed to close connection: " + e.getMessage());
            }
        }
    }
}
