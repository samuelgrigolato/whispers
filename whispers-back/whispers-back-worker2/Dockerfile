FROM amazoncorretto:21

# remember to build the jar first (./gradlew bootJar at the root of the project)
ADD build/libs/*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
