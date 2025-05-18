package rut.uvp.search.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FamilyLeisureRequest(
    val members: List<MemberInfo>?,
    val date: String?, // ISO или "auto"
    val preferences: List<String>?,
    val restrictions: List<String>?,
    val city: String?,
    val userId: Long? = null // инициатор запроса
)