import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

group = "org.saigon"
version = "0.0.1-SNAPSHOT"

plugins {
    java
    kotlin("jvm") version "1.3.31"
    id("org.springframework.boot") version "2.2.0.M2"
}

apply(plugin = "io.spring.dependency-management")

java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
}

springBoot {
    buildInfo()
}

repositories {
    jcenter()
    maven("http://repo.spring.io/milestone")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.apache.commons:commons-lang3")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    runtimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks {
    test {
        if (System.getProperty("serverUrl") != null) {
            systemProperty("serverUrl", System.getProperty("serverUrl"))
            systemProperty("username", System.getProperty("username"))
            systemProperty("password", System.getProperty("password"))
            filter.includeTestsMatching("*IT")
        } else {
            filter.includeTestsMatching("*Test")
        }

        testLogging.events = setOf(PASSED, SKIPPED, FAILED)
        testLogging.exceptionFormat = FULL
    }
}
