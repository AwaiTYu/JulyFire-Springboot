CREATE DATABASE julyfire;
USE julyfire;
Drop Table IF EXISTS users;
CREATE TABLE users
(
    id            BIGINT PRIMARY KEY NOT NULL COMMENT '用户ID，系统使用雪花算法生成的唯一标识符',
    username      VARCHAR(50)        NOT NULL COMMENT '用户名，可重复，用于展示，不做登录凭证',
    password_hash VARCHAR(255)       NOT NULL COMMENT '密码哈希值，建议使用 BCrypt/Argon2 加密',
    email         VARCHAR(100)       NOT NULL UNIQUE COMMENT '邮箱地址，必须唯一，用作登录凭证或找回密码',
    gender        TINYINT            NOT NULL DEFAULT 0 COMMENT '性别：0=未知，1=男，2=女，3=其他',
    birth_date    DATE COMMENT '出生日期，可为空',
    height        DECIMAL(5, 2) COMMENT '身高，单位厘米（cm），如175.50',
    weight        DECIMAL(5, 2) COMMENT '体重，单位公斤（kg），如60.25',
    bio           TEXT COMMENT '个人简介，可填写任意长度的自我介绍，允许为空',
    phone_model   VARCHAR(255) COMMENT '用户手机型号，如 iPhone 14 Pro，可为空',
    os_type       VARCHAR(50) COMMENT '操作系统类型，如 Android / iOS，可为空',
    created_at    TIMESTAMP          DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间，默认当前时间',
    updated_at    TIMESTAMP          DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账户最近更新时间，随更新自动修改'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户信息表：存储平台注册用户的完整档案信息';
