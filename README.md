# Pomona Transit System (PTS)

A command-line application for managing transit system records using Java, JDBC, and SQLite.

## Current Features

- Manage buses (add, list, delete)
- Manage drivers (add, list, delete)
- Manage stops (add, list, delete)
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

**Available entities:** bus, driver, stop

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
