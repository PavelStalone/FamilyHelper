package rut.uvp.search.tool

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component
import rut.uvp.core.common.log.Log

@Component
class FamilyTools {

    @Tool(description = "Поиск подходящих мероприятий")
    fun findActivities(
        @ToolParam(
            required = false,
            description = "Остальные члены семьи с которыми планируется поход на мероприятие (самого пользователя указывать не нужно). Например (брат, папа, жена и т.д.)"
        )
        members: List<String>,
        @ToolParam(required = false, description = "Желаемая дата и время начала мероприятия в формате ISO")
        startDate: String?,
        @ToolParam(required = false, description = "Желаемая дата и время окончания мероприятия в формате ISO")
        endDate: String?,
        @ToolParam(
            required = false,
            description = "Дополнительные предпочтения или интересы. Например (спорт, кино и т.д.)"
        )
        preferences: List<String>,
        @ToolParam(required = false, description = "Предпочитаемый город")
        city: String?,
    ): String {
        Log.v(
            """
                members: $members,
                startDate: $startDate,
                endDate: $endDate,
                preferences: $preferences,
                city: $city
            """.trimIndent()
        )

        return "Квест - 'найди отличия в парке'"
    }
}