package awaitzhang.user.dto.reponse

data class AuthResponse(
    /**
     * 临时令牌
     */
    val accessToken: String,
    /**
     * 长期令牌
     */
    val refreshToken: String
)