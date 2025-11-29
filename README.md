# Pomona Transit System (PTS)

A command-line application for managing bus records using Java, JDBC, and SQLite.

## Current Features

- Add buses to the database
- List all buses
- Delete buses by ID
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
./pts <command> bus [arguments]
```

### Option 2: Using java -jar directly

```bash
java -jar target/cs4350-lab-04-1.0-SNAPSHOT.jar <command> bus [arguments]
```

### Option 3: Using Maven exec plugin

```bash
mvn exec:java -Dexec.mainClass="dev.klongid.App" -Dexec.args="<command> bus [arguments]"
```

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

### Help
Get help for any command:
```bash
./pts --help
./pts add bus --help
./pts delete bus --help
./pts list bus --help
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
