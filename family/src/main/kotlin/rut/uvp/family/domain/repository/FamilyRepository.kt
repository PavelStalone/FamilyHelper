package rut.uvp.family.domain.repository

import rut.uvp.family.domain.model.Family
import rut.uvp.family.domain.model.FamilyMember
import rut.uvp.family.domain.model.Gender
import rut.uvp.family.domain.model.Relationship

interface FamilyRepository {

    fun getById(familyId: String): Family?
    fun createFamilyCode(family: Family): String
    fun getByFamilyCode(familyCode: String): Family?
    fun addMember(family: Family, member: FamilyMember)
    fun createFamily(owner: FamilyMember, familyName: String): Family
    fun getMembersByRelationship(
        family: Family,
        relationship: Relationship,
        gender: Gender,
        name: String?
    ): List<FamilyMember>
}
