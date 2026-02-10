FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY CalendarService/pom.xml CalendarService/pom.xml
RUN mvn -q -DskipTests -f CalendarService/pom.xml dependency:go-offline

COPY CalendarService CalendarService
RUN mvn -q -DskipTests -f CalendarService/pom.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/CalendarService/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","/app/app.jar"]
