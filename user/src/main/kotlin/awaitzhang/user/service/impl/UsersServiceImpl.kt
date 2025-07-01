package awaitzhang.user.service.impl

import awaitzhang.user.domain.Users
import awaitzhang.user.mapper.UsersMapper
import awaitzhang.user.service.UsersService
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.random.Random

/**
 * @author 17163
 * @description 针对表【users(用户信息表：存储平台注册用户的完整档案信息)】的数据库操作Service实现
 * @createDate 2025-06-26 22:48:56
 */
@Service
class UsersServiceImpl : ServiceImpl<UsersMapper, Users>(), UsersService {

//    @Autowired
//    lateinit var passwordEncoder: PasswordEncoder

    val nicknamePool = listOf(
        "星辰", "微光", "清风", "明月", "山海", "浮生", "墨染", "流年",
        "归人", "故里", "烟雨", "孤舟", "听风", "逐梦", "浅笑", "安然",
        "落霞", "长歌", "若水", "静好", "云淡", "风轻", "清欢", "初见",
        "远方", "心安", "归期", "忘忧", "知秋", "拾夏", "朝暮", "归岚",
        "星阑", "梦影", "墨香", "书意", "琴音", "画眉", "执念", "无恙",
        "青衫", "白发", "旧巷", "茶凉", "酒温", "闲云", "野鹤", "归鸿",
        "南风", "北雪", "东篱", "西楼", "春眠", "夏至", "秋寒", "冬暖",
        "半盏", "一壶", "三两", "清酒", "旧梦", "新愁", "归途", "他乡",
        "疏影", "暗香", "流萤", "飞花", "落雪", "听雨", "煮茶", "抚琴",
        "远山", "近水", "归燕", "栖云", "墨痕", "素心", "一笑", "十年",
        "少年", "故人", "天涯", "咫尺", "人间", "烟火", "岁月", "静好"
    )

    val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")


    override fun existsByEmail(email: String): Boolean {
        return query().eq("email", email).one() != null
    }

    override fun encodePassword(password: String): String {
        return  password
    }

    override fun generateRandomNickname(): String {
        val nickname = nicknamePool.random()
        val suffix = Random.nextInt(10000, 99999) // 可选：添加数字后缀保证唯一性
        return "$nickname#$suffix"
    }

    override fun isValidEmailFormat(email: String): Boolean {
        return emailRegex.matches(email)
    }
}




