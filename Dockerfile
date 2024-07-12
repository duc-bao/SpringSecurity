#Syntax=docker/dockerfile:1
#which official Java image
FROM openjdk:17
# working directory
WORKDIR /app
# coppy from your Host(PC, Laptop) to container ví dụ từ file .Mvn/ sang mvn của container

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Run this inside the image
RUN  ./mvnw dependency:go-offline
COPY  src ./src

#run inside container
CMD ["./mvnw", "spring-boot:run"]