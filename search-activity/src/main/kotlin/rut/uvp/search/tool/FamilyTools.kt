package rut.uvp.search.tool

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.springframework.ai.chat.model.ToolContext
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import rut.uvp.core.common.log.Log
import rut.uvp.search.model.FamilyMemberSearch
import rut.uvp.search.service.SearchActivityService

interface FamilyTools {

    fun findActivities(
        members: List<FamilyMemberSearch>,
        startDate: String?,
        endDate: String?,
        preferences: List<String>,
        city: String?,
        context: String,
        toolContext: ToolContext,
    ): String

    companion object {

        const val FAMILY_ID = "familyId"
    }
}


@Primary
@Component
internal class FamilyToolsImpl(
    private val searchActivityService: SearchActivityService
) : FamilyTools {

    private val mapper = jacksonObjectMapper()

    @Tool(description = "Найти подходящие мероприятия для семьи", returnDirect = false)
    override fun findActivities(
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
            description = "Предпочтения в запросе (хотелось бы активность в помещении, мы не любим жару, нам интересна робототехника и т.д.)"
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
        val familyId = toolContext.context[FamilyTools.FAMILY_ID] as String

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

        val activities = runBlocking {
            searchActivityService.findActivity(
                members = (members as List<LinkedHashMap<String, Any>>).map {
                    mapper.convertValue(it, FamilyMemberSearch::class.java)
                },
                startDate = startDate,
                endDate = endDate,
                preferences = preferences,
                city = city,
                context = context,
                familyId = familyId,
            )
        }

        return runBlocking {
            """
            |Найденные активности: $activities
            |Используй все эти данные для составления рекомендаций. Также указывай ссылку на предложенное мероприятие (Она находится в поле url)
            |Your response should be in JSON format.
            |Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
            |Do not include markdown code blocks in your response.
            |Here is the JSON Schema instance your output must adhere to:
            |```{
            |  "type" : "object",
            |  "properties" : {
            |    "activities" : {
            |      "type" : "array",
            |      "items" : {
            |        "type" : "object",
            |        "properties" : {
            |          "dateRange" : {
            |            "type" : "string"
            |          },
            |          "description" : {
            |            "type" : "string"
            |          },
            |          "location" : {
            |            "type" : "string"
            |          },
            |          "title" : {
            |            "type" : "string"
            |          },
            |          "url" : {
            |            "type" : "string"
            |          }
            |        },
            |        "additionalProperties" : false
            |      }
            |    },
            |    "message" : {
            |      "type" : "string"
            |    }
            |  },
            |  "additionalProperties" : false,
            |  "required": ["activities", "message"]
            |}```
            |В ответе укажи только JSON структуру без ``` и подписей
        """.trimMargin()
        }
    }
}