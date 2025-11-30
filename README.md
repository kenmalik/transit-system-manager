# Pomona Transit System (PTS)

A command-line application for managing transit system records using Java, JDBC, and SQLite.

## Current Features

- Manage buses (add, list, delete)
- Manage drivers (add, list, delete)
- Manage stops (add, list, delete)
- Manage trips (add, list, delete)
- Manage trip offerings (add, list, delete)
- Manage trip stop info (add, list, delete)
- Manage actual trip stop info (add, list, delete)
- SQLite database for persistent storage
- CLI argument parsing with Picocli

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Building

### 1. Clean and compile:
```bash
mvn clean compile
```

### 2. Package into executable JAR:
```bash
mvn clean package
```

This creates a fat JAR (with all dependencies) at:
```
target/cs4350-lab-04-1.0-SNAPSHOT.jar
```

## Running the Application

### Option 1: Using the shell script (Recommended)

```bash
./pts <command> <entity> [arguments]
```

### Option 2: Using java -jar directly

```bash
java -jar target/cs4350-lab-04-1.0-SNAPSHOT.jar <command> <entity> [arguments]
```

### Option 3: Using Maven exec plugin

```bash
mvn exec:java -Dexec.mainClass="dev.klongid.App" -Dexec.args="<command> <entity> [arguments]"
```

**Available entities:** bus, driver, stop, trip, tripoffering, tripstopinfo, actualtripstopinfo

## Commands

### Add a bus
```bash
./pts add bus <busID> <model> <year>
```

**Example:**
```bash
./pts add bus 101 "Volvo 9700" 2020
```

### List all buses
```bash
./pts list bus
```

**Example output:**
```
All buses:
BusID: 101 | Model: Volvo 9700 | Year: 2020
BusID: 102 | Model: MCI J4500 | Year: 2019
BusID: 103 | Model: Prevost H3-45 | Year: 2021
```

### Delete a bus
```bash
./pts delete bus <busID>
```

**Example:**
```bash
./pts delete bus 102
```

### Add a driver
```bash
./pts add driver <name> <phone>
```

**Example:**
```bash
./pts add driver "John Smith" "555-1234"
```

### List all drivers
```bash
./pts list driver
```

**Example output:**
```
All drivers:
Name: John Smith | Phone: 555-1234
Name: Bob Wilson | Phone: 555-9012
```

### Delete a driver
```bash
./pts delete driver <name>
```

**Example:**
```bash
./pts delete driver "John Smith"
```

### Add a stop
```bash
./pts add stop <stopNumber> <address>
```

**Example:**
```bash
./pts add stop 1 "Main St & 1st Ave"
```

### List all stops
```bash
./pts list stop
```

**Example output:**
```
All stops:
Number: 1 | Address: Main St & 1st Ave
Number: 2 | Address: Campus Center
Number: 3 | Address: Downtown Terminal
```

### Delete a stop
```bash
./pts delete stop <stopNumber>
```

**Example:**
```bash
./pts delete stop 2
```

### Add a trip
```bash
./pts add trip <tripNumber> <startLocation> <destination>
```

**Example:**
```bash
./pts add trip 1 "Los Angeles" "San Francisco"
```

### List all trips
```bash
./pts list trip
```

**Example output:**
```
All trips:
TripNumber: 1 | Start: Los Angeles | Destination: San Francisco
TripNumber: 2 | Start: San Diego | Destination: Sacramento
```

### Delete a trip
```bash
./pts delete trip <tripNumber>
```

**Example:**
```bash
./pts delete trip 1
```

### Add a trip offering
```bash
./pts add tripoffering <tripNumber> <date> <startTime> <arrivalTime> <driverName> <busID>
```

**Example:**
```bash
./pts add tripoffering 1 "2024-01-15" "08:00" "12:00" "John Smith" 101
```

**Interactive mode:**
```bash
./pts add tripoffering -i
```

