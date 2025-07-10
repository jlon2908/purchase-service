FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/purchase-service-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

