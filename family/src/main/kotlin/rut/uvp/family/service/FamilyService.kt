package rut.uvp.family.service

import org.springframework.stereotype.Service
import rut.uvp.family.domain.model.Family
import rut.uvp.family.domain.model.FamilyMember
import rut.uvp.family.domain.model.Gender
import rut.uvp.family.domain.model.Relationship
import rut.uvp.family.domain.repository.FamilyRepository

interface FamilyService {

    fun getFamilyById(familyId: String): Family?
    fun createFamilyCode(family: Family): String
    fun findFamilyByCode(familyCode: String): Family?
    fun addMember(family: Family, member: FamilyMember)
    fun createFamily(owner: FamilyMember, familyName: String): Family
    fun getMembersByRelationship(
        family: Family,
        relativeFamilyMember: FamilyMember,
        relationship: Relationship,
        gender: Gender,
        name: String? = null,
    ): List<FamilyMember>
}

@Service
internal class FamilyServiceImpl(
    private val familyRepository: FamilyRepository,
) : FamilyService {

    override fun getFamilyById(familyId: String): Family? {
        return familyRepository.getById(familyId)
    }

    override fun createFamilyCode(family: Family): String {
        return familyRepository.createFamilyCode(family)
    }

    override fun findFamilyByCode(familyCode: String): Family? {
        return familyRepository.getByFamilyCode(familyCode)
    }

    override fun addMember(family: Family, member: FamilyMember) {
        familyRepository.addMember(family, member)
    }

    override fun createFamily(owner: FamilyMember, familyName: String): Family {
        return familyRepository.createFamily(owner, familyName)
    }

    override fun getMembersByRelationship(
        family: Family,
        relativeFamilyMember: FamilyMember,
        relationship: Relationship,
        gender: Gender,
        name: String?
    ): List<FamilyMember> {
        val absoluteRelationships = with(relativeFamilyMember.relationship) {
            val relations = mutableListOf(
                Relationship(
                    levelRelation = levelRelation + relationship.levelRelation,
                    levelProximity = levelProximity + relationship.levelProximity,
                )
            )

            if (relationship.levelProximity > 0) {
                runCatching {
                    relations.add(
                        Relationship(
                            levelRelation = levelRelation + relationship.levelRelation,
                            levelProximity = levelProximity - relationship.levelProximity,
                        )
                    )
                }
            }

            relations
        }.toList()

        return absoluteRelationships.flatMap { absoluteRelationship ->
            familyRepository.getMembersByRelationship(
                family = family,
                relationship = absoluteRelationship,
                gender = gender,
                name = name
            )
        }
    }
}