This will prompt you for each field and allow you to add multiple trip offerings:
```
=== Add Trip Offering ===
Trip Number: 1
Date (YYYY-MM-DD): 2024-01-15
Scheduled Start Time (HH:MM): 08:00
Scheduled Arrival Time (HH:MM): 12:00
Driver Name: John Smith
Bus ID: 101

✓ TripOffering added successfully:
  TripNumber: 1
  Date: 2024-01-15
  ScheduledStartTime: 08:00
  ScheduledArrivalTime: 12:00
  DriverName: John Smith
  BusID: 101

Do you want to add another trip offering? (yes/no): no
Exiting interactive mode.
```

### List all trip offerings
```bash
./pts list tripoffering
```

**Example output:**
```
All trip offerings:
TripNumber: 1 | Date: 2024-01-15 | Start: 08:00 | Arrival: 12:00 | Driver: John Smith | BusID: 101
TripNumber: 1 | Date: 2024-01-16 | Start: 09:00 | Arrival: 13:00 | Driver: Bob Wilson | BusID: 102
```

### Delete a trip offering
```bash
./pts delete tripoffering <tripNumber> <date> <startTime>
```

**Example:**
```bash
./pts delete tripoffering 1 "2024-01-15" "08:00"
```

### Add trip stop info
```bash
./pts add tripstopinfo <tripNumber> <stopNumber> <sequenceNumber> <drivingTime>
```

**Example:**
```bash
./pts add tripstopinfo 1 1 1 0
```

### List all trip stop info
```bash
./pts list tripstopinfo
```

**Example output:**
```
All trip stop info:
TripNumber: 1 | StopNumber: 1 | Sequence: 1 | DrivingTime: 0
TripNumber: 1 | StopNumber: 2 | Sequence: 2 | DrivingTime: 30
TripNumber: 1 | StopNumber: 3 | Sequence: 3 | DrivingTime: 60
```

### Delete trip stop info
```bash
./pts delete tripstopinfo <tripNumber> <stopNumber>
```

**Example:**
```bash
./pts delete tripstopinfo 1 2
```

### Add actual trip stop info
```bash
./pts add actualtripstopinfo <tripNumber> <date> <startTime> <stopNumber> <schedArrival> <actualStart> <actualArrival> <passIn> <passOut>
```

**Example:**
```bash
./pts add actualtripstopinfo 1 "2024-01-15" "08:00" 1 "08:15" "08:02" "08:17" 5 2
```

### List all actual trip stop info
```bash
./pts list actualtripstopinfo
```

**Example output:**
```
All actual trip stop info:
TripNumber: 1 | Date: 2024-01-15 | StartTime: 08:00 | StopNumber: 1 | SchedArrival: 08:15 | ActualStart: 08:02 | ActualArrival: 08:17 | PassIn: 5 | PassOut: 2
TripNumber: 1 | Date: 2024-01-15 | StartTime: 08:00 | StopNumber: 2 | SchedArrival: 08:45 | ActualStart: 08:44 | ActualArrival: 08:48 | PassIn: 3 | PassOut: 4
```

### Delete actual trip stop info
```bash
./pts delete actualtripstopinfo <tripNumber> <date> <startTime> <stopNumber>
```

**Example:**
```bash
./pts delete actualtripstopinfo 1 "2024-01-15" "08:00" 2
```

### Schedule a trip
```bash
./pts schedule trip <startLocation> <destination> <date>
```

**Example:**
```bash
./pts schedule trip "Los Angeles" "San Francisco" "2024-01-15"
```

**Example output:**
```
Trip schedule for Los Angeles to San Francisco on 2024-01-15:
Start: Los Angeles | Destination: San Francisco | Date: 2024-01-15 | StartTime: 08:00 | ArrivalTime: 12:00 | Driver: John Smith | BusID: 101
Start: Los Angeles | Destination: San Francisco | Date: 2024-01-15 | StartTime: 14:00 | ArrivalTime: 18:00 | Driver: Bob Wilson | BusID: 102
```

### Schedule for a driver
```bash
./pts schedule driver <driverName> <date>
```

