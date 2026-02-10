FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY _contracts/pom.xml _contracts/pom.xml
RUN mvn -q -DskipTests -f _contracts/pom.xml dependency:go-offline

COPY _contracts _contracts
RUN mvn -q -DskipTests -f _contracts/pom.xml install

COPY RoomService/pom.xml RoomService/pom.xml
RUN mvn -q -DskipTests -f RoomService/pom.xml dependency:go-offline

COPY RoomService RoomService
RUN mvn -q -DskipTests -f RoomService/pom.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/RoomService/target/*.jar app.jar

EXPOSE 8083
EXPOSE 7072
ENTRYPOINT ["java","-jar","/app/app.jar"]
