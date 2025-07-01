package awaitzhang.common.exception


import awaitzhang.common.result.Result
import jakarta.validation.ConstraintViolationException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.slf4j.LoggerFactory

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /** 参数校验异常（@Valid） */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): Result<Unit> {
        val errorMsg = ex.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "参数无效" }
        return Result.fail(ErrorCode.VALIDATION_ERROR, errorMsg)
    }

    /** 表单参数绑定异常（如 Controller 参数为 JavaBean） */
    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException): Result<Unit> {
        val errorMsg = ex.allErrors.joinToString(", ") { it.defaultMessage ?: "参数错误" }
        return Result.fail(ErrorCode.VALIDATION_ERROR, errorMsg)
    }

    /** 单个参数 @Validated 抛出的异常 */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): Result<Unit> {
        return Result.fail(ErrorCode.VALIDATION_ERROR, ex.message ?: "参数约束错误")
    }

    /** 业务异常（主动抛出） */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): Result<Unit> {
        log.warn("业务异常：{}", ex.message)
        return Result.fail(ex.errorCode, ex.message)
    }

    /** 非法参数异常（系统抛出） */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Result<Unit> {
        log.warn("非法参数异常：{}", ex.message)
        return Result.fail(ErrorCode.VALIDATION_ERROR, ex.message ?: "非法参数")
    }

    /** 未捕获的系统异常 */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): Result<Unit> {
        log.error("系统异常", ex)
        return Result.fail(ErrorCode.SYSTEM_ERROR)
    }
}