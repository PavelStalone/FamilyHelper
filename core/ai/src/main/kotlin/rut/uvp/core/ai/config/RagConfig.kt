package rut.uvp.core.ai.config

import org.springframework.ai.chat.client.advisor.api.*
import org.springframework.ai.chat.model.MessageAggregator
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import rut.uvp.core.common.log.Log

@Configuration
class RagConfig {

}

class LoggerAdvisor() : CallAroundAdvisor, StreamAroundAdvisor {

    override fun getOrder(): Int {
        return 10
    }

    override fun getName(): String {
        return this::class.java.simpleName
    }

    override fun aroundStream(advisedRequest: AdvisedRequest, chain: StreamAroundAdvisorChain): Flux<AdvisedResponse> {
        println("BEFORE Stream: $advisedRequest")

        Log.v("userParams: ${advisedRequest.userParams}")

        val advisedResponse = chain.nextAroundStream(advisedRequest)

        return MessageAggregator().aggregateAdvisedResponse(advisedResponse) { println("AFTER Stream: $it") }
    }

    override fun aroundCall(advisedRequest: AdvisedRequest, chain: CallAroundAdvisorChain): AdvisedResponse {
        println("BEFORE Call: $advisedRequest")

        val advisedResponse = chain.nextAroundCall(advisedRequest)

        println("AFTER Call: $advisedResponse")

        return advisedResponse
    }
}
