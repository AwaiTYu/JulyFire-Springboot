package awaitzhang.user.controller

import awaitzhang.common.exception.ErrorCode
import awaitzhang.common.result.Result
import awaitzhang.common.util.SnowflakeIdGenerator
import awaitzhang.user.domain.Users
import awaitzhang.user.dto.RegisterRequest
import awaitzhang.user.mapper.UsersMapper
import awaitzhang.user.service.UsersService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var usersMapper: UsersMapper

    @Autowired
    lateinit var usersService: UsersService

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): Result<Unit> {
        if (!usersService.isValidEmailFormat(request.email)) return Result.fail(ErrorCode.EMAIL_FORMAT_INVALID)
        if (usersService.existsByEmail(request.email)) return Result.fail(ErrorCode.USER_EXISTS)
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


    @RequestMapping("/id")
    fun hello(): String {
        return "hello"
    }

    @RequestMapping("/getUserList")
    fun getUserList(): List<Users> {
        return usersMapper.selectList(null)
    }


}