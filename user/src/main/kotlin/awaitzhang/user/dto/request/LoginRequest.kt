package awaitzhang.user.dto.request

data class LoginRequest(
    /**
     * 用户名
     */
    val username: String = "",
    /**
     * 密码
     */
    val password: String = ""
)