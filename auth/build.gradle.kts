plugins {
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "1.9.25"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.auth0:java-jwt:4.4.0")

    implementation(project(":core:common"))
    implementation(project(":core:data"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
