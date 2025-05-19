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
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.retry:spring-retry:1.3.4")
    implementation("org.springframework:spring-aspects")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.jsoup:jsoup:1.17.2")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}