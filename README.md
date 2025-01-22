# Java Spring Application with Maven and MySQL Workbench

This project is a Java Spring Boot application configured to use Maven for dependency management and connects to a MySQL database managed via MySQL Workbench. Below, you'll find the steps to set up, configure, and run the application.

## Prerequisites

1. **Java Development Kit (JDK)**: Ensure JDK 8 or higher is installed.
   - [Download JDK](https://www.oracle.com/java/technologies/javase-jdk-downloads.html)

2. **Maven**: Install Maven for dependency management.
   - [Download Maven](https://maven.apache.org/download.cgi)

3. **MySQL Workbench**: Install and configure MySQL Workbench.
   - [Download MySQL Workbench](https://dev.mysql.com/downloads/workbench/)

4. **Postman**: Download and install Postman to test the APIs.
   - [Download Postman](https://www.postman.com/downloads/)

## Project Setup

### Clone the Repository to your environment.

### Configure the Database

1. Open MySQL Workbench and create a new schema for the application:
   ```sql
   CREATE DATABASE mydb;
   ```

2. Add a new user and grant privileges.
   
3. Note the database name, username, and password are configured in my `application.properties` file.
(for me, as stated in my `application.properties` file, name is myApi, username is root and password is root(for db)).

### Install Dependencies

Run Maven to install the required dependencies:

```bash
mvn clean install
```

## Running the Application

### Run Using an IDE

1. Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
2. Locate the `Application` class (src/main/java/yael/project/myApi/main/MyApiApplication.java).
3. Run the `main` method.

## Testing the Application

### Endpoints

Ensure the application is running on the configured port(I used default: 8080)

I provide 2 ways to test the endpoints:

1)Postman-A separate word file named 'Api documentation - AT&T' will be provided containing detailed instructions and examples for testing all APIs using Postman.

2)Swagger-I also include a swagger link : http://localhost:8080/swagger-ui/index.html
 **Before testing /movies and /showtimes APIS, make sure to put in Authorize button in the UI the JWT token generated after signing in as 'ADMIN'.
 **Before accessing /bookings APIS, make sure to put in the Authorize button in the UI the JWT token generated after signing in as 'CUSTOMER'.

### Database Verification

Use MySQL Workbench to verify data being stored in the database by querying the relevant tables

## Troubleshooting

1. **Database Connection Issues**:
   - Ensure MySQL server is running.
   - Check user credentials and privileges.

2. **Dependency Issues**:
   - Run `mvn dependency:resolve` to identify missing dependencies.


## License
This project is licensed under the MIT License. See the `LICENSE` file for details.
