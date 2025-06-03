package rut.uvp.calendar.service

import kotlinx.datetime.Instant
import org.springframework.stereotype.Service
import rut.uvp.calendar.domain.model.CalendarEvent
import rut.uvp.calendar.domain.repository.CalendarRepository

interface CalendarService {

    fun removeEvent(userId: String, eventId: String)
    fun saveEvent(userId: String, event: CalendarEvent)
    fun getEvents(userId: String, counts: Int): List<CalendarEvent>
    fun getEventsByRange(userId: String, dateRange: ClosedRange<Instant>): List<CalendarEvent>
}

@Service
internal class CalendarServiceImpl(
    private val calendarRepository: CalendarRepository,
) : CalendarService {

    override fun removeEvent(userId: String, eventId: String) {
        calendarRepository.removeEvent(userId, eventId)
    }

    override fun saveEvent(userId: String, event: CalendarEvent) {
        calendarRepository.addEvent(userId, event)
    }

    override fun getEvents(userId: String, counts: Int): List<CalendarEvent> {
        return calendarRepository.getEvents(userId, counts)
    }

    override fun getEventsByRange(userId: String, dateRange: ClosedRange<Instant>): List<CalendarEvent> {
        return calendarRepository.getEventsByRange(userId, dateRange)
    }
}
