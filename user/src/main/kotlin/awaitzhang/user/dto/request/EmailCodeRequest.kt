package awaitzhang.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailCodeRequest(
    /**
     * 邮箱
     */
    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式不正确", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    var email: String = ""
)