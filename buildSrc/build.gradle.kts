import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0-rc"
    kotlin("plugin.serialization") version "1.4.0-rc"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencyManagement {
    imports {
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.8")
        mavenBom("io.ktor:ktor-bom:1.3.2")
    }
    dependencies {
        dependency("org.eclipse.jgit:org.eclipse.jgit:5.8.1.202007141445-r")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.eclipse.jgit:org.eclipse.jgit")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime")
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
