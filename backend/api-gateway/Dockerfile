FROM eclipse-temurin:21-jre

LABEL maintainer="ShopEase DevOps Team"
LABEL application="api-gateway"
LABEL version="1.0.0"

WORKDIR /app

COPY target/api-gateway-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]