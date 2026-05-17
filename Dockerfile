FROM maven:3.9.12-eclipse-temurin-25 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]