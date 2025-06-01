package rut.uvp.deepsearch.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import rut.uvp.core.common.log.Log

@Configuration
internal class SearchConfig {

    private val httpClient = HttpClient.create()
        .followRedirect(true)

    @Bean
    fun provideWebClient(): WebClient = WebClient.builder()
        .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) }
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36 OPR/118.0.0.0 (Edition Yx GX)")
//        .filter(logRequest())
//        .filter(logResponse())
        .build()

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
