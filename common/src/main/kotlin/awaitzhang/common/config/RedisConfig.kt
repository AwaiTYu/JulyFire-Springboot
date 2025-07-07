package awaitzhang.common.config

import awaitzhang.common.jwt.TokenInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun tokenInfoRedisTemplate(
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, TokenInfo> {
        return RedisTemplate<String, TokenInfo>().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()

            // 配置支持 Instant 的序列化器
            val objectMapper = ObjectMapper().apply {
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            }

            val serializer = Jackson2JsonRedisSerializer(TokenInfo::class.java).apply {
                setObjectMapper(objectMapper)
            }

            valueSerializer = serializer
            afterPropertiesSet()
        }
    }
}

