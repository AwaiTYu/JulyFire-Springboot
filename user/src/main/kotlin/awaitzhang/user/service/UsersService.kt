package awaitzhang.user.service

import awaitzhang.user.domain.Users
import com.baomidou.mybatisplus.extension.service.IService

/**
 * @author 17163
 * @description 针对表【users(用户信息表：存储平台注册用户的完整档案信息)】的数据库操作Service
 * @createDate 2025-06-26 22:48:56
 */
interface UsersService : IService<Users> {
    fun existsByEmail(email: String): Boolean
    fun encodePassword(password: String): String
    fun generateRandomNickname(): String
    fun isValidEmailFormat(email: String): Boolean
    fun generateVerificationCode(): String
    fun sendVerificationCodeAsync(email: String, code: String)
}
