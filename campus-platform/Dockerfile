# ============================================
# 校园综合服务平台 — Dockerfile
# ============================================
# 用法（先在项目根目录 mvn clean package -DskipTests）：
#   docker build --build-arg MODULE=campus-gateway -t campus-gateway .
#   docker build --build-arg MODULE=user-service -t user-service .
# ============================================

FROM eclipse-temurin:17-jre-alpine

# 设置时区
RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 接收模块名参数
ARG MODULE
ENV MODULE=${MODULE}

WORKDIR /app

# 复制目标 JAR（Maven 已在外部构建好）
COPY ${MODULE}/target/${MODULE}-*.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "app.jar"]
