FROM openjdk:17-jdk-alpine
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/crud.jar"]

