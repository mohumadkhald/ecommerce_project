# Use Amazon Corretto base image
FROM amazoncorretto:17.0.7-alpine

# Set working directory
WORKDIR /usr/app

# Expose port 8443
EXPOSE 8443
ENV SPRING_PROFILES_ACTIVE=docker

# Copy the jar file and keystore file into the container
COPY ./target/ecommerce-*.jar ./ecommerce.jar
COPY ./src/main/resources/keystore.p12 /etc/ssl/certs/keystore.p12

# Create the uploads directory
RUN mkdir -p /usr/app/uploads

# Run the jar file
CMD ["java", "-jar", "./ecommerce.jar"]
