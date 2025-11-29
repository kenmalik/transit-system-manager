package dev.klongid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearBusTable();

        addBus(101, "Volvo 9700", 2020);
        addBus(102, "MCI J4500", 2019);
        addBus(103, "Prevost H3-45", 2021);

        System.out.println("\nAll buses:");
        displayAllBuses();

        deleteBus(102);

        System.out.println("\nAll buses after deletion:");
        displayAllBuses();
    }

    private static void addBus(int busID, String model, int year) {
        String sql = "INSERT INTO Bus (BusID, Model, Year) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, busID);
            pstmt.setString(2, model);
            pstmt.setInt(3, year);
            pstmt.executeUpdate();

            System.out.println("Bus added: ID=" + busID + ", Model=" + model + ", Year=" + year);

        } catch (SQLException e) {
            System.err.println("Error adding bus: " + e.getMessage());
        }
    }

    private static void deleteBus(int busID) {
        String sql = "DELETE FROM Bus WHERE BusID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, busID);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Bus deleted: ID=" + busID);
            } else {
                System.out.println("No bus found with ID=" + busID);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting bus: " + e.getMessage());
        }
    }

    private static void displayAllBuses() {
        String sql = "SELECT BusID, Model, Year FROM Bus";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("BusID: %d | Model: %s | Year: %d%n",
                    rs.getInt("BusID"),
                    rs.getString("Model"),
                    rs.getInt("Year"));
            }

        } catch (SQLException e) {
            System.err.println("Error querying buses: " + e.getMessage());
        }
    }
}
