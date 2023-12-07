plugins {
    id("java")
}

group = "io.whispers.trending"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":trending-topics-back-domain"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}