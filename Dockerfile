FROM openjdk:17-jdk-slim
VOLUME /tmp
WORKDIR /app
ARG JAR_FILE=build/libs/farm-dora-product-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} farm-dora-product.jar
ENV JASYPT_KEY=${JASYPT_KEY}
EXPOSE 8050
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "farm-dora-product.jar"]