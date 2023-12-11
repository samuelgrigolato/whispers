plugins {
    id("java-library")
}

group = "io.whispers.trending"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":trending-topics-app"))
    api("software.amazon.lambda:powertools-batch:1.18.0")
    api("com.amazonaws:aws-lambda-java-core:1.2.3")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}