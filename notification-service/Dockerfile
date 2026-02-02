FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY payment-service payment-service
COPY wallet-service wallet-service
COPY ledger-service ledger-service
COPY notification-service notification-service
RUN mvn clean package -DskipTests -pl notification-service -am

FROM eclipse-temurin:17-jre
VOLUME /tmp
COPY --from=build /app/notification-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
