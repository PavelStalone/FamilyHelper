package rut.uvp.deepsearch.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "deepsearch")
data class DeepSearchProperties(
    var searchUrl: String = "https://duckduckgo.com/html",
    var connectTimeout: Duration = Duration.ofSeconds(5),
    var readTimeout: Duration = Duration.ofSeconds(5),
    var cacheTtl: Duration = Duration.ofHours(1),
    var cacheMaxSize: Long = 1000
)
