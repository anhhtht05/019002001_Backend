# Stage 1: Build the Spring Boot application
# Uses a JDK image from Eclipse Temurin for compilation.
FROM eclipse-temurin:17-jdk-focal AS builder

# Set working directory inside container
WORKDIR /app

# Copy Maven wrapper if bạn có dùng mvnw
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml
COPY pom.xml .

# Download dependencies (offline) để tăng tốc build
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the Spring Boot JAR, skip tests để nhanh hơn
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy JAR từ stage build
COPY --from=builder /app/target/000902001_Backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render sẽ set biến PORT)
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
