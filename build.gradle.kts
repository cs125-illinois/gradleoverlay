plugins {
    kotlin("jvm") version "1.4.32"
    `java-gradle-plugin`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.jmailen.kotlinter") version "3.4.0"
    id("com.github.ben-manes.versions") version "0.38.0"
}

group = "com.github.cs125-illinois"
version = "2021.3.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(gradleApi())
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.2")
}
gradlePlugin {
    plugins {
        create("plugin") {
            id = "com.github.cs125-illinois.gradleoverlay"
            implementationClass = "edu.illinois.cs.cs125.gradleoverlay.Plugin"
        }
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.dependencyUpdates {
    resolutionStrategy {
        componentSelection {
            all {
                if (listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "eap", "pr").any { qualifier ->
                        candidate.version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+]*"))
                    }) {
                    reject("Release candidate")
                }
            }
        }
    }
    gradleReleaseChannel = "current"
}
