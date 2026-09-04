FROM maven:3.9.16-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml ./

# Copy Java source code and the R analysis
COPY src/main/java src/main/java
COPY src/main/resources/R/geeglm src/main/resources/R/geeglm

RUN mvn clean package -Dmaven.test.skip=true

# Extract version from pom.xml
RUN VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) && \
    echo "VERSION=$VERSION" && \
    mv target/valipop-${VERSION}-jar-with-dependencies.jar ./valipop.jar

# Create runtime image
FROM valipop-base AS run

COPY --from=build /app/valipop.jar ./valipop.jar
COPY docker/model/entrypoint.sh .

ENTRYPOINT [ "./entrypoint.sh" ]
