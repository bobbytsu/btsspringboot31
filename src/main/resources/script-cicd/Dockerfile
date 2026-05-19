FROM eclipse-temurin:21-jre
WORKDIR /app

COPY target/paul-api.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -DDB_URL=\"$DB_URL\" -DDB_USERNAME=\"$DB_USERNAME\" -DDB_PASSWORD=\"$DB_PASSWORD\" -jar /app/app.jar"]