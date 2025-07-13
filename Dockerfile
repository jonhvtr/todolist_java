FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/todolist-0.0.1-SNAPSHOT.jar app.jar

ENV DB_HOST=localhost \
    DB_NAME=todolist \
    DB_USERNAME=user_local \
    DB_PASSWORD=123

CMD ["java", "-jar", "app.jar"]