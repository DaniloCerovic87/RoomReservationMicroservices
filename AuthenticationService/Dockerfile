FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY AuthenticationService/pom.xml AuthenticationService/pom.xml
RUN mvn -q -DskipTests -f AuthenticationService/pom.xml dependency:go-offline

COPY AuthenticationService AuthenticationService
RUN mvn -q -DskipTests -f AuthenticationService/pom.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/AuthenticationService/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
