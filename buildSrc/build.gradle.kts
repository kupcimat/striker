import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencyManagement {
    imports {
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.2-1.3.60")
        mavenBom("io.ktor:ktor-bom:1.2.6")
    }
    dependencies {
        dependency("org.eclipse.jgit:org.eclipse.jgit:5.5.1.201910021850-r")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
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
