package rut.uvp.app.test

import org.springframework.ai.chat.client.advisor.api.*
import org.springframework.ai.chat.model.MessageAggregator
import reactor.core.publisher.Flux

class TestLoggerAdvisor(
    private val logAction: (message: String) -> Unit,
) : CallAroundAdvisor, StreamAroundAdvisor {

    override fun getOrder(): Int {
        return 10
    }

    override fun getName(): String {
        return this::class.java.simpleName
    }

    override fun aroundStream(advisedRequest: AdvisedRequest, chain: StreamAroundAdvisorChain): Flux<AdvisedResponse> {
        logAction(
            """
                |=== TestLoggerAdvisor ===
                |Request model before generate -------------------- 
                |${advisedRequest.toString().replace("\n", "|")}
                |--------------------------------------
            """.trimMargin()
        )

        val advisedResponse = chain.nextAroundStream(advisedRequest)
        return MessageAggregator().aggregateAdvisedResponse(advisedResponse) {
            logAction(
                """
                |=== TestLoggerAdvisor ===
                |Request model after generate -------------------- 
                |${it.toString().replace("\n", "|")}
                |--------------------------------------
            """.trimMargin()
            )
        }
    }

    override fun aroundCall(advisedRequest: AdvisedRequest, chain: CallAroundAdvisorChain): AdvisedResponse {
        logAction(
            """
                |=== TestLoggerAdvisor ===
                |Request model before generate -------------------- 
                |${advisedRequest.toString().replace("\n", "|")}
                |--------------------------------------
            """.trimMargin()
        )

        val advisedResponse = chain.nextAroundCall(advisedRequest)

        logAction(
            """
                |=== TestLoggerAdvisor ===
                |Request model after generate -------------------- 
                |${advisedResponse.toString().replace("\n", "|")}
                |--------------------------------------
            """.trimMargin()
        )
        return advisedResponse
    }
}
