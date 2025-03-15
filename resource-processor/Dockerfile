FROM maven:3.9.2-eclipse-temurin-17-alpine AS build

WORKDIR ./app

COPY pom.xml .
COPY src ./src

RUN mvn dependency:go-offline
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine

WORKDIR ./app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8084

CMD ["java", "-jar", "app.jar"]