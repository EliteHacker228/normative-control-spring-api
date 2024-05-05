FROM gradle:8.5.0-jdk21-alpine AS build
RUN mkdir -p /app/sources
RUN mkdir -p /app/built
WORKDIR /app/sources
COPY / /app/sources
RUN gradle bootJar

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S instance && adduser -S maea -G instance
USER maea
ENV AMQP_HOST # localhost
ENV AMQP_PASSWORD # normativecontrol
ENV AMQP_PORT # 5672
ENV AMQP_QUEUE to_be_verified
ENV AMQP_USERNAME # maeasoftworks
ENV DB_PASSWORD # maeasoftworks
ENV DB_URL # jdbc:postgresql://localhost:5432/normative-control
ENV DB_USERNAME # postgres
ENV S3_ACCEESS_KEY_ID # pdh5niPCbqYxGW5BKxGv
ENV S3_BUCKET normative-control
ENV S3_ENDPOINT # http://localhost:9000
ENV S3_REGION US-EAST-1
ENV S3_SECRET_ACCESS_KEY # xqtc6gXpKy0OIZVVSeinxDd7dd0pbDsOEtU7huJX
ENV API_PREFIX # /api

COPY --from=build /app/sources/build/libs/*.jar /app/built/app.jar
ENTRYPOINT java -jar app/built/app.jar