package awaitzhang.common.repository

import awaitzhang.common.jwt.TokenInfo
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * Token信息仓库，负责Token信息在Redis中的CRUD操作
 */
@Repository
class TokenRepository(private val redisTemplate: RedisTemplate<String, TokenInfo>) {


    /**
     * 保存Token信息到Redis
     * @param tokenInfo Token信息对象
     */
    fun saveTokenInfo(tokenInfo: TokenInfo) {
        // 计算Token剩余有效时间(秒)
        val duration = Duration.between(Instant.now(), tokenInfo.refreshTokenExpiry)

        // 存储到Redis并设置自动过期
        redisTemplate.opsForValue().set(
            getKey(tokenInfo.username), // Redis键格式: token:username
            tokenInfo, // Token信息值
            duration.seconds, // 过期时间(秒)
            TimeUnit.SECONDS // 时间单位
        )
    }

    /**
     * 根据用户名查找Token信息
     * @param username 用户名
     * @return TokenInfo? Token信息对象(可能为null)
     */
    fun findTokenInfoByUsername(username: String): TokenInfo? {
        return redisTemplate.opsForValue().get(getKey(username))
    }

    /**
     * 删除指定用户的Token信息
     * @param username 用户名
     */
    fun deleteTokenInfo(username: String) {
        redisTemplate.delete(getKey(username))
    }

    /**
     * 验证Token是否有效(存在且匹配)
     * @param username 用户名
     * @param refreshToken Refresh Token字符串
     * @return Boolean 是否有效
     */
    fun isTokenValid(username: String, refreshToken: String): Boolean {
        return redisTemplate.opsForValue().get(getKey(username))
            ?.let { it.refreshToken == refreshToken } // 比较Token是否匹配
            ?: false // 如果不存在返回false
    }

    /**
     * 生成Redis键
     * @param username 用户名
     * @return String Redis键
     */
    private fun getKey(username: String): String {
        return "token:$username"
    }
}