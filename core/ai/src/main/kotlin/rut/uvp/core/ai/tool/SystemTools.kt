package rut.uvp.core.ai.tool

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component
import rut.uvp.core.common.log.Log

@Component
internal class SystemTools {

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    @Tool(description = "Получить текущую системную дату и время")
    fun getActualTime(): String {
        val date = dateTimeFormat.format(Clock.System.now().toLocalDateTime(TimeZone.UTC))

        Log.v("getActualTime called: $date")
        return "Текущая дата и время: $date"
    }
}
