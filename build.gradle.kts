import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    java
    id("org.springframework.boot") version "2.1.0.M4"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

group = "org.saigon"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

springBoot {
    buildInfo()
}

repositories {
    jcenter()
    maven("http://repo.spring.io/milestone")
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-web")

    runtime("com.h2database:h2")
    runtime("org.flywaydb:flyway-core")
    runtime("org.postgresql:postgresql")

    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("org.springframework.boot:spring-boot-starter-webflux")
}

tasks {
    val test by existing(Test::class)
    val clean by existing
    val bootJar by existing
    val stage by registering

    test {
        val serverUrl = System.getProperty("serverUrl")
        if (serverUrl != null) {
            systemProperty("serverUrl", serverUrl)
            filter.includeTestsMatching("*IT")
        } else {
            filter.includeTestsMatching("*Test")
        }

        testLogging.events = setOf(PASSED, SKIPPED, FAILED)
        testLogging.exceptionFormat = FULL
    }

    bootJar {
        mustRunAfter(clean)
    }

    // task run by heroku
    stage {
        dependsOn(clean, bootJar)
    }
}
