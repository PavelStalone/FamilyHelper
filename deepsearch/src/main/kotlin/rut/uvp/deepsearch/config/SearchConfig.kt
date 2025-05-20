package rut.uvp.deepsearch.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import rut.uvp.core.common.log.Log
import java.net.URLDecoder

@Configuration
internal class SearchConfig {

    @Bean
    fun provideWebClient(): WebClient = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
        .defaultHeader("User-Agent", "Mozilla/5.0 (compatible; DeepSearchBot/1.0)")
        .filter(decoder())
//        .filter(logRequest())
//        .filter(logResponse())
        .build()

    private fun decoder(): ExchangeFilterFunction = ExchangeFilterFunction.ofRequestProcessor { request ->
        val modifiedRequest = runCatching {
            val query = request.url().query
            requireNotNull(query)
            require(query.contains("uddg="))

            val encodedUddg = query.substringAfter("uddg=").substringBefore("&")
            val firstDecode = URLDecoder.decode(encodedUddg, "UTF-8")
            val realUrl = URLDecoder.decode(firstDecode, "UTF-8")

            ClientRequest.from(request).url(java.net.URI(realUrl)).build()
        }.getOrDefault(request)


        Mono.just(modifiedRequest)
    }

    private fun logRequest(): ExchangeFilterFunction = ExchangeFilterFunction.ofRequestProcessor { request ->
        Log.v("Request: ${request.method()} ${request.url()}")

        request.headers().forEach { name, values ->
            values.forEach { value -> println("$name: $value") }
        }
        Mono.just(request)
    }

    private fun logResponse(): ExchangeFilterFunction = ExchangeFilterFunction.ofResponseProcessor { response ->
        Log.v("Response status: ${response.statusCode()}")

        response.headers().asHttpHeaders().forEach { name, values ->
            values.forEach { value -> println("$name: $value") }
        }
        Mono.just(response)
    }
}
