import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.saigon.striker.gradle.PrintBuildVersionTask
import org.saigon.striker.gradle.UpgradeDependenciesTask
import org.saigon.striker.gradle.getBuildVersion

group = "org.saigon"
version = "0.0.1-SNAPSHOT"

plugins {
    java
    groovy
    kotlin("jvm") version "1.3.50"
    kotlin("kapt") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
    id("kotlinx-serialization") version "1.3.50"
    id("org.springframework.boot") version "2.2.0.M5"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("com.google.cloud.tools.jib") version "1.5.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

springBoot {
    buildInfo {
        properties {
            val commit = getBuildVersion(projectDir)
            time = commit.time
            version = commit.hash
        }
    }
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
    jcenter()
    maven("http://repo.spring.io/milestone")
}

dependencyManagement {
    imports {
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.0")
        mavenBom("io.ktor:ktor-bom:1.2.3")
    }
    dependencies {
        dependency("org.codehaus.groovy:groovy-all:2.5.8")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.12.0")
        // TODO exclude junit4 dependency when migrated to spock2 (see bd97d9d)
        dependency("org.spockframework:spock-core:1.3-groovy-2.5")
        dependency("org.spockframework:spock-spring:1.3-groovy-2.5")
        dependency("io.mockk:mockk:1.9.3")
        dependency("com.charleskorn.kaml:kaml:0.12.0")
        dependency("net.javacrumbs.json-unit:json-unit:2.8.0")
    }
}

dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor")

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
    testImplementation("net.javacrumbs.json-unit:json-unit")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime")
    testImplementation("com.charleskorn.kaml:kaml")
    testImplementation("io.ktor:ktor-server-netty")
    testImplementation("io.mockk:mockk")

    testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
}

tasks {
    register<PrintBuildVersionTask>("printBuildVersion")
    register<UpgradeDependenciesTask>("upgradeDependencies") {
        createPullRequest = project.hasProperty("createPullRequest")
        githubUsername = project.findProperty("githubUsername")?.toString() ?: ""
        githubToken = project.findProperty("githubToken")?.toString() ?: ""
        buildFiles = listOf("./build.gradle.kts", "./buildSrc/build.gradle.kts")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    compileTestGroovy {
        // Groovy tests depend on kotlin test utils
        classpath += files(compileTestKotlin.get().destinationDir)
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
