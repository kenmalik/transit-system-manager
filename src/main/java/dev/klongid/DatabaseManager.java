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

            String createBusTableSQL = """
                CREATE TABLE IF NOT EXISTS Bus (
                    BusID INTEGER PRIMARY KEY,
                    Model TEXT NOT NULL,
                    Year INTEGER NOT NULL
                )
                """;

            String createDriverTableSQL = """
                CREATE TABLE IF NOT EXISTS Driver (
                    DriverName TEXT PRIMARY KEY,
                    DriverTelephoneNumber TEXT NOT NULL
                )
                """;

            String createStopTableSQL = """
                CREATE TABLE IF NOT EXISTS Stop (
                    StopNumber INTEGER PRIMARY KEY,
                    StopAddress TEXT NOT NULL
                )
                """;

            stmt.execute(createBusTableSQL);
            stmt.execute(createDriverTableSQL);
            stmt.execute(createStopTableSQL);
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

    public static void clearDriverTable() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM Driver");
            System.out.println("Driver table cleared.");

        } catch (SQLException e) {
            System.err.println("Error clearing Driver table: " + e.getMessage());
        }
    }

    public static void clearStopTable() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM Stop");
            System.out.println("Stop table cleared.");

        } catch (SQLException e) {
            System.err.println("Error clearing Stop table: " + e.getMessage());
        }
    }
}
