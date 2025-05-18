package rut.uvp.family

import rut.uvp.family.domain.model.Family
import rut.uvp.family.domain.model.FamilyMember
import rut.uvp.family.domain.model.Gender
import rut.uvp.family.domain.model.Relationship
import rut.uvp.family.infrastructure.InMemoryFamilyRepositoryImpl
import rut.uvp.family.service.FamilyServiceImpl
import java.util.*
import kotlin.test.Test

internal class FamilyServiceTest {

    private val family: Family
    private val familyRepository = InMemoryFamilyRepositoryImpl()
    private val familyService = FamilyServiceImpl(familyRepository = familyRepository)

    private val son = createMember(
        name = "Сын (Я)",
        gender = Gender.MALE,
        relationship = Relationship(levelRelation = 1, levelProximity = 0)
    )
    private val cousin = createMember(
        name = "Двоюродный брат",
        gender = Gender.MALE,
        relationship = Relationship(levelRelation = 1, levelProximity = 1)
    )

    init {
        family = createInitialFamily()

        familyService.addMember(
            family = family,
            member = son,
        )
        familyService.addMember(
            family = family,
            member = cousin,
        )
    }

    @Test
    fun `get brother by son`() {
        val members = familyService.findMembersByRelationship(
            family = family,
            relativeFamilyMember = son,
            relationship = Relationship(levelRelation = 0, levelProximity = 0),
            gender = Gender.MALE
        )

        println("get brother by son: $members")
        requireNotNull(members.find { member -> member.name == "Брат" })
    }

    @Test
    fun `get grandfather by son`() {
        val members = familyService.findMembersByRelationship(
            family = family,
            relativeFamilyMember = son,
            relationship = Relationship(levelRelation = -2, levelProximity = 0),
            gender = Gender.MALE
        )

        println("get grandfather by son: $members")
        requireNotNull(members.find { member -> member.name == "Дедушка" })
    }

    @Test
    fun `get cousin sister by son`() {
        val members = familyService.findMembersByRelationship(
            family = family,
            relativeFamilyMember = son,
            relationship = Relationship(levelRelation = 0, levelProximity = 1),
            gender = Gender.FEMALE
        )

        println("get cousin sister by son: $members")
        requireNotNull(members.find { member -> member.name == "Двоюродная сестра" })
    }

    @Test
    fun `get son by cousin`() {
        val members = familyService.findMembersByRelationship(
            family = family,
            relativeFamilyMember = cousin,
            relationship = Relationship(levelRelation = 0, levelProximity = 1),
            gender = Gender.MALE
        )

        println("get son by cousin: $members")
        requireNotNull(members.find { member -> member.name == son.name })
    }

    private fun createInitialFamily(): Family {
        val owner = createMember(
            name = "Отец (Создатель)",
            gender = Gender.MALE,
            relationship = Relationship(0, 0),
        )

        val family = familyService.createFamily(owner = owner, familyName = "Семья для теста")

        familyService.addMember(
            family = family,
            createMember(
                name = "Мать",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = 0, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Дядя",
                gender = Gender.MALE,
                relationship = Relationship(levelRelation = 0, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Тетя",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = 0, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Жена",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = 1, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Сестра",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = 1, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Брат",
                gender = Gender.MALE,
                relationship = Relationship(levelRelation = 1, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Двоюродная сестра",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = 1, levelProximity = 1)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Дедушка",
                gender = Gender.MALE,
                relationship = Relationship(levelRelation = -1, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Бабушка",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = -1, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Двоюродный дед",
                gender = Gender.MALE,
                relationship = Relationship(levelRelation = -1, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Двоюродная бабушка",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = -1, levelProximity = 0)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Двоюродный дядя",
                gender = Gender.MALE,
                relationship = Relationship(levelRelation = 0, levelProximity = 1)
            )
        )
        familyService.addMember(
            family = family,
            createMember(
                name = "Двоюродная тетя",
                gender = Gender.FEMALE,
                relationship = Relationship(levelRelation = 0, levelProximity = 1)
            )
        )

        return family
    }

    private fun createMember(
        name: String,
        relationship: Relationship,
        gender: Gender = Gender.MALE,
    ): FamilyMember = FamilyMember(
        id = UUID.randomUUID().toString(),
        userId = UUID.randomUUID().toString(),
        name = name,
        gender = gender,
        family = emptySet(),
        relationship = relationship,
        preferences = emptyList(),
    )
}