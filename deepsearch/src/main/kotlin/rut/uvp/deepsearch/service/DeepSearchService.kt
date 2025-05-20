package rut.uvp.deepsearch.service

import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import rut.uvp.core.common.log.Log
import rut.uvp.deepsearch.domain.repository.SearchRepository
import kotlin.time.Duration.Companion.seconds

interface DeepSearchService {

    suspend fun deepSearch(query: String): List<String>
}

@Service
internal class DeepSearchServiceImpl(
    private val searchRepository: SearchRepository,
) : DeepSearchService {

    override suspend fun deepSearch(query: String): List<String> = withContext(Dispatchers.IO) {
        Log.i("DeepSearch started for query: $query")

        val links = searchRepository.getLinks(query = query, size = 5)
        Log.i("DeepSearch links: $links")

        coroutineScope {
            links.map { link ->
                async(start = CoroutineStart.LAZY) { // TODO: Add batching strategy - shoplikpavel
                    runCatching {
                        Log.i("Start - $link")
                        withTimeout(5.seconds) {
                            Jsoup.parse(searchRepository.getPage(link)).body().text()
                        }
                    }.onFailure { throwable ->
                        Log.e(throwable, "Failure link: $link")
                    }.getOrNull()
                }
            }.awaitAll()
        }.filterNotNull()
    }
}
