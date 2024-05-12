FROM maven:3.8.5-openjdk-17 as builder
COPY ./src /tmp/src
COPY ./pom.xml /tmp/pom.xml
RUN cd /tmp && \
  mvn clean package

FROM openjdk:17-alpine
COPY --from=builder /tmp/target/*.jar /app/app.jar
CMD java -jar /app/app.jar