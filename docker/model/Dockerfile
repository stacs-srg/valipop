FROM maven:3.9.9-eclipse-temurin-21-jammy AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:resolve

COPY src/main/java src/main/java

RUN mvn package -Dmaven.test.skip -Dmaven.repo.local=repository

FROM valipop-base AS run

# Only extract the Jar file
COPY --from=build /app/target/population-model-4.0-SNAPSHOT-jar-with-dependencies.jar ./valipop.jar

COPY docker/model/entrypoint.sh .

ENTRYPOINT [ "./entrypoint.sh" ]
