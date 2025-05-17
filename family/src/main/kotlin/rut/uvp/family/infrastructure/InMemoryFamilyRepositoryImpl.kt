package rut.uvp.family.infrastructure

import org.springframework.stereotype.Component
import rut.uvp.family.domain.model.Family
import rut.uvp.family.domain.model.FamilyMember
import rut.uvp.family.domain.model.Gender
import rut.uvp.family.domain.model.Relationship
import rut.uvp.family.domain.repository.FamilyRepository
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

@Component
internal class InMemoryFamilyRepositoryImpl : FamilyRepository {

    private val store: MutableMap<Family, MutableList<FamilyMember>> = mutableMapOf()
    private val familyCodeStore: MutableMap<String, Family> =
        mutableMapOf() // Use TTL system for this (for example Redis)

    override fun getById(familyId: String): Family? {
        return store.firstNotNullOfOrNull { (family, _) -> family.takeIf { family.id == familyId } }
    }

    override fun createFamilyCode(family: Family): String {
        var code: String

        do {
            code = Random.nextInt(100_000..999_999).toString()
        } while (!familyCodeStore.contains(code))

        if (familyCodeStore.size > 900_000) familyCodeStore.clear()

        familyCodeStore[code] = family
        return code
    }

    override fun getByFamilyCode(familyCode: String): Family? {
        return familyCodeStore[familyCode]
    }

    override fun addMember(family: Family, member: FamilyMember) {
        store.getOrPut(family) { mutableListOf() }
            .apply { add(member.copy(family = member.family + family)) }
    }

    override fun createFamily(owner: FamilyMember, familyName: String): Family {
        val family = Family(
            id = UUID.randomUUID().toString(),
            name = familyName,
            ownerId = owner.id,
        )

        store[family] = mutableListOf()
        addMember(family, owner)

        return family
    }

    override fun getMembersByRelationship(
        family: Family,
        relationship: Relationship,
        gender: Gender,
        name: String?
    ): List<FamilyMember> {
        val members = store[family]
        requireNotNull(members) { "This family is not in the store" }

        return members.filter { member -> member.relationship == relationship }
            .filter { member -> member.gender == gender }
            .run {
                name?.let { memberName ->
                    filter { member -> member.name == memberName }
                } ?: this
            }
    }
}
