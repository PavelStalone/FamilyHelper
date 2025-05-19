package rut.uvp.deepsearch.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.retry.annotation.EnableRetry
import org.springframework.web.client.RestTemplate

@Configuration
@EnableRetry
@EnableConfigurationProperties(DeepSearchProperties::class)
class DeepSearchConfig {

    @Bean
    fun restTemplate(props: DeepSearchProperties): RestTemplate {
        val factory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(props.connectTimeout.toMillis().toInt())
            setReadTimeout(props.readTimeout.toMillis().toInt())
        }
        return RestTemplate(factory)
    }

    @Bean
    fun caffeineCacheManager(props: DeepSearchProperties): CacheManager {
        val caffeine = Caffeine.newBuilder()
            .expireAfterWrite(props.cacheTtl)
            .maximumSize(props.cacheMaxSize)
        return CaffeineCacheManager().apply { setCaffeine(caffeine) }
    }

    @Bean
    fun cacheManagerCustomizer(): CacheManagerCustomizer<CacheManager> = CacheManagerCustomizer { }
}
