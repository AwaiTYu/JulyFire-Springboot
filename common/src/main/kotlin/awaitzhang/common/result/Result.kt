package awaitzhang.common.result

import awaitzhang.common.exception.ErrorCode

data class Result<T>(
    val code: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> ok(data: T): Result<T> = Result(ErrorCode.SUCCESS.code, ErrorCode.SUCCESS.message, data)
        fun ok(): Result<Unit> = ok(Unit)

        fun fail(errorCode: ErrorCode): Result<Unit> =
            Result(errorCode.code, errorCode.message, null)

        fun <T> fail(errorCode: ErrorCode, message: String): Result<T> =
            Result(errorCode.code, message, null)
    }
}