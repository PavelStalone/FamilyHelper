package rut.uvp.core.ai.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.OllamaEmbeddingModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.util.ReflectionUtils
import rut.uvp.core.ai.tool.SystemTools

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OllamaConfig.OllamaClientProperties::class)
internal class OllamaConfig {

    @ConfigurationProperties(prefix = "spring.ai.ollama")
    data class OllamaClientProperties(
        val baseUrl: String,
        val model: String
    )

    @Bean
    fun provideEmbeddingModel(properties: OllamaClientProperties): EmbeddingModel {
        return OllamaEmbeddingModel.builder()
            .defaultOptions(
                OllamaOptions.builder()
                    .model(properties.model)
                    .seed(23)
                    .temperature(0.0)
                    .repeatPenalty(1.0)
                    .presencePenalty(0.0)
                    .frequencyPenalty(0.0)
                    .build()
            )
            .ollamaApi(OllamaApi(properties.baseUrl))
            .build()
    }

    @Bean
    fun chatModel(properties: OllamaClientProperties): ChatModel {
        return OllamaChatModel.builder()
            .ollamaApi(OllamaApi(properties.baseUrl))
            .defaultOptions(
                OllamaOptions.builder()
                    .model(properties.model)
                    .temperature(0.4)
                    .build()
            )
            .build()
    }

    @Bean
    @Qualifier("GenerationClient")
    fun generateChatClient(
        chatModel: ChatModel,
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultOptions(
                OllamaOptions
                    .builder()
                    .temperature(1.0)
                    .build()
            )
            .build()
    }

    @Bean
    fun chatClient(
        chatModel: ChatModel,
        vectorStore: VectorStore,
        systemTools: SystemTools,
    ): ChatClient {
        val chatMemory = InMemoryChatMemory()

        chatMemory.clear("ChatMemory")

        return ChatClient.builder(chatModel)
            .defaultOptions(
                OllamaOptions
                    .builder()
                    .temperature(0.2)
                    .build()
            )
            .defaultSystem(
                """
                    Ты - семейный помощник, который помогает семьям находить интересные мероприятия для совместного проведения времени.
                    
                    Твоя задача - подобрать мероприятия, которые будут интересны всем членам семьи. Основывай свое решение только на полученных данных. Нельзя выдумывать мероприятия.
                    
                    Если пользователь явно не указал дату, то постарайся просчитать дату основываясь на системном времени.
                    
                    Не используй вложенные функции!
                    
                    Правила работы:
                        Спрашивай о желаемой дате и времени.
                        Будь дружелюбным, позитивным, поддерживай атмосферу заботы и внимания к каждому члену семьи.
                """.trimIndent()
            )
            .defaultAdvisors(
                MessageChatMemoryAdvisor(chatMemory, "ChatMemory", 5),
                LoggerAdvisor(),
            )
            .defaultTools(systemTools)
            .build()
    }
}
