package rut.uvp.calendar.infrastructure

import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import rut.uvp.calendar.domain.model.CalendarEvent
import rut.uvp.calendar.domain.repository.CalendarRepository

@Component
internal class InMemoryCalendarRepositoryImpl : CalendarRepository {

    private val store: MutableMap<String, MutableSet<CalendarEvent>> = mutableMapOf()

    override fun addEvent(userId: String, event: CalendarEvent) {
        store.getOrPut(userId) { mutableSetOf() }.add(event)
    }

    override fun getEvents(userId: String, counts: Int): List<CalendarEvent> {
        return store[userId]?.toList() ?: emptyList()
    }

    override fun getEventsByRange(userId: String, dateRange: ClosedRange<Instant>): List<CalendarEvent> {
        return store[userId]?.filter { event -> event.inRange(dateRange) } ?: emptyList()
    }
}
