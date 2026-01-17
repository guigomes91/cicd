FROM eclipse-temurin:17-jre

COPY target/cicd-api.jar cicd-api.jar

ENTRYPOINT ["java","-jar","/cicd-api.jar"]