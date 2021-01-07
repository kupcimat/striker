import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30-M1"
    kotlin("plugin.serialization") version "1.4.30-M1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencyManagement {
    imports {
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.4.2")
        mavenBom("io.ktor:ktor-bom:1.4.1")
    }
    dependencies {
        dependency("org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.eclipse.jgit:org.eclipse.jgit")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-serialization-jvm")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=kotlin.Experimental")
        }
    }
}
