FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY baopu-common/pom.xml ./baopu-common/pom.xml
COPY baopu-gateway/pom.xml ./baopu-gateway/pom.xml
COPY baopu-iam-service/pom.xml ./baopu-iam-service/pom.xml
COPY baopu-dingtalk-service/pom.xml ./baopu-dingtalk-service/pom.xml
COPY baopu-learning-service/pom.xml ./baopu-learning-service/pom.xml
RUN mvn -pl baopu-learning-service -am dependency:go-offline
COPY baopu-learning-service/src ./baopu-learning-service/src
RUN mvn -pl baopu-learning-service -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/baopu-learning-service/target/baopu-learning-service-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
