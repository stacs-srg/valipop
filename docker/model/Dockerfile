FROM maven:3.9.9-eclipse-temurin-21-jammy AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml dependency:resolve-plugins dependency:resolve clean package -Dmaven.test.skip -Dmaven.repo.local=repository

COPY src/main/java src/main/java

# We need the R code packcaged in the jar file
COPY src/main/resources/valipop/analysis-r/geeglm src/main/resources/valipop/analysis-r/geeglm

RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml clean package -Dmaven.test.skip -Dmaven.repo.local=repository

FROM valipop-base AS run

# Only extract the Jar file
COPY --from=build /app/target/population-model-4.0-SNAPSHOT-jar-with-dependencies.jar ./valipop.jar

COPY docker/model/entrypoint.sh .

ENTRYPOINT [ "./entrypoint.sh" ]
