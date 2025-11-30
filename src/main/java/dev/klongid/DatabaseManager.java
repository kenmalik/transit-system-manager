package dev.klongid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:app.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        // Enable foreign key constraints in SQLite
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
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

            String createTripTableSQL = """
                    CREATE TABLE IF NOT EXISTS Trip (
                        TripNumber INTEGER PRIMARY KEY,
                        StartLocationName TEXT NOT NULL,
                        DestinationName TEXT NOT NULL
                    )
                    """;

            String createTripOfferingTableSQL = """
                    CREATE TABLE IF NOT EXISTS TripOffering (
                        TripNumber INTEGER NOT NULL,
                        Date TEXT NOT NULL,
                        ScheduledStartTime TEXT NOT NULL,
                        ScheduledArrivalTime TEXT NOT NULL,
                        DriverName TEXT,
                        BusID INTEGER,
                        PRIMARY KEY (TripNumber, Date, ScheduledStartTime),
                        FOREIGN KEY (TripNumber) REFERENCES Trip(TripNumber) ON DELETE CASCADE,
                        FOREIGN KEY (DriverName) REFERENCES Driver(DriverName),
                        FOREIGN KEY (BusID) REFERENCES Bus(BusID)
                    )
                    """;

            String createTripStopInfoTableSQL = """
                    CREATE TABLE IF NOT EXISTS TripStopInfo (
                        TripNumber INTEGER NOT NULL,
                        StopNumber INTEGER NOT NULL,
                        SequenceNumber INTEGER NOT NULL,
                        DrivingTime INTEGER NOT NULL,
                        PRIMARY KEY (TripNumber, StopNumber),
                        FOREIGN KEY (TripNumber) REFERENCES Trip(TripNumber) ON DELETE CASCADE,
                        FOREIGN KEY (StopNumber) REFERENCES Stop(StopNumber) ON DELETE CASCADE
                    )
                    """;

            String createActualTripStopInfoTableSQL = """
                    CREATE TABLE IF NOT EXISTS ActualTripStopInfo (
                        TripNumber INTEGER NOT NULL,
                        Date TEXT NOT NULL,
                        ScheduledStartTime TEXT NOT NULL,
                        StopNumber INTEGER NOT NULL,
                        ScheduledArrivalTime TEXT,
                        ActualStartTime TEXT,
                        ActualArrivalTime TEXT,
                        NumberOfPassengersIn INTEGER,
                        NumberOfPassengersOut INTEGER,
                        PRIMARY KEY (TripNumber, Date, ScheduledStartTime, StopNumber),
                        FOREIGN KEY (TripNumber, Date, ScheduledStartTime) REFERENCES TripOffering(TripNumber, Date, ScheduledStartTime) ON DELETE CASCADE,
                        FOREIGN KEY (StopNumber) REFERENCES Stop(StopNumber) ON DELETE CASCADE
                    )
                    """;

            stmt.execute(createBusTableSQL);
            stmt.execute(createDriverTableSQL);
            stmt.execute(createStopTableSQL);
            stmt.execute(createTripTableSQL);
            stmt.execute(createTripOfferingTableSQL);
            stmt.execute(createTripStopInfoTableSQL);
            stmt.execute(createActualTripStopInfoTableSQL);
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
