package awaitzhang.user.domain

import com.baomidou.mybatisplus.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 用户实体类，对应 users 表
 */
@TableName("users")
data class Users(

    @TableId(type = IdType.INPUT)
    val id: Long, // 用户ID，使用雪花算法生成，非自增

    @TableField("username")
    val username: String, // 用户名，可重复，用于展示

    @TableField("password_hash")
    val passwordHash: String, // 加密后的密码（如 bcrypt）

    @TableField("email")
    val email: String, // 邮箱，唯一，用作登录凭证，可为空以支持匿名注册

    @TableField("gender")
    val gender: Int = 0, // 性别：0=未知，1=男，2=女，3=其他

    @TableField("birth_date")
    val birthDate: LocalDate? = null, // 出生日期（可选）

    @TableField("height")
    val height: BigDecimal? = null, // 身高（cm）

    @TableField("weight")
    val weight: BigDecimal? = null, // 体重（kg）

    @TableField("bio")
    val bio: String? = null, // 个人简介

    @TableField("phone_model")
    val phoneModel: String? = null, // 手机型号

    @TableField("os_type")
    val osType: String? = null, // 操作系统类型（如 iOS / Android）

    @TableField("created_at", fill = FieldFill.INSERT)
    val createdAt: LocalDateTime = LocalDateTime.now(), // 创建时间，数据库自动填充

    @TableField("updated_at", fill = FieldFill.INSERT_UPDATE)
    val updatedAt: LocalDateTime? = null // 更新时间，数据库自动填充
)
