import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

group = "org.saigon"
version = "0.0.1-SNAPSHOT"

plugins {
    java
    groovy
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    kotlin("plugin.spring") version "1.3.31"
    id("org.springframework.boot") version "2.2.0.M3"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

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

dependencyManagement {
    dependencies {
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")
        dependency("org.codehaus.groovy:groovy-all:2.5.7")
        dependencySet("org.spockframework:1.3-groovy-2.5") {
            entry("spock-core")
            entry("spock-spring")
        }
        dependency("net.javacrumbs.json-unit:json-unit:2.6.1")
    }
}

dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.apache.commons:commons-lang3")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    runtimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("net.javacrumbs.json-unit:json-unit")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "12"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    test {
        useJUnitPlatform()

        testLogging {
            events = setOf(PASSED, SKIPPED, FAILED)
            exceptionFormat = FULL
        }

        if (System.getProperty("serverUrl") != null) {
            systemProperty("serverUrl", System.getProperty("serverUrl"))
            systemProperty("username", System.getProperty("username"))
            systemProperty("password", System.getProperty("password"))
            filter.includeTestsMatching("*IT")
        } else {
            filter.includeTestsMatching("*Test")
        }
    }
}
