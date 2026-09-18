import java.nio.file.Paths

plugins {
    kotlin("jvm") version "2.4.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.typho"
version = "1.0.3"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://typho.net/maven")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

dependencies {
}

gradlePlugin {
    plugins {
        create("typho_publish") {
            id = "net.typho.typho_publish"
            implementationClass = "net.typho.typho_publish.TyphoPublishPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "typho"
            url = Paths.get(project.findProperty("typho_publish.website_dir") as String).resolve("maven").toUri()
        }
    }
}