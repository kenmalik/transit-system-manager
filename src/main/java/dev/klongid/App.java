package dev.klongid;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@Command(name = "pts", version = "1.0", description = "Pomona Transit System", subcommands = { App.AddCommand.class,
        App.DeleteCommand.class, App.ListCommand.class, App.ScheduleCommand.class })
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
        System.out.println("Commands: add, delete, list, schedule");
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
            @Parameters(index = "0", description = "Trip number")
            private int tripNumber;

            @Parameters(index = "1", description = "Date")
            private String date;

            @Parameters(index = "2", description = "Scheduled start time")
            private String startTime;

            @Parameters(index = "3", description = "Scheduled arrival time")
            private String arrivalTime;

            @Parameters(index = "4", description = "Driver name")
            private String driverName;

            @Parameters(index = "5", description = "Bus ID")
            private int busID;

            @Override
            public Integer call() {
                String sql = "INSERT INTO TripOffering (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection conn = DatabaseManager.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, tripNumber);
                    pstmt.setString(2, date);
                    pstmt.setString(3, startTime);
                    pstmt.setString(4, arrivalTime);
                    pstmt.setString(5, driverName);
                    pstmt.setInt(6, busID);
                    pstmt.executeUpdate();

                    System.out.println("TripOffering added: TripNumber=" + tripNumber + ", Date=" + date
                            + ", StartTime=" + startTime + ", ArrivalTime=" + arrivalTime + ", Driver=" + driverName
                            + ", BusID=" + busID);
                    return 0;

                } catch (SQLException e) {
                    System.err.println("Error adding trip offering: " + e.getMessage());
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

    @Command(name = "schedule", description = "Schedule queries", subcommands = { ScheduleCommand.TripCommand.class })
    static class ScheduleCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            System.out.println("Usage: pts schedule <entity> [options]");
            System.out.println("  pts schedule trip <startLocation> <destination> <date>");
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
    }
}
