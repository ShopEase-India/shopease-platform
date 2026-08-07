FROM eclipse-temurin:21-jre

LABEL maintainer="ShopEase DevOps Team"
LABEL application="api-gateway"
LABEL version="1.0.0"

WORKDIR /app

COPY backend/api-gateway/target/api-gateway-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]