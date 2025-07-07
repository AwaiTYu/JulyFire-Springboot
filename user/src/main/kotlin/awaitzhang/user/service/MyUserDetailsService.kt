package awaitzhang.user.service

import awaitzhang.user.mapper.UsersMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component


@Component
class MyUserDetailsService : UserDetailsService {

    @Autowired
    private lateinit var usersMapper: UsersMapper

    override fun loadUserByUsername(email: String?): UserDetails? {
        val user = usersMapper.findByUsername(email) ?: throw UsernameNotFoundException("用户不存在")
        return User(
            user.email,
            user.passwordHash,
            emptyList() // 可以添加用户权限
        )
    }
}