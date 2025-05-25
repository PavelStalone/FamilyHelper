package rut.uvp.family.domain.model

data class FamilyMember(
    val id: String,
    val name: String,
    val userId: String,
    val gender: Gender,
    val relationship: Relationship,
    val preferences: List<Preference>,
)
