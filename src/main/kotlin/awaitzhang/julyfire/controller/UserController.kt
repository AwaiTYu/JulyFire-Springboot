package awaitzhang.julyfire.controller

import awaitzhang.julyfire.entity.User
import awaitzhang.julyfire.mapper.UserMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @Autowired
    lateinit var userMapper: UserMapper

    @RequestMapping("/id")
    fun hello(): String {
        return "hello"
    }

    @RequestMapping("/getUserList")
    fun getUserWeibo(): List<User> {
        return userMapper.selectList(null)
    }
}