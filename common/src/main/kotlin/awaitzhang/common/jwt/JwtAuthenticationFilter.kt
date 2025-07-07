package awaitzhang.common.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT认证过滤器，处理每个请求的Token验证
 *
 * 主要功能：
 * 1. 从请求头中提取JWT Token
 * 2. 验证Token有效性
 * 3. 设置认证信息到安全上下文
 */
@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    companion object {
        // 认证头名称
        private const val AUTH_HEADER = "Authorization"
        // Token前缀
        private const val BEARER_PREFIX = "Bearer "
    }

    /**
     * 核心过滤方法
     * @param request HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        if (path.startsWith("/user/login")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // 1. 从请求中获取JWT Token
            getJwtFromRequest(request)?.let { jwt ->

                // 2. 验证Token有效性
                if (jwtTokenProvider.validateToken(jwt)) {

                    // 3. 从Token中解析用户名
                    val username = jwtTokenProvider.getUsernameFromToken(jwt)
                    // 4. 创建认证令牌
                    val authentication = UsernamePasswordAuthenticationToken(
                        username, // 主体
                        null, // 凭证(null表示已认证)
                        emptyList() // 权限列表
                    )
                    .apply {
                        // 设置请求详情
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }

                    // 5. 设置认证信息到安全上下文
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (ex: Exception) {

            SecurityContextHolder.clearContext()
            // 记录认证失败日志
            logger.error("设置用户认证到安全上下文失败", ex)
            // 返回未授权响应
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的令牌")
            return
        }

        // ✅ 只在认证成功时继续传递请求
        filterChain.doFilter(request, response)
    }

    /**
     * 从请求头中提取JWT Token
     * @param request HTTP请求
     * @return String? Token字符串(可能为null)
     */
    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTH_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // 去除"Bearer "前缀
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }
}