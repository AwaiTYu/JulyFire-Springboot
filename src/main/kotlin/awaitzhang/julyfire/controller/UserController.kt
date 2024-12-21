package awaitzhang.julyfire.controller

import awaitzhang.julyfire.entity.User
import awaitzhang.julyfire.entity.Weibo
import awaitzhang.julyfire.mapper.UserMapper
import awaitzhang.julyfire.mapper.WeiboMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
class UserController {

    @Autowired
    lateinit var userMapper: UserMapper

    @Autowired
    lateinit var weiboMapper: WeiboMapper


    @RequestMapping("/id")
    fun hello(): String {
        return "hello"
    }

    @RequestMapping("/getUserList")
    fun getUserList(): List<User> {
        return userMapper.selectList(null)
    }

    @RequestMapping("/getWeiboList")
    fun getWeiboList(): List<Weibo> {
        return weiboMapper.selectList(null)
    }

    @PostMapping("/uploadFile")
    fun uploadFile(@RequestParam("file") file: MultipartFile): String {
        return "上传成功${file.name}"
    }

}