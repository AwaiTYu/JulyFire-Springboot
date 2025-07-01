package awaitzhang.common.aop

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Aspect
@Component
class ApiLogAspect {

    private val log = LoggerFactory.getLogger(ApiLogAspect::class.java)

    @Around("execution(* awaitzhang..controller..*(..))")
    fun logAround(joinPoint: ProceedingJoinPoint): Any? {
        val request = currentHttpRequest()
        val method = joinPoint.signature.toShortString()
        val args = joinPoint.args.joinToString(", ")
        val start = System.currentTimeMillis()

        val methodType = request?.method ?: "N/A"
        val uri = request?.requestURI ?: "N/A"
        val ip = request?.remoteAddr ?: "N/A"
        val headers = getHeaders(request)

        log.info("→ [{}] {} {} IP: {} 方法: {} 参数: {}", now(), methodType, uri, ip, method, args)
        log.info("→ 请求头: {}", headers)

        val result: Any?
        try {
            result = joinPoint.proceed()
        } catch (ex: Throwable) {
            log.error("← [{}] 接口异常: {} - {}", now(), method, ex.message)
            throw ex
        }

        val duration = System.currentTimeMillis() - start
        log.info("← [{}] 响应接口: {} 耗时: {}ms 结果: {}", now(), method, duration, shorten(result))

        return result
    }

    /** 获取当前 HTTP 请求对象 */
    private fun currentHttpRequest(): HttpServletRequest? {
        return (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
    }

    /** 获取请求头信息（格式化为字符串） */
    private fun getHeaders(request: HttpServletRequest?): String {
        if (request == null) return "无请求头"
        val headerNames = request.headerNames ?: return "无请求头"
        val headers = mutableMapOf<String, String>()
        while (headerNames.hasMoreElements()) {
            val name = headerNames.nextElement()
            headers[name] = request.getHeader(name)
        }
        return headers.entries.joinToString { "${it.key}=${it.value}" }
    }

    /** 当前时间字符串 */
    private fun now(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))

    /** 返回值过长时截断 */
    private fun shorten(obj: Any?): String {
        return when {
            obj == null -> "null"
            obj.toString().length > 300 -> obj.toString().substring(0, 300) + "...(截断)"
            else -> obj.toString()
        }
    }
}
