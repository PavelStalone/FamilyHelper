package rut.uvp.deepsearch.extractor

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component

@Component
class WebContentExtractor {
    private val log = LoggerFactory.getLogger(javaClass)

    @Retryable(maxAttempts = 3, backoff = Backoff(delay = 1000L, multiplier = 2.0))
    fun extractPlainText(url: String): String? {
        log.debug("Extracting content from URL: {}", url)
        return try {
            Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; DeepSearchBot/1.0)")
                .timeout(5000)
                .get()
                .body()
                .text()
                .also { log.info("Successfully extracted text from $url (${it.length} chars)") }
        } catch (ex: Exception) {
            log.error("Error while extracting content from {}", url, ex)
            null
        }
    }
}
