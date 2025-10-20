FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/bank-cards-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]