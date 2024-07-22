# Stage 1: Build the JAR file
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and any other configuration files
COPY pom.xml .

# Copy the source code
COPY src ./src

# Package the application
RUN mvn clean package

# Stage 2: Create the final image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/TuringSec-0.0.1.jar app.jar

# Copy the keystore if needed
COPY --from=build /app/src/main/resources/keystore.p12 /app/keystore.p12

# Expose the port your application runs on 5000 in app.dev but in app.prod -> (8080 internally, 443 externally)
EXPOSE 5000
EXPOSE 6000 #fix in aws

# Specify the command to run on container startup
CMD ["java", "-jar", "app.jar"]
