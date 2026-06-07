FROM eclipse-temurin:17-jre-alpine
ARG JAR_PATH
RUN apk add --no-cache curl tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
WORKDIR /app
COPY ${JAR_PATH} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xms128m", "-Xmx384m", "-jar", "app.jar"]
