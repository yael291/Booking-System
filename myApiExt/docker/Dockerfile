# Use an official OpenJDK image as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build output to the container
COPY target/Book_InventoryN-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application will run on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]