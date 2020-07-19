import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.saigon.striker.gradle.UpgradeDependenciesTask

group = "org.saigon"
version = "release"

plugins {
    java
    groovy
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
    id("org.springframework.boot") version "2.4.0-M1"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.gorylenko.gradle-git-properties") version "2.2.2"
    id("com.google.cloud.tools.jib") version "2.4.0"
}

buildscript {
    dependencies {
        // TODO temporarily override apache http client version until jib upgrades it
        classpath("org.apache.httpcomponents:httpclient:4.5.12")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

springBoot {
    buildInfo {
        properties {
            time = null
        }
    }
}

gitProperties {
    keys = listOf("git.branch", "git.commit.id", "git.commit.time")
}

jib {
    from {
        image = "gcr.io/distroless/java:11"
    }
    to {
        image = "registry.heroku.com/striker-vn/web"
    }
    container {
        args = listOf("--spring.profiles.active=heroku")
        jvmFlags = listOf("-Xmx300m", "-Xss512k", "-XX:CICompilerCount=2", "-Dfile.encoding=UTF-8")
    }
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://repo.spring.io/milestone")
}

dependencyManagement {
    imports {
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.8")
        mavenBom("org.spockframework:spock-bom:2.0-M3-groovy-3.0")
        mavenBom("io.ktor:ktor-bom:1.3.2")
    }
    dependencies {
        dependency("org.codehaus.groovy:groovy-all:3.0.4")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
        dependency("io.mockk:mockk:1.9.3")
        dependency("com.charleskorn.kaml:kaml:0.17.0")
        dependency("net.javacrumbs.json-unit:json-unit:2.18.1")
    }
}

// Override spring-boot-dependencies bom version for groovy
extra["groovy.version"] = "3.0.4"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime")
    testImplementation("net.javacrumbs.json-unit:json-unit")
    testImplementation("com.charleskorn.kaml:kaml")
    testImplementation("io.ktor:ktor-server-netty")
    testImplementation("io.mockk:mockk")

    testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

configurations {
    testImplementation {
        exclude(group = "junit", module = "junit")
    }
}

tasks {
    register<UpgradeDependenciesTask>(
        "upgradeDependencies",
        project.hasProperty("createPullRequest"),
        project.findProperty("githubUsername")?.toString() ?: "",
        project.findProperty("githubToken")?.toString() ?: "",
        listOf("./build.gradle.kts", "./buildSrc/build.gradle.kts")
    )

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    compileTestGroovy {
        // Groovy tests depend on kotlin test utils
        classpath += files(compileTestKotlin.get().destinationDirectory)
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
