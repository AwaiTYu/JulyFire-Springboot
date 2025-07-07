package awaitzhang.common.jwt

import java.time.Instant

/**
 * Token对数据类
 * @property accessToken 访问令牌
 * @property refreshToken 刷新令牌
 * @property accessExpiry Access Token过期时间
 * @property refreshExpiry Refresh Token过期时间
 */
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val accessExpiry: Instant,
    val refreshExpiry: Instant
)