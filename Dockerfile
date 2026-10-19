FROM maven:3.9.16-eclipse-temurin-17

WORKDIR /app

COPY .mvn/ .mvn/
COPY pom.xml ./
RUN mvn -B -ntp org.apache.maven.plugins:maven-dependency-plugin:3.8.1:go-offline

COPY src/ src/
RUN mvn -B -ntp -DskipTests test

CMD ["mvn", "-B", "-ntp", "test"]
