# Use Amazon Corretto base image
FROM amazoncorretto:17.0.7-alpine

# Set working directory
WORKDIR /usr/app

# Expose port 8080
EXPOSE 8080

# Copy the jar file into the container
COPY ./target/ecommerce-*.jar ./ecommerce.jar

# Run the jar file
CMD ["java", "-jar", "./ecommerce.jar"]
