package rut.uvp.deepsearch.service

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import rut.uvp.core.common.log.Log
import rut.uvp.deepsearch.domain.repository.SearchRepository

interface DeepSearchService {

    suspend fun deepSearch(query: String): List<String>
}

@Service
internal class DeepSearchServiceImpl(
    private val searchRepository: SearchRepository,
) : DeepSearchService {

    override suspend fun deepSearch(query: String): List<String> {
        Log.i("DeepSearch started for query: $query")
        val links = searchRepository.getLinks(query = query, size = 5)
        Log.i("DeepSearch links: $links")

        val result = links.map { link ->
            Log.v("Start")
            Jsoup.parse(searchRepository.getPage(link))
                .body()
                .text()
        }

        Log.i("Result parsing: $result")

        return result
    }
}
