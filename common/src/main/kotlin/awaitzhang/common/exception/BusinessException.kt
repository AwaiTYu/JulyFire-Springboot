package awaitzhang.common.exception


class BusinessException(
    val errorCode: ErrorCode = ErrorCode.SYSTEM_ERROR,
    override val message: String = errorCode.message
) : RuntimeException(message)
