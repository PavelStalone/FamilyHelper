package rut.uvp.search.tool

import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component
import rut.uvp.core.common.log.Log
import rut.uvp.search.model.FamilyMemberSearch

@Component
class FamilyTools {

    @Tool(description = "Найти подходящие мероприятия для семьи")
    fun findActivities(
        @ToolParam(
            required = false,
            description = """
                Остальные члены семьи с которыми планируется поход на мероприятие (самого пользователя указывать не нужно).
                Каждый член семьи содержит:
                - gender: Пол члена семьи (MALE/FEMALE)
                - levelRelation: Уровень родства. Аналогия поколения - Отец или Мама это -1 (Старшее поколение), Бабушка или дедушка это -2 (Еще более старшее поколение) и т.д. Брат, Сестра или Жена это 0 поколение. Сын или дочь это 1 поколение и т.д.
                - levelProximity: Степень близости. Родные это 0 степень близости. Двоюродные родственики это 1 степень, троюродные 2 и т.д.
                - name: Имя члена семьи (необязательно)
            """
        )
        members: List<FamilyMemberSearch>,
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
        @ToolParam(
            description = "Опиши здесь ситуацию для другого помощника. Распиши подробно что хочет пользователь (от 3-го лица)",
            required = true
        )
        context: String,
        toolContext: ToolContext,
    ): String {
        val familyId = toolContext.context[FAMILY_ID]

        Log.v(
            """
                members: $members,
                startDate: $startDate,
                endDate: $endDate,
                preferences: $preferences,
                city: $city,
                context: $context,
                familyId: $familyId,
            """.trimIndent()
        )

        return "Квест - 'найди отличия в парке'"
    }

    companion object {

        const val FAMILY_ID = "familyId"
    }
}