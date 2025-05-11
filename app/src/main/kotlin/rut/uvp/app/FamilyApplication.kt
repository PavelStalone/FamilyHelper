package rut.uvp.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["rut.uvp"])
@EntityScan("rut.uvp.core.data.entity")
class FamilyApplication

fun main(args: Array<String>) {
    runApplication<FamilyApplication>(*args)
}