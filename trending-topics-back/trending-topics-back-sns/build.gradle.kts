plugins {
    id("java-library")
}

group = "io.whispers.trending"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":trending-topics-back-domain"))
    api("com.amazonaws:aws-java-sdk-sns:1.12.603")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}