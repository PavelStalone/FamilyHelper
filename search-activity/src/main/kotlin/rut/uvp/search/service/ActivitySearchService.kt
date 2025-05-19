package rut.uvp.search.service

import org.springframework.stereotype.Service
import rut.uvp.core.common.log.Log
import rut.uvp.search.model.FamilyLeisureRequest
import rut.uvp.search.model.FamilyLeisureResponse
import rut.uvp.deepsearch.service.DeepSearchService

@Service
class ActivitySearchService(
    private val deepSearchService: DeepSearchService,
    private val kudaGoService: KudaGoService,
    private val searchQueryService: SearchQueryService
) {

    /**
     * Возвращает список мероприятий для семьи
     * 1. Пытаемся найти через DeepSearch
     * 2. Если DeepSearch не дал валидный результат — падаем на KudaGo
     */
    fun findActivities(request: FamilyLeisureRequest): List<FamilyLeisureResponse> {

        val query = buildSearchPhrase(request)
        Log.i("DeepSearch query: $query")

        val deepText = deepSearchService.deepSearch(query)

        if (!deepText.isNullOrBlank() && deepText.length > 100) {
            return listOf(
                FamilyLeisureResponse(
                    title = "Найдено по веб-поиску",
                    description = deepText,
                    imageUrl = null,
                    date = null,
                    time = null,
                    location = null,
                    address = null,
                    lat = null,
                    lon = null
                )
            )
        }

        Log.v("DeepSearch empty, falling back to KudaGo")
        val params = searchQueryService.buildKudaGoQuery(request)
        return kudaGoService.searchEvents(params)
    }

    /** Простейший генератор поисковой фразы; */
    private fun buildSearchPhrase(r: FamilyLeisureRequest): String = buildString {
        append("семейные мероприятия ")
        if (!r.city.isNullOrBlank()) append("в ${r.city} ")
        r.date?.let { append("на дату $it ") }
        val prefs = (r.preferences ?: emptyList()) + (r.members?.flatMap { it.interests ?: emptyList() } ?: emptyList())
        if (prefs.isNotEmpty()) append("по интересам ${prefs.joinToString()} ")
    }.trim()
}
