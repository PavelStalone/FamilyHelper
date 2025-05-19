package rut.uvp.app.controller

import org.springframework.ai.chat.client.ChatClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import rut.uvp.core.common.log.Log
import rut.uvp.search.model.FamilyLeisureRequest
import rut.uvp.search.service.*
import rut.uvp.search.tool.FamilyTools

@RestController
@RequestMapping("chat")
class ChatController(
    private val tools: FamilyTools,
    private val chatClient: ChatClient,

    private val activitySearchService: ActivitySearchService,

    private val dateSelectionService: DateSelectionService,
    private val conversationFlowService: ConversationFlowService,
) {

    @PostMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun sendMessage(@RequestBody messageRequest: MessageRequest): Flux<String> {
        Log.v("Incoming message: $messageRequest")

        // 1) Извлекаем структуру запроса пользователя
        val draftReq: FamilyLeisureRequest =
            conversationFlowService.parseUserMessage(messageRequest.message)

        // 2) Автоподбор даты, если нужно
        val finalReq =
            if (draftReq.date.isNullOrBlank() || draftReq.date == "auto") {
                val (date, _) = dateSelectionService.selectDate(
                    draftReq.members?.map { it.role } ?: emptyList()
                )
                draftReq.copy(date = date)
            } else draftReq

        Log.v("Final leisure request: $finalReq")

        // 3) Получаем мероприятия (DeepSearch → KudaGo fallback)
        val events = activitySearchService.findActivities(finalReq)
        Log.i("Suggested events: $events")

        /* --------- два варианта ответа ---------
           3.a) Вернуть список JSON'ом --> return ResponseEntity.ok(events)
           3.b) Включить события в prompt LLM --> chatClient.prompt(...).tools(...).stream()
         */

        // --- пример 3.b --- добавляем события в prompt
        val prompt = buildPromptWithEvents(messageRequest.message, events)

        return chatClient
            .prompt(prompt)
            .tools(tools)
            .stream()
            .content()
    }

    private fun buildPromptWithEvents(userMsg: String, events: List<Any>): String =
        buildString {
            appendLine(userMsg)
            appendLine()
            appendLine("Ниже список актуальных мероприятий для пользователя:")
            events.forEachIndexed { i, e -> appendLine("${i + 1}. $e") }
            appendLine()
            appendLine("Ответь пользователю, используя эти данные.")
        }

    data class MessageRequest(val message: String)
}
