FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY src src
RUN mvn -B -ntp package -DskipTests

FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app
COPY --from=build /workspace/target/iam-policy-service-*.jar app.jar
USER nonroot:nonroot
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
