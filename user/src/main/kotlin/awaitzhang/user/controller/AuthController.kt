package awaitzhang.user.controller

import awaitzhang.common.exception.BusinessException
import awaitzhang.common.exception.InvalidTokenException
import awaitzhang.common.jwt.JwtTokenProvider
import awaitzhang.common.jwt.TokenInfo
import awaitzhang.common.repository.TokenRepository
import awaitzhang.common.result.Result
import awaitzhang.user.dto.reponse.AuthResponse
import awaitzhang.user.dto.reponse.RefreshTokenRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * 认证控制器，处理用户登录、Token刷新和登出
 */
@RestController
@RequestMapping("/auth")
class AuthController(
    // JWT令牌提供者
    private val jwtTokenProvider: JwtTokenProvider,
    // Token存储仓库
    private val tokenRepository: TokenRepository,
) {


    /**
     * 刷新Access Token接口
     * @param request 刷新Token请求DTO
     * @return ResponseEntity<AuthResponse> 新的认证响应
     */
    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): Result<AuthResponse> {
        val refreshToken = request.refreshToken

        // 验证Refresh Token是否有效
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw BusinessException(message = "Invalid refresh token")
        }
        // 从Token中解析用户名
        val username = jwtTokenProvider.getUsernameFromToken(refreshToken)
        // 从Redis获取存储的Token信息
        val storedToken = tokenRepository.findTokenInfoByUsername(username)
            ?: throw InvalidTokenException(message = "Token not found")
        // 验证Refresh Token是否匹配
        if (storedToken.refreshToken != refreshToken) {
            throw InvalidTokenException(message = "Refresh token mismatch")
        }


        // 删除Token信息
        tokenRepository.deleteTokenInfo(username)
        val authentication = UsernamePasswordAuthenticationToken(username, null, emptyList())
        val tokenPair = jwtTokenProvider.generateNewTokenPair(authentication)
        // 保存Token信息
        tokenRepository.saveTokenInfo(
            TokenInfo(
                username = username,
                accessToken = tokenPair.accessToken,
                refreshToken = tokenPair.refreshToken,
                accessTokenExpiry = tokenPair.accessExpiry,
                refreshTokenExpiry = tokenPair.refreshExpiry
            )
        )

        // 返回新的认证响应
        return Result.ok(
            AuthResponse(
                accessToken = tokenPair.accessToken,
                refreshToken = tokenPair.refreshToken
            )
        )

    }

    /**
     * 用户登出接口
     * @return ResponseEntity<Void> 空响应
     */
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        // 从Security上下文中获取认证信息
        val authentication = SecurityContextHolder.getContext().authentication
        // 删除Redis中的Token信息
        authentication?.name?.let { username ->
            tokenRepository.deleteTokenInfo(username)
        }
        return ResponseEntity.noContent().build()
    }
}

