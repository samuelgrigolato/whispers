plugins {
    id("java")
}

group = "io.whispers.trending"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":trending-topics-adapter-lambda"))
    implementation(project(":trending-topics-adapter-redis"))
    implementation(project(":trending-topics-adapter-sns"))
    implementation(project(":trending-topics-adapter-simple-resolver"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Zip>("buildZip") {
    into("lib") {
        from(tasks.jar)
        from(configurations.runtimeClasspath)
    }
}