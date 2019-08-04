import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    id("kotlinx-serialization") version "1.3.41"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

repositories {
    jcenter()
}

dependencyManagement {
    dependencies {
        dependency("org.eclipse.jgit:org.eclipse.jgit:5.4.0.201906121030-r")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.2")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")
        dependency("io.ktor:ktor-client-cio:1.2.3")
        dependency("io.ktor:ktor-client-serialization-jvm:1.2.3")
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
