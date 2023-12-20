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
    api("org.redisson:redisson:3.25.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.redis:testcontainers-redis:2.0.1")
}

tasks.test {
    useJUnitPlatform()
}