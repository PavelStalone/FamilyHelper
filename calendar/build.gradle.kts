plugins {
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("plugin.serialization") version "2.1.20"
    kotlin("plugin.jpa") version "1.9.25"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":core:common"))
    implementation(project(":core:data"))
}
