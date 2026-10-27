FROM gradle:8.14-jdk21 AS build
WORKDIR /project
COPY . .
RUN gradle :auth-service:bootJar --no-daemon
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /project/auth-service/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
