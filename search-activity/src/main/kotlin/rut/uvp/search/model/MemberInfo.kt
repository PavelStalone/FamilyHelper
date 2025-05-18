package rut.uvp.search.model

import rut.uvp.calendar.domain.model.CalendarEvent
import rut.uvp.core.data.model.user.User
import rut.uvp.family.domain.model.FamilyMember
import rut.uvp.family.domain.model.Preference

class MemberInfo(
    val user: User,
    val familyMember: FamilyMember,
    val preference: List<Preference>,
    val calendarEvents: List<CalendarEvent>,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberInfo

        return user == other.user
    }

    override fun hashCode(): Int {
        return user.hashCode()
    }

    override fun toString(): String {
        return """
            Имя: ${familyMember.name}
            Предпочтения: [${preference.joinToString("; ") { pref -> pref.preferences }}]
            Занятость: [${calendarEvents.joinToString("; ") { event -> "Название: ${event.title}, Описание: ${event.description}, Дата начала: ${event.start}, Дата окончания: ${event.end}" }}]
        """.trimIndent()
    }
}
