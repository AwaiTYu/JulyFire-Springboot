package awaitzhang.julyfire.entity

import com.baomidou.mybatisplus.annotation.TableName

@TableName("user")
data class User(
    val id: String,
    val nickname: String,
    val gender: String?,
    val location: String?,
    val birthday: String?,
    val description: String?,
    val verified_reason: String?,
    val talent: String?,
    val education: String?,
    val work: String?,
    val weibo_num: Int = 0,
    val following: Int = 0,
    val followers: Int = 0,
)
