package rut.uvp.deepsearch.domain.repository

interface SearchRepository {

    suspend fun getLinks(query: String, size: Int): List<String>
    suspend fun getPage(link: String): String
}
