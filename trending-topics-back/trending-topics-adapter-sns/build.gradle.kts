plugins {
    id("java-library")
}

group = "io.whispers.trending"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":trending-topics-domain"))
    api("com.amazonaws:aws-java-sdk-sns:1.12.603")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testcontainers:localstack:1.19.3")
}

tasks.test {
    useJUnitPlatform()
}

ext {
    set("testcontainersVersion", "1.18.0")
}
