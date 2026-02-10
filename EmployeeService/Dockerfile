FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY _contracts/pom.xml _contracts/pom.xml
RUN mvn -q -DskipTests -f _contracts/pom.xml dependency:go-offline

COPY _contracts _contracts
RUN mvn -q -DskipTests -f _contracts/pom.xml install

COPY EmployeeService/pom.xml EmployeeService/pom.xml
RUN mvn -q -DskipTests -f EmployeeService/pom.xml dependency:go-offline

COPY EmployeeService EmployeeService
RUN mvn -q -DskipTests -f EmployeeService/pom.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/EmployeeService/target/*.jar app.jar

EXPOSE 8082
EXPOSE 7071

ENTRYPOINT ["java","-jar","/app/app.jar"]
