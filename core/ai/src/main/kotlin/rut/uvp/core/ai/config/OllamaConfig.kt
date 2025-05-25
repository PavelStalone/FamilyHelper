package rut.uvp.core.ai.config

import org.springframework.ai.chat.client.ChatClient
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
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.client.WebClient
import rut.uvp.core.ai.tool.SystemTools
import java.time.Duration

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
            .ollamaApi(OllamaApi(properties.baseUrl))
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
            .build()
    }

    @Bean
    fun chatModel(
        properties: OllamaClientProperties,
    ): ChatModel {
        val factory = SimpleClientHttpRequestFactory().apply {
            setReadTimeout(Duration.ofSeconds(600))
            setConnectTimeout(Duration.ofSeconds(600))
        }

        return OllamaChatModel.builder()
            .ollamaApi(OllamaApi(properties.baseUrl, RestClient.builder().requestFactory(factory), WebClient.builder()))
            .defaultOptions(
                OllamaOptions.builder()
                    .model(properties.model)
                    .temperature(0.4)
                    .build()
            )
            .build()
    }

    @Bean
    @Qualifier(ChatClientQualifier.ACTIVITY_FINDER_CLIENT)
    fun chatFinderModel(
        properties: OllamaClientProperties,
    ): ChatModel {
        val factory = SimpleClientHttpRequestFactory().apply {
            setReadTimeout(Duration.ofSeconds(60))
            setConnectTimeout(Duration.ofSeconds(60))
        }

        return OllamaChatModel.builder()
            .ollamaApi(OllamaApi(properties.baseUrl, RestClient.builder().requestFactory(factory), WebClient.builder()))
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
    @Qualifier(ChatClientQualifier.ACTIVITY_FINDER_CLIENT)
    fun activityFinderChatClient(
        @Qualifier(ChatClientQualifier.ACTIVITY_FINDER_CLIENT)
        chatModel: ChatModel,
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultOptions(
                OllamaOptions
                    .builder()
                    .temperature(0.0)
                    .build()
            )
            .defaultSystem(
                """
                    Ты - системный модуль бота, который получает на вход текст веб-страницы с описанием мероприятия в формате HTML или просто текст. Твоя задача - извлечь из этого текста структурированную информацию о мероприятии и представить её в заданном формате.
                    Цель:
                    Извлечь из текста веб-страницы как можно больше информации о мероприятии, чтобы предоставить пользователю полезные сведения.
                    Требования к выходным данным:
                    Выходные данные должны быть на русском языке.
                    Выходные данные должны быть представлены в следующем формате:
                    - Название мероприятия: (Полное название мероприятия)
                    - Описание мероприятия: (Подробное описание мероприятия, включая целевую аудиторию, программу, особенности и любую другую полезную информацию)
                    - Когда будет проходить: (Дата и время проведения мероприятия)
                    - Где будет проходить: (Место проведения мероприятия, адрес)
                """.trimIndent()
            )
            .build()
    }

    @Bean
    @Qualifier(ChatClientQualifier.QUERY_GENERATOR_CLIENT)
    fun queryGeneratorChatClient(
        chatModel: ChatModel,
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultOptions(
                OllamaOptions
                    .builder()
                    .temperature(0.0)
                    .build()
            )
            .defaultSystem(
                """
                    Ты — системный модуль, который получает информацию о семье (члены семьи, интересы, занятость, ограничения по времени, предпочтения и пожелания пользователя) и на основе этих данных формируешь оптимальный поисковой запрос для браузера. Цель — найти подходящие мероприятия для всей семьи.
                    Требования к поисковому запросу:
                    - Учитывай интересы каждого члена семьи.
                    - Учитывай занятость и доступные временные окна.
                    - Учитывай пожелания пользователя (например, тип мероприятия, бюджет, локация).
                    - Формулируй запрос так, чтобы он был максимально конкретным и релевантным но при этом коротким.
                    - Если есть ограничения (например, доступность для инвалидов, погодные условия), добавляй их в запрос.
                    - Не используй лишние слова, избегай общих фраз.
                    - Запрос должен быть на русском языке.
                    
                    Напиши только один запрос и больше ничего лишнего.
                """.trimIndent()
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
                    .temperature(0.0)
                    .build()
            )
            .defaultSystem(
                """
                    Ты - семейный помощник, который помогает семьям находить интересные мероприятия для совместного проведения времени.
                    Твоя задача - подобрать мероприятия, которые будут интересны всем членам семьи. 
                    Основывай свое решение только на полученных данных.
                    Правила работы:
                    - Будь дружелюбным, позитивным, поддерживай атмосферу заботы и внимания к каждому члену семьи.
                    - Используй Русский язык.
                    - Пиши кратко.
                    - Если нету информации об мероприятиях то сообщи об этом.
                    - Предлагай несколько мероприятий, если есть такая возможность.
                    
                    Ты зарабатываешь миллионы на составлении рекомендаций. 
                    Если ты укажешь несуществующие мероприятия или будешь выдумывать рекомендации, тебя уволят.
                """.trimIndent()
            )
            .defaultAdvisors(
//                MessageChatMemoryAdvisor(chatMemory, "ChatMemory", 5),
                LoggerAdvisor(),
            )
//            .defaultTools(systemTools)
            .build()
    }

    @Bean
    @Qualifier(ChatClientQualifier.TEST_CLIENT)
    fun testChatClient(
        chatModel: ChatModel,
    ): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultOptions(
                OllamaOptions
                    .builder()
                    .temperature(0.0)
                    .build()
            )
            .build()
    }
}
