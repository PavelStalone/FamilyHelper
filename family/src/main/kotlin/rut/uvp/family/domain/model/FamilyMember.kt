package rut.uvp.family.domain.model

data class FamilyMember(
    val id: String,
    val name: String,
    val gender: Gender,
    val family: Set<Family>,
    val relationship: Relationship,
    val preferences: List<Preference>,
)
