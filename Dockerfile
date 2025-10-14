FROM eclipse-temurin:21.0.2_13-jdk-jammy as build

WORKDIR /build

# Копируем исходный код
COPY pom.xml .
COPY src ./src

# Собираем приложение
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

FROM eclipse-temurin:21.0.2_13-jre-jammy

RUN addgroup spring-boot-group && adduser --ingroup spring-boot-group spring-boot
USER spring-boot:spring-boot-group
WORKDIR /application

COPY --from=build /build/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]