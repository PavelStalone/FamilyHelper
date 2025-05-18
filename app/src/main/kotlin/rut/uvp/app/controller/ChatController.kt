package rut.uvp.app.controller

import org.springframework.ai.chat.client.ChatClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import rut.uvp.core.common.log.Log
import rut.uvp.search.service.ConversationFlowService
import rut.uvp.search.service.DateSelectionService
import rut.uvp.search.service.KudaGoService
import rut.uvp.search.service.SearchQueryService
import rut.uvp.search.tool.FamilyTools

@RestController
@RequestMapping("chat")
class ChatController(
    private val tools: FamilyTools,
    private val chatClient: ChatClient,
    private val kudaGoService: KudaGoService,
    private val searchQueryService: SearchQueryService,
    private val dateSelectionService: DateSelectionService,
    private val conversationFlowService: ConversationFlowService,
) {

    @PostMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun sendMessage(@RequestBody messageRequest: MessageRequest): Flux<String> {
        Log.v("message: $messageRequest")

        val response = chatClient.prompt(messageRequest.message)
            .tools(tools)
            .toolContext(mapOf(FamilyTools.FAMILY_ID to messageRequest.familyId))
            .stream()
            .content()

//        val leisureRequest = conversationFlowService.parseUserMessage(messageRequest.message)
//        val finalRequest = if (leisureRequest.date == null || leisureRequest.date == "auto") {
//            val (date, timeRange) = dateSelectionService.selectDate(leisureRequest.members?.map { it.role }
//                ?: emptyList())
//            leisureRequest.copy(date = date)
//        } else leisureRequest
//        Log.v("finalRequest: $finalRequest")
//
//        val query = searchQueryService.buildKudaGoQuery(finalRequest)
//        query.plus("location" to query["city"])
//        query.filter { it.key != "city" }
//
//        val events = kudaGoService.searchEvents(query)
//
//        Log.i("events: $events")
//
//        return ResponseEntity.ok(events)
        return response
    }

    data class MessageRequest(
        val message: String,
        val familyId: String,
    )
}
