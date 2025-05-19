package rut.uvp.deepsearch.client

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import rut.uvp.deepsearch.config.DeepSearchProperties
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class DuckDuckGoClient(
    private val restTemplate: RestTemplate,
    private val props: DeepSearchProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Retryable(maxAttempts = 3, backoff = Backoff(delay = 1000L, multiplier = 2.0))
    fun searchFirstLink(query: String): String? {
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8)
        val url = "${props.searchUrl}?q=$encodedQuery"
        log.debug("DuckDuckGo search URL: {}", url)
        val response = restTemplate.getForEntity(url, String::class.java)
        val document = Jsoup.parse(response.body ?: return null)
        val linkElement = document.selectFirst("a.result__a")
        val link = linkElement?.attr("href")
        log.info("First search result for '$query': $link")
        return link
    }
}
