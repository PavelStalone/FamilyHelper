package rut.uvp.app.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
import rut.uvp.core.common.log.Log
import rut.uvp.deepsearch.domain.model.Activity
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

    @Qualifier(TestConfig.TEST)
    private val testData: TestData,

    private val dateSelectionService: DateSelectionService,
    private val conversationFlowService: ConversationFlowService,
) {

    private val mapper = jacksonObjectMapper()

    @PostMapping
    suspend fun sendMessage(@RequestBody messageRequest: MessageRequest): ResponseEntity<MessageResponse?> {
        Log.v("Incoming message: $messageRequest")

        val result = run {
            val content = chatClient
                .prompt(messageRequest.message + " [Текущая дата и время: ${Clock.System.now()}]")
                .tools(tools)
                .toolContext(mapOf(FamilyTools.FAMILY_ID to messageRequest.familyId))
                .call()
                .content()

            Log.v("Content: $content")
            requireNotNull(content)

            runCatching {
                mapper.readValue(content.removeSurrounding("```").removePrefix("json"), MessageResponse::class.java)
            }.onFailure { throwable ->
                Log.e(throwable, "Error when mapping: $content")
            }.getOrDefault(MessageResponse(message = content, activities = emptyList()))
        }

        return ResponseEntity.ok(result)
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

    data class MessageResponse(
        val message: String,
        val activities: List<Activity>
    )
}
