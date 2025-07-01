package awaitzhang.julyfire.entity

import com.baomidou.mybatisplus.annotation.TableName

@TableName("weibo")
data class Weibo(
    val id: String,
    val user_id: String,
    val content: String,
    val article_url: String,
    val original_pictures: String,
    val retweet_pictures: String?,
    val original: Int = 1,
    val video_url: String,
    val publish_place: String,
    val publish_time: String,
    val publish_tool: String,
    val up_num: Int,
    val retweet_num: Int,
    val comment_num: Int
)
