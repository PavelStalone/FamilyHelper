package rut.uvp.app.controller

import kotlinx.datetime.Clock
import org.springframework.ai.chat.client.ChatClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import rut.uvp.core.common.log.Log
import rut.uvp.deepsearch.service.DeepSearchService
import rut.uvp.search.service.ConversationFlowService
import rut.uvp.search.service.DateSelectionService
import rut.uvp.search.tool.FamilyTools

@RestController
@RequestMapping("chat")
class ChatController(
    private val tools: FamilyTools,
    private val chatClient: ChatClient,

    private val deepSearchService: DeepSearchService,

    private val dateSelectionService: DateSelectionService,
    private val conversationFlowService: ConversationFlowService,
) {

    @PostMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun sendMessage(@RequestBody messageRequest: MessageRequest): Flux<String> {
        Log.v("Incoming message: $messageRequest")

        return chatClient
            .prompt(messageRequest.message + " Текущая дата и время: ${Clock.System.now()}")
            .tools(tools)
            .toolContext(mapOf(FamilyTools.FAMILY_ID to messageRequest.familyId))
            .stream()
            .content()
    }

    @PostMapping("/test")
    suspend fun getLinks(@RequestBody messageRequest: MessageRequest): ResponseEntity<Any> {
        Log.v("Incoming message: $messageRequest")

        return ResponseEntity.ok(deepSearchService.deepSearch(messageRequest.message))
    }

    data class MessageRequest(
        val message: String,
        val familyId: String,
    )
}
