plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "io.whispers"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":whispers-adapter-web"))
    implementation(project(":whispers-adapter-dynamo"))
    implementation(project(":whispers-adapter-sns"))
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}