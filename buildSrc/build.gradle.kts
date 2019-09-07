import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    id("kotlinx-serialization") version "1.3.50"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

repositories {
    jcenter()
}

dependencyManagement {
    imports {
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.3.1")
        mavenBom("io.ktor:ktor-bom:1.2.4")
    }
    dependencies {
        dependency("org.eclipse.jgit:org.eclipse.jgit:5.4.3.201909031940-r")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.12.0")
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
