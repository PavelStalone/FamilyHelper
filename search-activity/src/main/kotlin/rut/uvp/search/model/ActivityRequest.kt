package rut.uvp.search.model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.datetime.Instant

data class ActivityRequest(
    @JsonProperty(required = false, value = "участники") val members: List<String>,
    @JsonProperty(required = false, value = "желаемая дата начала") val startDate: Instant?,
    @JsonProperty(required = false, value = "желаемая дата окончания") val endDate: Instant?,
    @JsonProperty(required = false, value = "предпочтения, интересы") val preferences: List<String>,
    @JsonProperty(required = false, value = "желаемый город") val city: String?,
)
