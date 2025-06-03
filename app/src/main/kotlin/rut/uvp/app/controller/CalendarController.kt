package rut.uvp.app.controller

import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rut.uvp.calendar.domain.model.CalendarEvent
import rut.uvp.calendar.service.CalendarService
import java.time.Instant

@RestController
@RequestMapping("calendar")
class CalendarController(
    private val calendarService: CalendarService,
) {

    @GetMapping("/{userId}")
    fun getEventsByRange(
        @PathVariable userId: String,
        @RequestBody request: GetCalendarRequest
    ): ResponseEntity<List<CalendarEventResponse>> {
        val events = calendarService.getEventsByRange(
            userId = userId,
            dateRange = request.startAt.toKotlinInstant()..request.endAt.toKotlinInstant(),
        ).map { event ->
            CalendarEventResponse(
                id = event.id,
                title = event.title,
                description = event.description,
                startAt = event.start.toJavaInstant(),
                endAt = event.end.toJavaInstant(),
            )
        }

        return ResponseEntity.ok(events)
    }

    @PostMapping("/{userId}")
    fun saveEvent(@PathVariable userId: String, @RequestBody request: CalendarEventRequest) {
        calendarService.saveEvent(
            userId = userId, event = CalendarEvent(
                title = request.title,
                description = request.description,
                start = request.startAt.toKotlinInstant(),
                end = request.endAt.toKotlinInstant(),
            )
        )
    }

    @DeleteMapping("/{userId}")
    fun removeEvent(@PathVariable userId: String, @RequestParam eventId: String) {
        calendarService.removeEvent(userId = userId, eventId = eventId)
    }

    data class GetCalendarRequest(
        val startAt: Instant,
        val endAt: Instant,
    )

    data class CalendarEventRequest(
        val title: String,
        val description: String,
        val startAt: Instant,
        val endAt: Instant,
    )

    data class CalendarEventResponse(
        val id: String,
        val title: String,
        val description: String,
        val startAt: Instant,
        val endAt: Instant,
    )
}
