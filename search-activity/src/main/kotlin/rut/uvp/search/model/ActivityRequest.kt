package rut.uvp.search.model

import kotlinx.datetime.Instant

data class ActivityRequest(
    val members: List<String>,
    val startDate: Instant?,
    val endDate: Instant?,
    val preferences: List<String>,
    val city: String?,
)
