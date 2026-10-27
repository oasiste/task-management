FROM gradle:8.14-jdk21 AS build
WORKDIR /project
COPY . .
RUN gradle :task-service:bootJar --no-daemon
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /project/task-service/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
