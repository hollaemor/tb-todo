FROM maven:3-eclipse-temurin-25-alpine AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

RUN mvn package -DskipTests

FROM gcr.io/distroless/java25-debian13

COPY --from=builder /app/target/*.jar /app/todo.jar

WORKDIR /app

EXPOSE 8080
CMD ["todo.jar"]
