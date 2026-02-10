FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY NotificationService/pom.xml NotificationService/pom.xml
RUN mvn -q -DskipTests -f NotificationService/pom.xml dependency:go-offline

COPY NotificationService NotificationService
RUN mvn -q -DskipTests -f NotificationService/pom.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/NotificationService/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