**Example:**
```bash
./pts schedule driver "John Smith" "2024-01-15"
```

**Example output:**
```
Schedule for Driver: John Smith on 2024-01-15:
  Start: Los Angeles | Destination: San Francisco | StartTime: 08:00 | ArrivalTime: 12:00 | BusID: 101
  Start: San Diego | Destination: Sacramento | StartTime: 14:30 | ArrivalTime: 18:30 | BusID: 103
```

### Display stops for a trip
```bash
./pts stops <tripNumber>
```

**Example:**
```bash
./pts stops 1
```

**Example output:**
```
Stops for Trip from Los Angeles to San Francisco:
  Stop 1: Main St & 1st Ave (Driving time: 0 min)
  Stop 2: Campus Center (Driving time: 30 min)
  Stop 3: Downtown Terminal (Driving time: 60 min)
```

### Edit trip offering driver
```bash
./pts edit tripoffering <tripNumber> <date> <startTime> --driver <newDriver>
```

**Example:**
```bash
./pts edit tripoffering 1 "2024-01-15" "08:00" --driver "Bob Wilson"
```

**Example output:**
```
Trip Offering: TripNumber=1, Date=2024-01-15, StartTime=08:00
Old Driver: John Smith
New Driver: Bob Wilson
Are you sure you want to make this change? (yes/no): yes
Driver updated successfully.
```

### Edit trip offering bus
```bash
./pts edit tripoffering <tripNumber> <date> <startTime> --bus <newBusID>
```

**Example:**
```bash
./pts edit tripoffering 1 "2024-01-15" "08:00" --bus 102
```

**Example output:**
```
Trip Offering: TripNumber=1, Date=2024-01-15, StartTime=08:00
Old Bus ID: 101
New Bus ID: 102
Are you sure you want to make this change? (yes/no): yes
Bus updated successfully.
```

## Database

The application uses SQLite with a database file `app.db` created in the project root directory.

### Schema

**Bus Table:**
```sql
CREATE TABLE Bus (
    BusID INTEGER PRIMARY KEY,
    Model TEXT NOT NULL,
    Year INTEGER NOT NULL
)
```

**Driver Table:**
```sql
CREATE TABLE Driver (
    DriverName TEXT PRIMARY KEY,
    DriverTelephoneNumber TEXT NOT NULL
)
```

**Stop Table:**
```sql
CREATE TABLE Stop (
    StopNumber INTEGER PRIMARY KEY,
    StopAddress TEXT NOT NULL
)
```

**Trip Table:**
```sql
CREATE TABLE Trip (
    TripNumber INTEGER PRIMARY KEY,
    StartLocationName TEXT NOT NULL,
    DestinationName TEXT NOT NULL
)
```

**TripOffering Table:**
```sql
CREATE TABLE TripOffering (
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
```

**TripStopInfo Table:**
```sql
CREATE TABLE TripStopInfo (
    TripNumber INTEGER NOT NULL,
    StopNumber INTEGER NOT NULL,
    SequenceNumber INTEGER NOT NULL,
    DrivingTime INTEGER NOT NULL,
    PRIMARY KEY (TripNumber, StopNumber),
    FOREIGN KEY (TripNumber) REFERENCES Trip(TripNumber) ON DELETE CASCADE,
    FOREIGN KEY (StopNumber) REFERENCES Stop(StopNumber) ON DELETE CASCADE
)
```

**ActualTripStopInfo Table:**
```sql
CREATE TABLE ActualTripStopInfo (
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
```

## Dependencies

- **SQLite JDBC Driver** (3.47.0.0) - Database connectivity
- **Picocli** (4.7.5) - CLI argument parsing
- **JUnit Jupiter** (5.11.0) - Testing framework

## Development

### Run tests
```bash
mvn test
```

### Clean build artifacts
```bash
mvn clean
```

## Technologies Used

- Java 17
- Maven
- SQLite JDBC
- Picocli (CLI framework)
- Maven Shade Plugin (for fat JAR creation)
