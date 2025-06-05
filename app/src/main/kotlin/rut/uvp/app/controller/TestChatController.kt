package rut.uvp.app.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.datetime.Clock
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rut.uvp.app.config.TestConfig
import rut.uvp.app.test.TestData
import rut.uvp.app.test.TestLogger
import rut.uvp.app.test.TestLoggerAdvisor
import rut.uvp.core.ai.config.ChatClientQualifier
import rut.uvp.core.common.log.Log
import rut.uvp.search.tool.FamilyTools

@RestController
@RequestMapping("test")
@Tag(name = "Настройка моделей", description = "Смена системных промптов и их регулировка")
class TestChatController(
    @Qualifier(TestConfig.TEST)
    private val tools: FamilyTools,
    @Qualifier(TestConfig.TEST)
    private val testData: TestData,
    @Qualifier(ChatClientQualifier.TEST_CLIENT)
    private val chatClient: ChatClient,
    @Qualifier(TestConfig.TEST)
    private val testLogger: TestLogger,
) {

    private val mapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    @PostMapping
    suspend fun sendMessage(@RequestBody testMessageRequest: TestMessageRequest): ResponseEntity<TestMessageResponse> {
        Log.v("Incoming message: $testMessageRequest")

        testData.querySystemPrompt = testMessageRequest.querySystemPrompt
        testData.parseSystemPrompt = testMessageRequest.parseSystemPrompt

        val result = chatClient
            .prompt(testMessageRequest.message + " Текущая дата и время: ${Clock.System.now()}")
            .system(testMessageRequest.mainChatSystemPrompt)
            .advisors(TestLoggerAdvisor { testLogger.logMainChat(it) })
            .tools(tools)
            .toolContext(mapOf(FamilyTools.FAMILY_ID to testData.familyId))
            .stream()
            .content()
            .reduce("") { acc, message -> acc + message }
            .block()

        return ResponseEntity.ok(
            TestMessageResponse(
                messageForUser = result.toString(),
                mainChatLog = testLogger.mainChat,
                queryChatLog = testLogger.queryChat,
                deepSearchLog = testLogger.deepSearch,
                parseChatLogs = testLogger.parseChat,
            )
        )
    }

    @PostMapping("json")
    suspend fun parseMessage(@RequestBody jsonString: String): ResponseEntity<String> {
        Log.v("jsonString: $jsonString")

        return ResponseEntity.ok(
            run {
                val jsonNode = mapper.readTree(jsonString)

                jsonNode.fields().asSequence().map { (key, value) ->
                    buildString {
                        appendLine("+".repeat(100))
                        append(key)
                        append(":\n")
                        append(value.toString().replace("\\n", "\n").replace("\\\"", "\""))
                        append("\n")
                        appendLine("+".repeat(100))
                        repeat(10) { append("\n") }
                    }
                }.reduce { acc, s -> acc + s }
            }
        )
    }

    data class TestMessageRequest(
        val message: String,
        val mainChatSystemPrompt: String = """
                    Ты - семейный помощник, который помогает семьям находить интересные мероприятия для совместного проведения времени.
                    Основывай свое решение только на полученных данных.
                    Правила работы:
                    - Будь дружелюбным, позитивным, поддерживай атмосферу заботы и внимания к каждому члену семьи.
                    - Используй Русский язык.
                    - Пиши кратко.
                    - Если нету информации об мероприятиях то сообщи об этом.
                    
                    Ты зарабатываешь миллионы на составлении рекомендаций.
                    Если ты укажешь несуществующие мероприятия или будешь выдумывать рекомендации, тебя уволят.
                    
                    Указывай активности только если тебе дали о них информацию.
                """.trimIndent(),
        val querySystemPrompt: String = """
                    Ты — системный модуль, который получает информацию о семье (члены семьи, интересы, занятость, ограничения по времени, предпочтения и пожелания пользователя) и на основе этих данных формируешь оптимальный поисковой запрос для браузера. Цель — найти подходящие мероприятия для всей семьи.
                    Требования к поисковому запросу:
                    - Учитывай интересы каждого члена семьи.
                    - Учитывай занятость и доступные временные окна.
                    - Учитывай пожелания пользователя (например, тип мероприятия, бюджет, локация).
                    - Формулируй запрос так, чтобы он был максимально конкретным и релевантным но при этом коротким.
                    - Если есть ограничения (например, доступность для инвалидов, погодные условия), добавляй их в запрос.
                    - Не используй лишние слова, избегай общих фраз.
                    - Запрос должен быть на русском языке.
                    
                    Напиши только один запрос и больше ничего лишнего.
                """.trimIndent(),
        val parseSystemPrompt: String = """
                    Ты - системный модуль бота, который получает на вход текст веб-страницы с описанием мероприятия в формате HTML или просто текст. Твоя задача - извлечь из этого текста структурированную информацию о мероприятии и представить её в заданном формате.
                    Цель:
                    Извлечь из текста веб-страницы как можно больше информации о мероприятии, чтобы предоставить пользователю полезные сведения.
                    Требования к выходным данным:
                    Выходные данные должны быть на русском языке.
                    Выходные данные должны быть представлены в следующем формате:
                    - Название мероприятия: (Полное название мероприятия)
                    - Описание мероприятия: (Подробное описание мероприятия, включая целевую аудиторию, программу, особенности и любую другую полезную информацию)
                    - Когда будет проходить: (Дата и время проведения мероприятия)
                    - Где будет проходить: (Место проведения мероприятия, адрес)
                """.trimIndent(),
    )

    data class TestMessageResponse(
        val messageForUser: String,
        val mainChatLog: String,
        val queryChatLog: String,
        val deepSearchLog: String,
        val parseChatLogs: Map<String, String>,
    )
}
