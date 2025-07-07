package awaitzhang.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    /**
     * 用户名
     */
    val username: String? = null,
    /**
     * 密码
     */
    @field:NotBlank(message = "密码不能为空") val password: String,
    /**
     * 邮箱
     */
    @field:NotBlank(message = "邮箱不能为空") @field:Email(message = "邮箱格式不正确") val email: String,

    /**
     * 验证码
     */
    val emailCode : String
)