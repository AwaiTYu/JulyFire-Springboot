package awaitzhang.user.controller

import awaitzhang.common.config.CoroutineConfig
import awaitzhang.common.constant.RedisConstant
import awaitzhang.common.exception.ErrorCode
import awaitzhang.common.jwt.JwtTokenProvider
import awaitzhang.common.jwt.TokenInfo
import awaitzhang.common.repository.TokenRepository
import awaitzhang.common.result.Result
import awaitzhang.common.util.SnowflakeIdGenerator
import awaitzhang.user.domain.Users
import awaitzhang.user.dto.reponse.AuthResponse
import awaitzhang.user.dto.request.EmailCodeRequest
import awaitzhang.user.dto.request.LoginRequest
import awaitzhang.user.dto.request.RegisterRequest
import awaitzhang.user.mapper.UsersMapper
import awaitzhang.user.service.UsersService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

/**
 * 用户相关
 */
@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var usersMapper: UsersMapper

    @Autowired
    lateinit var usersService: UsersService

    @Autowired
    lateinit var coroutineConfig: CoroutineConfig

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, String>

    // Spring Security的认证管理器
    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    // JWT令牌提供者
    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    // Token存储仓库
    @Autowired
    lateinit var tokenRepository: TokenRepository

    /**
     * 用户注册接口
     *
     * @param request 用户上传信息
     * @return 注册结果
     */
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): Result<Unit> {
        if (!usersService.isValidEmailFormat(request.email)) return Result.fail(ErrorCode.EMAIL_FORMAT_INVALID)
        if (usersService.existsByEmail(request.email)) return Result.fail(ErrorCode.USER_EXISTS)
        val redisKey = "${RedisConstant.EMAIL_CODE}${request.email}"
        val storedCode = redisTemplate.opsForValue().get(redisKey)
        if (storedCode != null && storedCode == request.emailCode) {
            // 验证码匹配，消费掉（删除）
            redisTemplate.delete(redisKey)
        } else {
            return Result.fail(ErrorCode.CODE_ERROR)
        }
        return try {
            val randomName = usersService.generateRandomNickname()
            val user = Users(
                id = SnowflakeIdGenerator.nextId(),
                username = request.username?.let { it.ifBlank { randomName } } ?: randomName,
                passwordHash = usersService.encodePassword(request.password),
                email = request.email
            )
            usersMapper.insert(user)
            Result.ok()
        } catch (e: Exception) {
            Result.fail(ErrorCode.SYSTEM_ERROR, message = e.message ?: ErrorCode.SYSTEM_ERROR.message)
        }
    }

    /**
     * 获取邮箱验证码
     *
     * @param request 邮箱
     * @return 发送结果
     */
    @PostMapping("/getEmailCode")
    fun getEmailCode(@Valid @RequestBody request: EmailCodeRequest): Result<Unit> {
        try {
            val email = request.email
            val emailSendKey = "${RedisConstant.EMAIL_SEND}$email"
            val emailCodeKey = "${RedisConstant.EMAIL_CODE}$email"
            val lastSentTime = redisTemplate.opsForValue().get(emailSendKey)
            lastSentTime?.let {
                if (it == "1") return Result.fail(ErrorCode.CODE_REPEAT)
            }

            val code = usersService.generateVerificationCode()
            redisTemplate.opsForValue().let {
                it.set(emailSendKey, "1", 1, TimeUnit.MINUTES)
                it.set(emailCodeKey, code, 5, TimeUnit.MINUTES)
            }
            usersService.sendVerificationCodeAsync(request.email, code)
            return Result.ok()
        } catch (e: Exception) {
            return Result.fail(ErrorCode.SYSTEM_ERROR, message = e.message ?: ErrorCode.SYSTEM_ERROR.message)
        }
    }


    /**
     * 用户登录接口
     * @param loginRequest 登录请求DTO
     * @return ResponseEntity<AuthResponse> 认证响应
     */
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): Result<out Any> {
        try {
            val authToken = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            val authentication = authenticationManager.authenticate(authToken)

            // 生成Access Token和Refresh Token
            val (accessToken, refreshToken, accessExpiry, refreshExpiry) = jwtTokenProvider.generateNewTokenPair(
                authentication
            )

            tokenRepository.findTokenInfoByUsername(loginRequest.username)?.let {
                tokenRepository.deleteTokenInfo(it.username)
            }

            // 保存Token信息到Redis
            tokenRepository.saveTokenInfo(
                TokenInfo(
                    username = authentication.name,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    accessTokenExpiry = accessExpiry,
                    refreshTokenExpiry = refreshExpiry
                )
            )

            // 返回认证响应
            return Result.ok(
                AuthResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
        } catch (e: Exception) {
            return Result.fail(ErrorCode.SYSTEM_ERROR, e.message ?: "")
        }

    }


    @RequestMapping("/getUserList")
    fun getUserList(): List<Users> {
        return usersMapper.selectList(null)
    }
}