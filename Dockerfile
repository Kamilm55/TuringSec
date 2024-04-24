# Use an OpenJDK runtime as a base image
FROM openjdk:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build artifacts
COPY ./main/target/TuringSec.jar app.jar

# Expose the port your application runs on
EXPOSE 5000

# Specify the command to run on container startup
CMD ["java", "-jar", "app.jar"]