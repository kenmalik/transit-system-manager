package dev.klongid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:app.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS Bus (
                    BusID INTEGER PRIMARY KEY,
                    Model TEXT NOT NULL,
                    Year INTEGER NOT NULL
                )
                """;

            stmt.execute(createTableSQL);
            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void clearBusTable() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM Bus");
            System.out.println("Bus table cleared.");

        } catch (SQLException e) {
            System.err.println("Error clearing Bus table: " + e.getMessage());
        }
    }
}
