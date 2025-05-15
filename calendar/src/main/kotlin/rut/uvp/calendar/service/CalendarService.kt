package rut.uvp.calendar.service

import org.springframework.stereotype.Service

interface CalendarService {

    fun saveEvent()
    fun getEventsForUser()
}

@Service
internal class CalendarServiceImpl {
}
