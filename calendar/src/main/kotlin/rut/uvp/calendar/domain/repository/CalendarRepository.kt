package rut.uvp.calendar.domain.repository

import kotlinx.datetime.Instant
import rut.uvp.calendar.domain.model.CalendarEvent

interface CalendarRepository {

    fun addEvent(userId: String, event: CalendarEvent)
    fun getEvents(userId: String, counts: Int): List<CalendarEvent>
    fun getEventsByRange(userId: String, dateRange: ClosedRange<Instant>): List<CalendarEvent>
}