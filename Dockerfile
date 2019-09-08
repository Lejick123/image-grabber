FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
ENTRYPOINT ["java","-cp","image-grabber:image-grabber/src/main/java/*","portal.Application"]