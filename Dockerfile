FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN useradd --system --no-create-home app
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
