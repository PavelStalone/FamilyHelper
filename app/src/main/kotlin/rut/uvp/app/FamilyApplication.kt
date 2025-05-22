package rut.uvp.app

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import rut.uvp.core.common.log.Log
import rut.uvp.family.domain.model.FamilyMember
import rut.uvp.family.domain.model.Gender
import rut.uvp.family.domain.model.Relationship
import rut.uvp.family.service.FamilyService

@EntityScan("rut.uvp")
@EnableJpaRepositories(basePackages = ["rut.uvp"])
@SpringBootApplication(scanBasePackages = ["rut.uvp"])
class FamilyApplication {

    @Bean
    fun familyCreator(
        familyService: FamilyService
    ) = CommandLineRunner { _ ->
        val father = FamilyMember(
            id = "TestFather",
            name = "Владимир",
            userId = "1",
            gender = Gender.MALE,
            relationship = Relationship(0, 0),
            preferences = emptyList()
        )

        val son = FamilyMember(
            id = "TestSon",
            name = "Игорь",
            userId = "2",
            gender = Gender.MALE,
            relationship = Relationship(1, 0),
            preferences = emptyList()
        )

        val family = familyService.createFamily(father, "TestFamily")
        familyService.addMember(family, son)

        Log.i("TestFamily: $family")
    }
}

fun main(args: Array<String>) {
    runApplication<FamilyApplication>(*args)
}