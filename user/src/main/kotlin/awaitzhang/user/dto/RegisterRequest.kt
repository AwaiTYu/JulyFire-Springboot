package awaitzhang.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    val username: String? = null,
    @field:NotBlank(message = "密码不能为空")
    val password: String,
    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式不正确")
    val email: String
)