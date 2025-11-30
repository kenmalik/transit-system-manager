package dev.klongid;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(name = "pts", version = "1.0", description = "Pomona Transit System", subcommands = { App.AddCommand.class,
        App.DeleteCommand.class, App.ListCommand.class, App.ScheduleCommand.class, App.StopsCommand.class,
        App.EditCommand.class })
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
        System.out.println("Commands: add, delete, list, schedule, stops, edit");
        System.out.println("Entities: bus, driver, stop, trip, tripoffering, tripstopinfo, actualtripstopinfo");
        return 0;
    }

    @Command(name = "add", description = "Add entities", subcommands = { AddCommand.BusCommand.class,
            AddCommand.DriverCommand.class, AddCommand.StopCommand.class, AddCommand.TripCommand.class,
            AddCommand.TripOfferingCommand.class, AddCommand.TripStopInfoCommand.class,
            AddCommand.ActualTripStopInfoCommand.class })
    static class AddCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts add <entity> [options]");
            System.out.println("  pts add bus <busID> <model> <year>");
            System.out.println("  pts add driver <name> <phone>");
            System.out.println("  pts add stop <stopNumber> <address>");
            System.out.println("  pts add trip <tripNumber> <startLocation> <destination>");
            System.out.println(
                    "  pts add tripoffering <tripNumber> <date> <startTime> <arrivalTime> <driverName> <busID>");
            System.out.println("  pts add tripstopinfo <tripNumber> <stopNumber> <sequenceNumber> <drivingTime>");
            System.out.println(
                    "  pts add actualtripstopinfo <tripNumber> <date> <startTime> <stopNumber> <schedArrival> <actualStart> <actualArrival> <passIn> <passOut>");
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

        @Command(name = "stop", description = "Add a new stop")
        static class StopCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Stop number")
            private int stopNumber;

            @Parameters(index = "1", description = "Stop address")
            private String address;

            @Override
            public Integer call() {
                String sql = "INSERT INTO Stop (StopNumber, StopAddress) VALUES (?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, stopNumber);
                    pstmt.setString(2, address);
                    pstmt.executeUpdate();

                    System.out.println("Stop added: Number=" + stopNumber + ", Address=" + address);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error adding stop: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "trip", description = "Add a new trip")
        static class TripCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Start location name")
            private String startLocation;

            @Parameters(index = "2", description = "Destination name")
            private String destination;

            @Override
            public Integer call() {
                String sql = "INSERT INTO Trip (TripNumber, StartLocationName, DestinationName) VALUES (?, ?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setString(2, startLocation);
                    pstmt.setString(3, destination);
                    pstmt.executeUpdate();

                    System.out.println("Trip added: Number=" + tripNumber + ", Start=" + startLocation
                            + ", Destination=" + destination);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error adding trip: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "tripoffering", description = "Add a new trip offering")
        static class TripOfferingCommand implements Callable<Integer> {
            @Option(names = { "-i", "--interactive" }, description = "Interactive mode")
            private boolean interactive;

            @Parameters(index = "0", description = "Trip number", arity = "0..1")
            private Integer tripNumber;

            @Parameters(index = "1", description = "Date", arity = "0..1")
            private String date;

            @Parameters(index = "2", description = "Scheduled start time", arity = "0..1")
            private String startTime;

            @Parameters(index = "3", description = "Scheduled arrival time", arity = "0..1")
            private String arrivalTime;

            @Parameters(index = "4", description = "Driver name", arity = "0..1")
            private String driverName;

            @Parameters(index = "5", description = "Bus ID", arity = "0..1")
            private Integer busID;

            @Override
            public Integer call() {
                if (interactive) {
                    return interactiveMode();
                } else {
                    if (tripNumber == null || date == null || startTime == null || arrivalTime == null
                            || driverName == null || busID == null) {
                        System.err.println("Error: All parameters are required in non-interactive mode.");
                        System.err.println(
                                "Usage: pts add tripoffering <tripNumber> <date> <startTime> <arrivalTime> <driverName> <busID>");
                        return 1;
                    }
                    return addTripOffering(tripNumber, date, startTime, arrivalTime, driverName, busID);
                }
            }

            private Integer interactiveMode() {
                try (Scanner scanner = new Scanner(System.in)) {
                    boolean addAnother = true;

                    while (addAnother) {
                        System.out.println("\n=== Add Trip Offering ===");

                        System.out.print("Trip Number: ");
                        int tripNum = scanner.nextInt();
                        scanner.nextLine(); // consume newline

                        System.out.print("Date (YYYY-MM-DD): ");
                        String tripDate = scanner.nextLine().trim();

                        System.out.print("Scheduled Start Time (HH:MM): ");
                        String schedStart = scanner.nextLine().trim();

                        System.out.print("Scheduled Arrival Time (HH:MM): ");
                        String schedArrival = scanner.nextLine().trim();

                        System.out.print("Driver Name: ");
                        String driver = scanner.nextLine().trim();

                        System.out.print("Bus ID: ");
                        int bus = scanner.nextInt();
                        scanner.nextLine(); // consume newline

                        // Attempt to insert
                        int result = addTripOffering(tripNum, tripDate, schedStart, schedArrival, driver, bus);

                        if (result != 0) {
                            System.out.println("\nFailed to add trip offering.");
                        }

                        System.out.print("\nDo you want to add another trip offering? (yes/no): ");
                        String response = scanner.nextLine().trim().toLowerCase();
                        addAnother = response.equals("yes") || response.equals("y");
                    }

                    System.out.println("Exiting interactive mode.");
                    return 0;
                }
            }

            private Integer addTripOffering(int tripNum, String tripDate, String schedStart, String schedArrival,
                    String driver, int bus) {
                String sql = "INSERT INTO TripOffering (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNum);
                    pstmt.setString(2, tripDate);
                    pstmt.setString(3, schedStart);
                    pstmt.setString(4, schedArrival);
                    pstmt.setString(5, driver);
                    pstmt.setInt(6, bus);
                    pstmt.executeUpdate();

                    System.out.println("\n✓ TripOffering added successfully:");
                    System.out.println("  TripNumber: " + tripNum);
                    System.out.println("  Date: " + tripDate);
                    System.out.println("  ScheduledStartTime: " + schedStart);
                    System.out.println("  ScheduledArrivalTime: " + schedArrival);
                    System.out.println("  DriverName: " + driver);
                    System.out.println("  BusID: " + bus);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("✗ Error adding trip offering: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "tripstopinfo", description = "Add a new trip stop info")
        static class TripStopInfoCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Stop number")
            private int stopNumber;

            @Parameters(index = "2", description = "Sequence number")
            private int sequenceNumber;

            @Parameters(index = "3", description = "Driving time")
            private int drivingTime;

            @Override
            public Integer call() {
                String sql = "INSERT INTO TripStopInfo (TripNumber, StopNumber, SequenceNumber, DrivingTime) VALUES (?, ?, ?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setInt(2, stopNumber);
                    pstmt.setInt(3, sequenceNumber);
                    pstmt.setInt(4, drivingTime);
                    pstmt.executeUpdate();

                    System.out.println("TripStopInfo added: TripNumber=" + tripNumber + ", StopNumber=" + stopNumber
                            + ", Sequence=" + sequenceNumber + ", DrivingTime=" + drivingTime);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error adding trip stop info: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "actualtripstopinfo", description = "Add a new actual trip stop info")
        static class ActualTripStopInfoCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Date")
            private String date;

            @Parameters(index = "2", description = "Scheduled start time")
            private String scheduledStartTime;

            @Parameters(index = "3", description = "Stop number")
            private int stopNumber;

            @Parameters(index = "4", description = "Scheduled arrival time")
            private String scheduledArrivalTime;

            @Parameters(index = "5", description = "Actual start time")
            private String actualStartTime;

            @Parameters(index = "6", description = "Actual arrival time")
            private String actualArrivalTime;

            @Parameters(index = "7", description = "Number of passengers in")
            private int passengersIn;

            @Parameters(index = "8", description = "Number of passengers out")
            private int passengersOut;

            @Override
            public Integer call() {
                String sql = "INSERT INTO ActualTripStopInfo (TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengersIn, NumberOfPassengersOut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setString(2, date);
                    pstmt.setString(3, scheduledStartTime);
                    pstmt.setInt(4, stopNumber);
                    pstmt.setString(5, scheduledArrivalTime);
                    pstmt.setString(6, actualStartTime);
                    pstmt.setString(7, actualArrivalTime);
                    pstmt.setInt(8, passengersIn);
                    pstmt.setInt(9, passengersOut);
                    pstmt.executeUpdate();

                    System.out.println("ActualTripStopInfo added: TripNumber=" + tripNumber + ", Date=" + date
                            + ", ScheduledStartTime=" + scheduledStartTime + ", StopNumber=" + stopNumber);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error adding actual trip stop info: " + e.getMessage());
                    return 1;
                }
            }
        }
    }

    @Command(name = "delete", description = "Delete entities", subcommands = { DeleteCommand.BusCommand.class,
            DeleteCommand.DriverCommand.class, DeleteCommand.StopCommand.class, DeleteCommand.TripCommand.class,
            DeleteCommand.TripOfferingCommand.class, DeleteCommand.TripStopInfoCommand.class,
            DeleteCommand.ActualTripStopInfoCommand.class })
    static class DeleteCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts delete <entity> [options]");
            System.out.println("  pts delete bus <busID>");
            System.out.println("  pts delete driver <name>");
            System.out.println("  pts delete stop <stopNumber>");
            System.out.println("  pts delete trip <tripNumber>");
            System.out.println("  pts delete tripoffering <tripNumber> <date> <startTime>");
            System.out.println("  pts delete tripstopinfo <tripNumber> <stopNumber>");
            System.out.println("  pts delete actualtripstopinfo <tripNumber> <date> <startTime> <stopNumber>");
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

        @Command(name = "stop", description = "Delete a stop")
        static class StopCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Stop number to remove")
            private int stopNumber;

            @Override
            public Integer call() {
                String sql = "DELETE FROM Stop WHERE StopNumber = ?";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, stopNumber);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println("Stop deleted: Number=" + stopNumber);
                        return 0;
                    } else {
                        System.out.println("No stop found with number: " + stopNumber);
                        return 1;
                    }

                } catch (SQLException e) {
                    System.err.println("Error deleting stop: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "trip", description = "Delete a trip")
        static class TripCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number to remove")
            private int tripNumber;

            @Override
            public Integer call() {
                String sql = "DELETE FROM Trip WHERE TripNumber = ?";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println("Trip deleted: Number=" + tripNumber);
                        return 0;
                    } else {
                        System.out.println("No trip found with number: " + tripNumber);
                        return 1;
                    }

                } catch (SQLException e) {
                    System.err.println("Error deleting trip: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "tripoffering", description = "Delete a trip offering")
        static class TripOfferingCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Date")
            private String date;

            @Parameters(index = "2", description = "Scheduled start time")
            private String startTime;

            @Override
            public Integer call() {
                String sql = "DELETE FROM TripOffering WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setString(2, date);
                    pstmt.setString(3, startTime);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println("TripOffering deleted: TripNumber=" + tripNumber + ", Date=" + date
                                + ", StartTime=" + startTime);
                        return 0;
                    } else {
                        System.out.println("No trip offering found with TripNumber=" + tripNumber + ", Date=" + date
                                + ", StartTime=" + startTime);
                        return 1;
                    }

                } catch (SQLException e) {
                    System.err.println("Error deleting trip offering: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "tripstopinfo", description = "Delete a trip stop info")
        static class TripStopInfoCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Stop number")
            private int stopNumber;

            @Override
            public Integer call() {
                String sql = "DELETE FROM TripStopInfo WHERE TripNumber = ? AND StopNumber = ?";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setInt(2, stopNumber);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println(
                                "TripStopInfo deleted: TripNumber=" + tripNumber + ", StopNumber=" + stopNumber);
                        return 0;
                    } else {
                        System.out.println(
                                "No trip stop info found with TripNumber=" + tripNumber + ", StopNumber=" + stopNumber);
                        return 1;
                    }

                } catch (SQLException e) {
                    System.err.println("Error deleting trip stop info: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "actualtripstopinfo", description = "Delete an actual trip stop info")
        static class ActualTripStopInfoCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Date")
            private String date;

            @Parameters(index = "2", description = "Scheduled start time")
            private String scheduledStartTime;

            @Parameters(index = "3", description = "Stop number")
            private int stopNumber;

            @Override
            public Integer call() {
                String sql = "DELETE FROM ActualTripStopInfo WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ? AND StopNumber = ?";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setString(2, date);
                    pstmt.setString(3, scheduledStartTime);
                    pstmt.setInt(4, stopNumber);
                    int rowsDeleted = pstmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        System.out.println("ActualTripStopInfo deleted: TripNumber=" + tripNumber + ", Date=" + date
                                + ", ScheduledStartTime=" + scheduledStartTime + ", StopNumber=" + stopNumber);
                        return 0;
                    } else {
                        System.out.println("No actual trip stop info found with TripNumber=" + tripNumber + ", Date="
                                + date + ", ScheduledStartTime=" + scheduledStartTime + ", StopNumber=" + stopNumber);
                        return 1;
                    }

                } catch (SQLException e) {
                    System.err.println("Error deleting actual trip stop info: " + e.getMessage());
                    return 1;
                }
            }
        }
    }

    @Command(name = "list", description = "List entities", subcommands = { ListCommand.BusCommand.class,
            ListCommand.DriverCommand.class, ListCommand.StopCommand.class, ListCommand.TripCommand.class,
            ListCommand.TripOfferingCommand.class, ListCommand.TripStopInfoCommand.class,
            ListCommand.ActualTripStopInfoCommand.class })
    static class ListCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts list <entity>");
            System.out.println("  pts list bus");
            System.out.println("  pts list driver");
            System.out.println("  pts list stop");
            System.out.println("  pts list trip");
            System.out.println("  pts list tripoffering");
            System.out.println("  pts list tripstopinfo");
            System.out.println("  pts list actualtripstopinfo");
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

        @Command(name = "stop", description = "List all stops")
        static class StopCommand implements Callable<Integer> {
            @Override
            public Integer call() {
                String sql = "SELECT StopNumber, StopAddress FROM Stop";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("All stops:");
                    boolean hasResults = false;
                    while (rs.next()) {
                        hasResults = true;
                        System.out.printf("Number: %d | Address: %s%n",
                                rs.getInt("StopNumber"),
                                rs.getString("StopAddress"));
                    }

                    if (!hasResults) {
                        System.out.println("No stops found.");
                    }

                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error querying stops: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "trip", description = "List all trips")
        static class TripCommand implements Callable<Integer> {
            @Override
            public Integer call() {
                String sql = "SELECT TripNumber, StartLocationName, DestinationName FROM Trip";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("All trips:");
                    boolean hasResults = false;
                    while (rs.next()) {
                        hasResults = true;
                        System.out.printf("TripNumber: %d | Start: %s | Destination: %s%n",
                                rs.getInt("TripNumber"),
                                rs.getString("StartLocationName"),
                                rs.getString("DestinationName"));
                    }

                    if (!hasResults) {
                        System.out.println("No trips found.");
                    }

                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error querying trips: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "tripoffering", description = "List all trip offerings")
        static class TripOfferingCommand implements Callable<Integer> {
            @Override
            public Integer call() {
                String sql = "SELECT TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID FROM TripOffering";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("All trip offerings:");
                    boolean hasResults = false;
                    while (rs.next()) {
                        hasResults = true;
                        System.out.printf(
                                "TripNumber: %d | Date: %s | Start: %s | Arrival: %s | Driver: %s | BusID: %d%n",
                                rs.getInt("TripNumber"),
                                rs.getString("Date"),
                                rs.getString("ScheduledStartTime"),
                                rs.getString("ScheduledArrivalTime"),
                                rs.getString("DriverName"),
                                rs.getInt("BusID"));
                    }

                    if (!hasResults) {
                        System.out.println("No trip offerings found.");
                    }

                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error querying trip offerings: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "tripstopinfo", description = "List all trip stop info")
        static class TripStopInfoCommand implements Callable<Integer> {
            @Override
            public Integer call() {
                String sql = "SELECT TripNumber, StopNumber, SequenceNumber, DrivingTime FROM TripStopInfo";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("All trip stop info:");
                    boolean hasResults = false;
                    while (rs.next()) {
                        hasResults = true;
                        System.out.printf("TripNumber: %d | StopNumber: %d | Sequence: %d | DrivingTime: %d%n",
                                rs.getInt("TripNumber"),
                                rs.getInt("StopNumber"),
                                rs.getInt("SequenceNumber"),
                                rs.getInt("DrivingTime"));
                    }

                    if (!hasResults) {
                        System.out.println("No trip stop info found.");
                    }

                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error querying trip stop info: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "actualtripstopinfo", description = "List all actual trip stop info")
        static class ActualTripStopInfoCommand implements Callable<Integer> {
            @Override
            public Integer call() {
                String sql = "SELECT TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengersIn, NumberOfPassengersOut FROM ActualTripStopInfo";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        ResultSet rs = pstmt.executeQuery()) {

                    System.out.println("All actual trip stop info:");
                    boolean hasResults = false;
                    while (rs.next()) {
                        hasResults = true;
                        System.out.printf(
                                "TripNumber: %d | Date: %s | StartTime: %s | StopNumber: %d | SchedArrival: %s | ActualStart: %s | ActualArrival: %s | PassIn: %d | PassOut: %d%n",
                                rs.getInt("TripNumber"),
                                rs.getString("Date"),
                                rs.getString("ScheduledStartTime"),
                                rs.getInt("StopNumber"),
                                rs.getString("ScheduledArrivalTime"),
                                rs.getString("ActualStartTime"),
                                rs.getString("ActualArrivalTime"),
                                rs.getInt("NumberOfPassengersIn"),
                                rs.getInt("NumberOfPassengersOut"));
                    }

                    if (!hasResults) {
                        System.out.println("No actual trip stop info found.");
                    }

                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error querying actual trip stop info: " + e.getMessage());
                    return 1;
                }
            }
        }
    }

    @Command(name = "schedule", description = "Schedule queries", subcommands = { ScheduleCommand.TripCommand.class,
            ScheduleCommand.DriverCommand.class })
    static class ScheduleCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts schedule <entity> [options]");
            System.out.println("  pts schedule trip <startLocation> <destination> <date>");
            System.out.println("  pts schedule driver <driverName> <date>");
            System.out.println("  pts schedule driver <driverName> <date> --week");
            return 0;
        }

        @Command(name = "trip", description = "Get trip schedule")
        static class TripCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Start location name")
            private String startLocation;

            @Parameters(index = "1", description = "Destination name")
            private String destination;

            @Parameters(index = "2", description = "Date")
            private String date;

            @Override
            public Integer call() {
                String sql = """
                        SELECT t.StartLocationName, t.DestinationName,
                               tof.Date, tof.ScheduledStartTime, tof.ScheduledArrivalTime,
                               tof.DriverName, tof.BusID
                        FROM Trip t
                        JOIN TripOffering tof ON t.TripNumber = tof.TripNumber
                        WHERE t.StartLocationName = ? AND t.DestinationName = ? AND tof.Date = ?
                        """;

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, startLocation);
                    pstmt.setString(2, destination);
                    pstmt.setString(3, date);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        System.out.println("Trip schedule for " + startLocation + " to " + destination + " on " + date
                                + ":");
                        boolean hasResults = false;
                        while (rs.next()) {
                            hasResults = true;
                            System.out.printf(
                                    "Start: %s | Destination: %s | Date: %s | StartTime: %s | ArrivalTime: %s | Driver: %s | BusID: %d%n",
                                    rs.getString("StartLocationName"),
                                    rs.getString("DestinationName"),
                                    rs.getString("Date"),
                                    rs.getString("ScheduledStartTime"),
                                    rs.getString("ScheduledArrivalTime"),
                                    rs.getString("DriverName"),
                                    rs.getInt("BusID"));
                        }

                        if (!hasResults) {
                            System.out.println("No trips found for the specified criteria.");
                        }

                        return 0;
                    }

                } catch (SQLException e) {
                    System.err.println("Error querying trip schedule: " + e.getMessage());
                    return 1;
                }
            }
        }

        @Command(name = "driver", description = "Get driver schedule")
        static class DriverCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Driver name")
            private String driverName;

            @Parameters(index = "1", description = "Date")
            private String date;

            @Option(names = { "--week" }, description = "Show weekly schedule instead of single day")
            private boolean weeklySchedule;

            @Override
            public Integer call() {
                if (weeklySchedule) {
                    return showWeeklySchedule();
                } else {
                    return showDailySchedule();
                }
            }

            private Integer showDailySchedule() {
                String sql = """
                        SELECT d.DriverName, tof.Date,
                               t.StartLocationName, t.DestinationName,
                               tof.ScheduledStartTime, tof.ScheduledArrivalTime,
                               tof.BusID
                        FROM Driver d
                        JOIN TripOffering tof ON d.DriverName = tof.DriverName
                        JOIN Trip t ON tof.TripNumber = t.TripNumber
                        WHERE d.DriverName = ? AND tof.Date = ?
                        ORDER BY tof.ScheduledStartTime
                        """;

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, driverName);
                    pstmt.setString(2, date);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        boolean hasResults = false;
                        boolean headerPrinted = false;

                        while (rs.next()) {
                            if (!headerPrinted) {
                                System.out.println("Schedule for Driver: " + rs.getString("DriverName") + " on "
                                        + rs.getString("Date") + ":");
                                headerPrinted = true;
                            }
                            hasResults = true;
                            System.out.printf(
                                    "  Start: %s | Destination: %s | StartTime: %s | ArrivalTime: %s | BusID: %d%n",
                                    rs.getString("StartLocationName"),
                                    rs.getString("DestinationName"),
                                    rs.getString("ScheduledStartTime"),
                                    rs.getString("ScheduledArrivalTime"),
                                    rs.getInt("BusID"));
                        }

                        if (!hasResults) {
                            System.out.println("No schedule found for driver '" + driverName + "' on " + date + ".");
                        }

                        return 0;
                    }

                } catch (SQLException e) {
                    System.err.println("Error querying driver schedule: " + e.getMessage());
                    return 1;
                }
            }

            private Integer showWeeklySchedule() {
                // Calculate week boundaries (Sunday-Saturday)
                LocalDate givenDate = LocalDate.parse(date);
                LocalDate weekStart = givenDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
                LocalDate weekEnd = weekStart.plusDays(6);

                String sql = """
                        SELECT d.DriverName, tof.Date,
                               t.StartLocationName, t.DestinationName,
                               tof.ScheduledStartTime, tof.ScheduledArrivalTime,
                               tof.BusID
                        FROM Driver d
                        JOIN TripOffering tof ON d.DriverName = tof.DriverName
                        JOIN Trip t ON tof.TripNumber = t.TripNumber
                        WHERE d.DriverName = ? AND tof.Date >= ? AND tof.Date <= ?
                        ORDER BY tof.Date, tof.ScheduledStartTime
                        """;

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, driverName);
                    pstmt.setString(2, weekStart.toString());
                    pstmt.setString(3, weekEnd.toString());

                    try (ResultSet rs = pstmt.executeQuery()) {
                        boolean hasResults = false;
                        String currentDate = null;

                        System.out.println("Weekly schedule for Driver: " + driverName);
                        System.out.println("Week: " + weekStart + " to " + weekEnd);
                        System.out.println();

                        while (rs.next()) {
                            hasResults = true;
                            String tripDate = rs.getString("Date");

                            // Print date header when date changes
                            if (!tripDate.equals(currentDate)) {
                                if (currentDate != null) {
                                    System.out.println(); // Blank line between days
                                }
                                LocalDate dateObj = LocalDate.parse(tripDate);
                                System.out.println(dateObj.getDayOfWeek() + ", " + tripDate + ":");
                                currentDate = tripDate;
                            }

                            System.out.printf(
                                    "  Start: %s | Destination: %s | StartTime: %s | ArrivalTime: %s | BusID: %d%n",
                                    rs.getString("StartLocationName"),
                                    rs.getString("DestinationName"),
                                    rs.getString("ScheduledStartTime"),
                                    rs.getString("ScheduledArrivalTime"),
                                    rs.getInt("BusID"));
                        }

                        if (!hasResults) {
                            System.out.println("No schedule found for driver '" + driverName + "' during the week of "
                                    + weekStart + ".");
                        }

                        return 0;
                    }

                } catch (SQLException e) {
                    System.err.println("Error querying driver weekly schedule: " + e.getMessage());
                    return 1;
                }
            }
        }
    }

    @Command(name = "stops", description = "Display stops for a trip")
    static class StopsCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Trip number")
        private int tripNumber;

        @Override
        public Integer call() {
            String sql = """
                    SELECT t.StartLocationName, t.DestinationName,
                           tsi.SequenceNumber, s.StopAddress, tsi.DrivingTime
                    FROM TripStopInfo tsi
                    JOIN Trip t ON tsi.TripNumber = t.TripNumber
                    JOIN Stop s ON tsi.StopNumber = s.StopNumber
                    WHERE tsi.TripNumber = ?
                    ORDER BY tsi.SequenceNumber
                    """;

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, tripNumber);

                try (ResultSet rs = pstmt.executeQuery()) {
                    boolean hasResults = false;
                    boolean headerPrinted = false;

                    while (rs.next()) {
                        if (!headerPrinted) {
                            System.out.println("Stops for Trip from " + rs.getString("StartLocationName") + " to "
                                    + rs.getString("DestinationName") + ":");
                            headerPrinted = true;
                        }
                        hasResults = true;
                        System.out.printf("  Stop %d: %s (Driving time: %d min)%n",
                                rs.getInt("SequenceNumber"),
                                rs.getString("StopAddress"),
                                rs.getInt("DrivingTime"));
                    }

                    if (!hasResults) {
                        System.out.println("No stops found for trip number " + tripNumber + ".");
                    }

                    return 0;
                }

            } catch (SQLException e) {
                System.err.println("Error querying trip stops: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "edit", description = "Edit entities", subcommands = { EditCommand.TripOfferingCommand.class })
    static class EditCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts edit <entity> [options]");
            System.out.println("  pts edit tripoffering <tripNumber> <date> <startTime> --driver <newDriver>");
            System.out.println("  pts edit tripoffering <tripNumber> <date> <startTime> --bus <newBusID>");
            return 0;
        }

        @Command(name = "tripoffering", description = "Edit a trip offering")
        static class TripOfferingCommand implements Callable<Integer> {
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Date")
            private String date;

            @Parameters(index = "2", description = "Scheduled start time")
            private String startTime;

            @Option(names = "--driver", description = "New driver name")
            private String newDriver;

            @Option(names = "--bus", description = "New bus ID")
            private Integer newBusID;

            @Override
            public Integer call() {
                if (newDriver == null && newBusID == null) {
                    System.err.println("Error: You must specify either --driver or --bus option.");
                    return 1;
                }

                if (newDriver != null && newBusID != null) {
                    System.err.println("Error: You can only specify one option at a time (--driver or --bus).");
                    return 1;
                }

                if (newDriver != null) {
                    return editDriver();
                } else {
                    return editBus();
                }
            }

            private Integer editDriver() {
                // First, check if the new driver exists
                String checkDriverSQL = "SELECT DriverName FROM Driver WHERE DriverName = ?";
                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(checkDriverSQL)) {

                    pstmt.setString(1, newDriver);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            System.err.println("Error: Driver '" + newDriver + "' does not exist in the database.");
                            return 1;
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking driver existence: " + e.getMessage());
                    return 1;
                }

                // Get current trip offering info
                String getTripOfferingSQL = "SELECT DriverName FROM TripOffering WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";
                String oldDriver;
                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(getTripOfferingSQL)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setString(2, date);
                    pstmt.setString(3, startTime);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            System.err.println("Error: Trip offering not found.");
                            return 1;
                        }
                        oldDriver = rs.getString("DriverName");
                    }
                } catch (SQLException e) {
                    System.err.println("Error retrieving trip offering: " + e.getMessage());
                    return 1;
                }

                // Display confirmation
                System.out.println("Trip Offering: TripNumber=" + tripNumber + ", Date=" + date + ", StartTime="
                        + startTime);
                System.out.println("Old Driver: " + oldDriver);
                System.out.println("New Driver: " + newDriver);
                System.out.print("Are you sure you want to make this change? (yes/no): ");

                try (Scanner scanner = new Scanner(System.in)) {
                    String response = scanner.nextLine().trim().toLowerCase();
                    if (!response.equals("yes")) {
                        System.out.println("Edit cancelled.");
                        return 0;
                    }
                }

                // Perform the update
                String updateSQL = "UPDATE TripOffering SET DriverName = ? WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";
                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

                    pstmt.setString(1, newDriver);
                    pstmt.setInt(2, tripNumber);
                    pstmt.setString(3, date);
                    pstmt.setString(4, startTime);

                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Driver updated successfully.");
                        return 0;
                    } else {
                        System.err.println("Error: Failed to update driver.");
                        return 1;
                    }
                } catch (SQLException e) {
                    System.err.println("Error updating driver: " + e.getMessage());
                    return 1;
                }
            }

            private Integer editBus() {
                // First, check if the new bus exists
                String checkBusSQL = "SELECT BusID FROM Bus WHERE BusID = ?";
                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(checkBusSQL)) {

                    pstmt.setInt(1, newBusID);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            System.err.println("Error: Bus with ID " + newBusID + " does not exist in the database.");
                            return 1;
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking bus existence: " + e.getMessage());
                    return 1;
                }

                // Get current trip offering info
                String getTripOfferingSQL = "SELECT BusID FROM TripOffering WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";
                int oldBusID;
                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(getTripOfferingSQL)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setString(2, date);
                    pstmt.setString(3, startTime);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            System.err.println("Error: Trip offering not found.");
                            return 1;
                        }
                        oldBusID = rs.getInt("BusID");
                    }
                } catch (SQLException e) {
                    System.err.println("Error retrieving trip offering: " + e.getMessage());
                    return 1;
                }

                // Display confirmation
                System.out.println("Trip Offering: TripNumber=" + tripNumber + ", Date=" + date + ", StartTime="
                        + startTime);
                System.out.println("Old Bus ID: " + oldBusID);
                System.out.println("New Bus ID: " + newBusID);
                System.out.print("Are you sure you want to make this change? (yes/no): ");

                try (Scanner scanner = new Scanner(System.in)) {
                    String response = scanner.nextLine().trim().toLowerCase();
                    if (!response.equals("yes")) {
                        System.out.println("Edit cancelled.");
                        return 0;
                    }
                }

                // Perform the update
                String updateSQL = "UPDATE TripOffering SET BusID = ? WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";
                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

                    pstmt.setInt(1, newBusID);
                    pstmt.setInt(2, tripNumber);
                    pstmt.setString(3, date);
                    pstmt.setString(4, startTime);

                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Bus updated successfully.");
                        return 0;
                    } else {
                        System.err.println("Error: Failed to update bus.");
                        return 1;
                    }
                } catch (SQLException e) {
                    System.err.println("Error updating bus: " + e.getMessage());
                    return 1;
                }
            }
        }
    }
}
