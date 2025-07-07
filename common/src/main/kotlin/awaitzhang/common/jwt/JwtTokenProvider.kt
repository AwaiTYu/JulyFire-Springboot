package awaitzhang.common.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.util.*

/**
 * JWT 工具类，用于生成和解析 Access Token 和 Refresh Token
 */
@Component
class JwtTokenProvider {
    // JWT签名密钥，从配置文件中注入
    @Value("\${spring.jwt.secret}")
    private lateinit var secret: String

    // Access Token过期时间(毫秒)，从配置文件中注入
    @Value("\${spring.jwt.access-token-expiration}")
    private var accessTokenExpiration: Long = 0

    // Refresh Token过期时间(毫秒)，从配置文件中注入
    @Value("\${spring.jwt.refresh-token-expiration}")
    private var refreshTokenExpiration: Long = 0

    // 日志记录器
    private val log = LoggerFactory.getLogger(javaClass)

    // 签名密钥(懒加载)
    private val signingKey: Key by lazy {
        // 使用HMAC-SHA算法生成签名密钥
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    /**
     * 生成全新的Token对(包含Access Token和Refresh Token)
     * @param authentication 用户认证信息
     * @return TokenPair 包含两个Token及其过期时间
     */
    fun generateNewTokenPair(authentication: Authentication): TokenPair {
        // 生成Access Token
        val (accessToken, accessExpiry) = generateToken(authentication.name, accessTokenExpiration)
        // 生成Refresh Token
        val (refreshToken, refreshExpiry) = generateToken(authentication.name, refreshTokenExpiration)
        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessExpiry = accessExpiry,
            refreshExpiry = refreshExpiry
        )
    }

    /**
     * 生成单个JWT令牌的核心方法
     * @param username 用户名
     * @param expiration 过期时间(毫秒)
     * @return Pair<String, Instant> Token字符串和过期时间点
     */
    private fun generateToken(username: String, expiration: Long): Pair<String, Instant> {
        // 当前时间
        val now = Instant.now()
        // 计算过期时间点
        val expiryDate = now.plusMillis(expiration)

        // 构建JWT令牌
        val token = Jwts.builder()
            .setSubject(username) // 设置用户标识
            .setIssuedAt(Date.from(now)) // 设置签发时间
            .setExpiration(Date.from(expiryDate)) // 设置过期时间
            .signWith(signingKey, SignatureAlgorithm.HS256) // 设置签名算法和密钥
            .compact()

        return Pair(token, expiryDate)
    }

    /**
     * 从Token中解析用户名
     * @param token JWT令牌字符串
     * @return 用户名
     * @throws JwtException 如果Token无效
     */
    fun getUsernameFromToken(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey) // 设置签名密钥用于验证
            .build()
            .parseClaimsJws(token) // 解析令牌
            .body
            .subject // 获取用户名
    }

    /**
     * 验证Token是否有效
     * @param token JWT令牌字符串
     * @return Boolean true表示有效，false表示无效
     */
    fun validateToken(token: String): Boolean {
        return try {
            // 尝试解析令牌，如果成功则有效
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            // 根据不同的异常类型记录日志
            when (ex) {
                is ExpiredJwtException -> log.warn("Token已过期: ${ex.message}")
                is UnsupportedJwtException -> log.warn("不支持的Token格式: ${ex.message}")
                is MalformedJwtException -> log.warn("Token格式错误: ${ex.message}")
                is SignatureException -> log.warn("签名验证失败: ${ex.message}")
                is IllegalArgumentException -> log.warn("Token claims为空: ${ex.message}")
                else -> log.warn("无效Token: ${ex.message}")
            }
            false
        }
    }
}
