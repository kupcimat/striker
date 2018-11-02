rootProject.name = "striker"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("http://repo.spring.io/milestone")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.springframework.boot") {
                useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
            }
        }
    }
}