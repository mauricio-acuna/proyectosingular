FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/ai-readiness-web-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
