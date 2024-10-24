FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
ARG ENV
COPY . .
RUN chmod +x gradlew
RUN ./gradlew -P ENVIRONMENT_NAME=$ENV build -x validateStructure test
RUN mv applications/app-service/build/libs/vd-loans-documents-msa.jar app.jar

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/app.jar .
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]