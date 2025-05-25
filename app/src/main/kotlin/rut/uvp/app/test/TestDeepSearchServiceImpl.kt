package rut.uvp.app.test

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import rut.uvp.app.config.TestConfig
import rut.uvp.core.ai.config.ChatClientQualifier
import rut.uvp.core.common.log.Log
import rut.uvp.deepsearch.domain.model.Activity
import rut.uvp.deepsearch.domain.repository.SearchRepository
import rut.uvp.deepsearch.service.DeepSearchService
import java.net.URLDecoder
import kotlin.time.Duration.Companion.seconds

@Service
@Qualifier(TestConfig.TEST)
internal class TestDeepSearchServiceImpl(
    @Qualifier(TestConfig.TEST)
    private val testData: TestData,
    @Qualifier(ChatClientQualifier.TEST_CLIENT)
    private val chatClient: ChatClient,
    @Qualifier(TestConfig.TEST)
    private val testLogger: TestLogger,
    private val searchRepository: SearchRepository,
) : DeepSearchService {

    override suspend fun deepSearch(query: String): List<Activity> = withContext(Dispatchers.IO) {
        val clearQuery = query.trim().trimEnd('.').removeSurrounding("\"")
        testLogger.logDeepSearch(
            """
                === DeepSearch started ===
                query: $clearQuery
            """.trimIndent()
        )
        Log.i("DeepSearch started for query: $clearQuery")

        val links = searchRepository.getLinks(query = clearQuery, size = 3).map { it.decode() }
        testLogger.logDeepSearch(
            """
                |=== DeepSearch find links ===
                |${links.joinToString("|")}
            """.trimMargin()
        )
        Log.i("DeepSearch links: $links")

        coroutineScope {
            links
                .map { link ->
                    async(start = CoroutineStart.LAZY) { // TODO: Add batching strategy - shoplikpavel
                        runCatching {
                            Log.d("Start parsing: $link")

                            Pair(
                                first = link,
                                second = withTimeout(5.seconds) {
                                    Jsoup.parse(searchRepository.getPage(link)).body().text()
                                }
                            )
                        }.onFailure { throwable ->
                            testLogger.logDeepSearch(
                                """
                                    === DeepSearch link failure ===
                                    $link
                                    ${throwable.message}
                                """.trimIndent()
                            )
                            Log.e(throwable, "Failure link: $link")
                        }.getOrNull()
                    }
                }
                .awaitAll()
                .filterNotNull()
                .map { (link, page) ->
                    async(start = CoroutineStart.LAZY) {
                        runCatching {
                            testLogger.logDeepSearch(
                                """
                                    === DeepSearch link started parse ===
                                    $link
                                """.trimIndent()
                            )
                            Log.i("Start finds activity: $link")

                            chatClient
                                .prompt(page)
                                .system(testData.parseSystemPrompt)
                                .advisors(TestLoggerAdvisor { testLogger.logParseChat(url = link, message = it) })
                                .call()
                                .entity(object : ParameterizedTypeReference<List<ActivityResponse>>() {})
                                ?.map { response ->
                                    Activity(
                                        title = response.title,
                                        description = response.description,
                                        dateRange = response.dateRange,
                                        location = response.location,
                                        url = link
                                    )
                                }
                        }.getOrNull()
                    }
                }
                .awaitAll()
                .filterNotNull()
                .flatten()
        }.also {
            testLogger.logDeepSearch(
                """
                    |=== DeepSearch finds Activity ===
                    |${it.joinToString("|") { it.toString() }}
                """.trimMargin()
            )
        }
    }

    private fun String.decode(): String = runCatching {
        val query = this
        require(query.contains("uddg="))

        val encodedUddg = query.substringAfter("uddg=").substringBefore("&")
        val firstDecode = URLDecoder.decode(encodedUddg, "UTF-8")
        val realUrl = URLDecoder.decode(firstDecode, "UTF-8")

        realUrl
    }.onFailure { throwable ->
        Log.e(throwable, "error decoding")
    }
        .getOrDefault(this)
}

@JsonPropertyOrder("title", "description", "dateRange", "location")
internal data class ActivityResponse(
    val title: String,
    val description: String,
    val dateRange: String,
    val location: String,
)
