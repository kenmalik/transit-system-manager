package dev.klongid;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Command(name = "pts", version = "1.0",
         description = "Pomona Transit System",
         subcommands = {App.AddCommand.class, App.DeleteCommand.class, App.ListCommand.class})
public class App implements Callable<Integer> {

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        System.out.println("Pomona Transit System");
        System.out.println("Usage: pts <command> <entity> [options]");
        System.out.println("Commands: add, delete, list");
        System.out.println("Entities: bus, driver");
        return 0;
    }

    @Command(name = "add", description = "Add entities",
             subcommands = {AddCommand.BusCommand.class, AddCommand.DriverCommand.class})
    static class AddCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts add <entity> [options]");
            System.out.println("  pts add bus <busID> <model> <year>");
            System.out.println("  pts add driver <name> <phone>");
            return 0;
        }

        @Command(name = "bus", description = "Add a new bus")
        static class BusCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Bus ID")
            private int busID;

            @Parameters(index = "1", description = "Bus model")
            private String model;

            @Parameters(index = "2", description = "Bus year")
            private int year;

            @Override
            public Integer call() {
                String sql = "INSERT INTO Bus (BusID, Model, Year) VALUES (?, ?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, busID);
                    pstmt.setString(2, model);
                    pstmt.setInt(3, year);
                    pstmt.executeUpdate();

                    System.out.println("Bus added: ID=" + busID + ", Model=" + model + ", Year=" + year);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error adding bus: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "driver", description = "Add a new driver")
        static class DriverCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Driver name")
            private String name;

            @Parameters(index = "1", description = "Driver telephone number")
            private String phone;

            @Override
            public Integer call() {
                String sql = "INSERT INTO Driver (DriverName, DriverTelephoneNumber) VALUES (?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, name);
                    pstmt.setString(2, phone);
                    pstmt.executeUpdate();

                    System.out.println("Driver added: Name=" + name + ", Phone=" + phone);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error adding driver: " + e.getMessage());
                    return 1;
                }
            }
        }
    }

    @Command(name = "delete", description = "Delete entities",
             subcommands = {DeleteCommand.BusCommand.class, DeleteCommand.DriverCommand.class})
    static class DeleteCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts delete <entity> [options]");
            System.out.println("  pts delete bus <busID>");
            System.out.println("  pts delete driver <name>");
            return 0;
        }

        @Command(name = "bus", description = "Delete a bus")
        static class BusCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Bus ID to remove")
            private int busID;

            @Override
            public Integer call() {
                String sql = "DELETE FROM Bus WHERE BusID = ?";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, busID);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println("Bus deleted: ID=" + busID);
                        return 0;
                    } else {
                        System.out.println("No bus found with ID=" + busID);
                        return 1;
                    }

                } catch (SQLException e) {
                    System.err.println("Error deleting bus: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "driver", description = "Delete a driver")
        static class DriverCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Driver name to remove")
            private String name;

            @Override
            public Integer call() {
                String sql = "DELETE FROM Driver WHERE DriverName = ?";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, name);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println("Driver deleted: Name=" + name);
                        return 0;
                    } else {
                        System.out.println("No driver found with name: " + name);
                        return 1;
                    }

                } catch (SQLException e) {
                    System.err.println("Error deleting driver: " + e.getMessage());
                    return 1;
                }
            }
        }
    }

    @Command(name = "list", description = "List entities",
             subcommands = {ListCommand.BusCommand.class, ListCommand.DriverCommand.class})
    static class ListCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts list <entity>");
            System.out.println("  pts list bus");
            System.out.println("  pts list driver");
            return 0;
        }

        @Command(name = "bus", description = "List all buses")
        static class BusCommand implements Callable<Integer> {
            @Override
            public Integer call() {
                String sql = "SELECT BusID, Model, Year FROM Bus";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("All buses:");
                    boolean hasResults = false;
                    while (rs.next()) {
                        hasResults = true;
                        System.out.printf("BusID: %d | Model: %s | Year: %d%n",
                            rs.getInt("BusID"),
                            rs.getString("Model"),
                            rs.getInt("Year"));
                    }

                    if (!hasResults) {
                        System.out.println("No buses found.");
                    }

                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error querying buses: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "driver", description = "List all drivers")
        static class DriverCommand implements Callable<Integer> {
            @Override
            public Integer call() {
                String sql = "SELECT DriverName, DriverTelephoneNumber FROM Driver";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("All drivers:");
                    boolean hasResults = false;
                    while (rs.next()) {
                        hasResults = true;
                        System.out.printf("Name: %s | Phone: %s%n",
                            rs.getString("DriverName"),
                            rs.getString("DriverTelephoneNumber"));
                    }

                    if (!hasResults) {
                        System.out.println("No drivers found.");
                    }

                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error querying drivers: " + e.getMessage());
                    return 1;
                }
            }
        }
    }
}
