package rut.uvp.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan("rut.uvp")
@EnableJpaRepositories(basePackages = ["rut.uvp"])
@SpringBootApplication(scanBasePackages = ["rut.uvp"])
class FamilyApplication

fun main(args: Array<String>) {
    runApplication<FamilyApplication>(*args)
}