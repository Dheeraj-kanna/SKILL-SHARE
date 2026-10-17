# Campus Skill Exchange

A Java console application backed by MySQL. Students can list skills they can teach or want to learn, find matches, request exchanges, schedule sessions, and review accepted exchanges.

## Requirements

- Java 17 or later
- MySQL 8 or later
- The bundled MySQL Connector/J driver in `lib/mysql-connector-j-26.7.0/`

## Set up the database

Open `src/database_schema.sql` in MySQL Workbench and run it. It creates the `campus_skill_exchange` database and the five tables used by the application. The script is safe to run again against an existing installation; it does not delete data or change existing table definitions.

## Configure the connection

Set these environment variables in the terminal where you will run the app:

```sh
export CAMPUS_DB_URL="jdbc:mysql://localhost:3306/campus_skill_exchange"
export CAMPUS_DB_USER="your_mysql_user"
export CAMPUS_DB_PASSWORD="your_mysql_password"
```

The URL defaults to the local database shown above if `CAMPUS_DB_URL` is omitted. Do not commit your actual password.

## Compile and run

From the project root on macOS or Linux:

```sh
javac -cp "lib/mysql-connector-j-26.7.0/mysql-connector-j-26.7.0.jar" src/*.java
java -cp "src:lib/mysql-connector-j-26.7.0/mysql-connector-j-26.7.0.jar" Main
```

To check the database connection without starting the menu:

```sh
java -cp "src:lib/mysql-connector-j-26.7.0/mysql-connector-j-26.7.0.jar" TestConnection
```

On Windows, use `;` instead of `:` between the two classpath entries in the `java` command.

## Password storage

New accounts store passwords as salted PBKDF2-HMAC-SHA-256 hashes. Existing local accounts created by an earlier version with plain-text values are upgraded to hashes after their next successful login.

## Main menu

1. Register and log in.
2. Add skills you can teach and skills you want to learn.
3. Find matches and send or respond to exchange requests.
4. Schedule sessions for accepted exchanges.
5. Submit and view reviews.

## Third-party driver

The bundled MySQL Connector/J driver is distributed by Oracle under GPLv2 with the Universal FOSS Exception. Its version-matched license is included beside the JAR in `lib/mysql-connector-j-26.7.0/LICENSE`; the [upstream 26.7.0 source](https://github.com/mysql/mysql-connector-j/tree/26.7.0) is also available.
