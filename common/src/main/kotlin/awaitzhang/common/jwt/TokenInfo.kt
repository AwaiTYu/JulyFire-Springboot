package awaitzhang.common.jwt

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class TokenInfo(
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    val accessTokenExpiry: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    val refreshTokenExpiry: Instant
)