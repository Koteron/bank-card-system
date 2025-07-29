FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /opt/app
COPY . .
RUN mvn clean package -DskipTests

FROM maven:3.9.11-eclipse-temurin-17
WORKDIR /opt/app
COPY --from=builder /opt/app/target/bank-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
