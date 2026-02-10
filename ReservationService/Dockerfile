FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY _contracts/pom.xml _contracts/pom.xml
RUN mvn -q -DskipTests -f _contracts/pom.xml dependency:go-offline

COPY _contracts _contracts
RUN mvn -q -DskipTests -f _contracts/pom.xml install

COPY ReservationService/pom.xml ReservationService/pom.xml
RUN mvn -q -DskipTests -f ReservationService/pom.xml dependency:go-offline

COPY ReservationService ReservationService
RUN mvn -q -DskipTests -f ReservationService/pom.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/ReservationService/target/*.jar app.jar

EXPOSE 8080
EXPOSE 7073

ENTRYPOINT ["java","-jar","/app/app.jar"]
