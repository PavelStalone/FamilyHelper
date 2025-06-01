plugins {
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("plugin.serialization") version "2.1.20"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jsoup:jsoup:1.17.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":core:ai"))
    implementation(project(":core:data"))
    implementation(project(":core:common"))

    implementation(project(":search-activity"))
    implementation(project(":deepsearch"))
    implementation(project(":calendar"))
    implementation(project(":family"))
    implementation(project(":auth"))
}
