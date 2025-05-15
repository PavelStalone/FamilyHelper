package rut.uvp.calendar.domain.model

import kotlinx.datetime.Instant

data class CalendarEvent(
    val id: String,
    val end: Instant,
    val title: String,
    val start: Instant,
    val description: String,
) {

    init {
        require(start <= end) { "The start date cannot be greater than the end date" }
    }

    fun inRange(dateRange: ClosedRange<Instant>): Boolean {
        return dateRange.contains(start) || dateRange.contains(end) || (start < dateRange.start && end > dateRange.endInclusive)
    }
}
