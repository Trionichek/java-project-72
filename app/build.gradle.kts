plugins {
    jacoco
    application
    checkstyle
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    id("com.github.ben-manes.versions") version "0.47.0"
    id("io.freefair.lombok") version "8.3"
}
application {mainClass.set("hexlet.code.App")}


group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("com.h2database:h2:2.2.220")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.javalin:javalin:5.6.1")
    implementation("gg.jte:jte:3.0.1")
    implementation("io.javalin:javalin-bundle:6.1.3")
    implementation("io.javalin:javalin-rendering:5.6.2")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("com.konghq:unirest-java:3.14.1")
    implementation("org.jsoup:jsoup:1.17.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    implementation("info.picocli:picocli:4.7.5")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
    annotationProcessor("info.picocli:picocli-codegen:4.7.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}