FROM openjdk:8 AS build

RUN mkdir /appbuild
COPY . /appbuild

WORKDIR /appbuild

RUN chmod +x gradlew
RUN ./gradlew clean build -x test

FROM openjdk:8-jre-alpine

ENV APPLICATION_USER 1033
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN mkdir /app/resources
RUN chown -R $APPLICATION_USER /app
RUN chmod -R 755 /app

USER $APPLICATION_USER

COPY --from=build /appbuild/build/libs/align-dating-server.jar /app/alignDatingBackend.jar
COPY --from=build /appbuild/resources/ /app/resources/
WORKDIR /app

CMD ["sh", "-c", "java -server -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar alignDatingBackend.jar"]