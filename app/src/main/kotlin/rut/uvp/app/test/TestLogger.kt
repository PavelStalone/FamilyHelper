package rut.uvp.app.test

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import rut.uvp.app.config.TestConfig

@Component
@Qualifier(TestConfig.TEST)
class TestLogger {

    private var _mainChat: String = ""
    private var _queryChat: String = ""
    private val _parseChat: MutableMap<String, String> = mutableMapOf()
    private var _deepSearch: String = ""

    fun logMainChat(message: String) {
        _mainChat = buildString {
            append(_mainChat)
            repeat(3) { append("\n") }
            append(message)
        }
    }

    fun logQueryChat(message: String) {
        _queryChat = buildString {
            append(_queryChat)
            repeat(3) { append("\n") }
            append(message)
        }
    }

    fun logDeepSearch(message: String) {
        _deepSearch = buildString {
            append(_deepSearch)
            repeat(3) { append("\n") }
            append(message)
        }
    }

    fun logParseChat(url: String, message: String) {
        _parseChat[url] = buildString {
            append(_parseChat.getOrPut(url) { "" })
            repeat(3) { append("\n") }
            append(message)
        }
    }

    val mainChat: String
        get() = _mainChat.let { log ->
            _mainChat = ""
            log
        }

    val queryChat: String
        get() = _queryChat.let { log ->
            _queryChat = ""
            log
        }

    val deepSearch: String
        get() = _deepSearch.let { log ->
            _deepSearch = ""
            log
        }

    val parseChat: Map<String, String>
        get() = _parseChat.let { log ->
            val copy = log.toMap()
            _parseChat.clear()
            copy
        }
}
