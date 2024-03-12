FROM eclipse-temurin:21
WORKDIR /usr/src/movieservice2
COPY ./target/movieservice2-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "movieservice2-0.0.1-SNAPSHOT.jar"]