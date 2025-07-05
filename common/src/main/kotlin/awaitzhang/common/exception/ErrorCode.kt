package awaitzhang.common.exception


enum class ErrorCode(val code: Int, val message: String) {
    // 通用错误
    SUCCESS(200, "成功"),
    SYSTEM_ERROR(500, "系统异常"),
    VALIDATION_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限访问"),

    // 用户相关
    USER_EXISTS(10001, "用户已存在"),
    USER_NOT_FOUND(10002, "用户不存在"),
    LOGIN_FAILED(10003, "用户名或密码错误"),
    EMAIL_FORMAT_INVALID(10004, "邮箱格式错误"),


    // 验证码相关
    CODE_REPEAT(20001, "验证码已存在"),
    CODE_ERROR(20002, "验证码错误或者过期")
}
