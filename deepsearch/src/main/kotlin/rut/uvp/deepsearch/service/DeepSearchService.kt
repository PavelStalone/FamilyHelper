package rut.uvp.deepsearch.service

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import rut.uvp.deepsearch.client.DuckDuckGoClient
import rut.uvp.deepsearch.extractor.WebContentExtractor

@Service
class DeepSearchService(
    private val duckDuckGoClient: DuckDuckGoClient,
    private val webContentExtractor: WebContentExtractor
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Cacheable("deepSearchResults")
    fun deepSearch(query: String): String? {
        log.info("DeepSearch started for query: '{}'", query)
        val url = duckDuckGoClient.searchFirstLink(query)
        if (url.isNullOrBlank()) {
            log.warn("No result URL found for query: '$query'")
            return null
        }
        return webContentExtractor.extractPlainText(url)
    }
}
