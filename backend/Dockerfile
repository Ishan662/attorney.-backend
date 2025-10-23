# Use an official OpenJDK runtime as a parent image
# Choose a version compatible with your Spring Boot version (e.g., 17, 21)
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files (if you use ./mvnw)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies first to leverage Docker cache
RUN ./mvnw dependency:go-offline -B

# Copy the rest of your source code
COPY src ./src

# Build the application JAR file
# Use -DskipTests to speed up builds in CI/CD environments
RUN ./mvnw package -DskipTests

# Expose the port the app runs on (usually 8080, but Render uses 10000)
# The SERVER_PORT environment variable will override this, but it's good practice
EXPOSE 10000

# Specify the command to run on container start
# Make sure the JAR file name matches what your build produces!
ENTRYPOINT ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]