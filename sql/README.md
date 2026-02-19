-- ====================================
-- 数据库表结构说明文档
-- ====================================

## 数据库概览

本项目共包含 7 个数据库，分别对应不同的微服务：

1. **edu_user** - 用户服务数据库（4张表）
2. **edu_course** - 课程服务数据库（5张表）
3. **edu_order** - 订单服务数据库（2张表）
4. **edu_video** - 视频服务数据库（2张表）
5. **edu_pay** - 支付服务数据库（2张表）
6. **edu_comment** - 评论服务数据库（2张表）
7. **edu_message** - 消息服务数据库（2张表）

**总计：7个数据库，19张表**

---

## 1. edu_user（用户服务数据库）

### 1.1 user - 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 用户ID（主键） |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码（加密） |
| phone | VARCHAR(11) | 手机号（唯一） |
| email | VARCHAR(100) | 邮箱 |
| avatar | VARCHAR(255) | 头像 |
| nickname | VARCHAR(50) | 昵称 |
| status | TINYINT | 状态 1-正常 0-禁用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- idx_username (username)
- idx_phone (phone)

### 1.2 user_profile - 用户资料表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| user_id | BIGINT | 用户ID（唯一） |
| real_name | VARCHAR(50) | 真实姓名 |
| gender | TINYINT | 性别 1-男 2-女 0-未知 |
| birthday | DATE | 生日 |
| province | VARCHAR(50) | 省份 |
| city | VARCHAR(50) | 城市 |
| intro | VARCHAR(500) | 个人简介 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 1.3 user_study_record - 学习记录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| user_id | BIGINT | 用户ID |
| course_id | BIGINT | 课程ID |
| section_id | BIGINT | 小节ID |
| study_time | INT | 学习时长(秒) |
| progress | INT | 学习进度(%) |
| last_study_time | DATETIME | 最后学习时间 |
| create_time | DATETIME | 创建时间 |

**索引：**
- idx_user_id (user_id)
- idx_course_id (course_id)
- idx_user_course (user_id, course_id)

### 1.4 user_coupon - 用户优惠券表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| user_id | BIGINT | 用户ID |
| coupon_name | VARCHAR(100) | 优惠券名称 |
| coupon_type | TINYINT | 类型 1-满减 2-折扣 |
| discount_amount | DECIMAL(10,2) | 优惠金额 |
| discount_rate | DECIMAL(3,2) | 折扣率 |
| min_amount | DECIMAL(10,2) | 最低消费金额 |
| status | TINYINT | 状态 0-未使用 1-已使用 2-已过期 |
| expire_time | DATETIME | 过期时间 |
| use_time | DATETIME | 使用时间 |
| create_time | DATETIME | 创建时间 |

---

## 2. edu_course（课程服务数据库）

### 2.1 course_category - 课程分类表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 分类ID |
| name | VARCHAR(50) | 分类名称 |
| parent_id | BIGINT | 父分类ID |
| sort | INT | 排序 |
| icon | VARCHAR(255) | 图标 |
| status | TINYINT | 状态 1-启用 0-禁用 |
| create_time | DATETIME | 创建时间 |

### 2.2 course_teacher - 讲师表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 讲师ID |
| name | VARCHAR(50) | 讲师姓名 |
| avatar | VARCHAR(255) | 头像 |
| title | VARCHAR(100) | 职称 |
| intro | TEXT | 简介 |
| status | TINYINT | 状态 1-启用 0-禁用 |
| create_time | DATETIME | 创建时间 |

### 2.3 course - 课程表（核心表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 课程ID |
| name | VARCHAR(100) | 课程名称 |
| cover | VARCHAR(255) | 封面图 |
| description | TEXT | 课程描述 |
| category_id | BIGINT | 分类ID |
| teacher_id | BIGINT | 讲师ID |
| price | DECIMAL(10,2) | 价格 |
| original_price | DECIMAL(10,2) | 原价 |
| stock | INT | 库存 |
| sales | INT | 销量 |
| view_count | INT | 浏览量 |
| level | TINYINT | 难度 1-入门 2-初级 3-中级 4-高级 |
| duration | INT | 总时长(秒) |
| is_seckill | TINYINT | 是否秒杀 1-是 0-否 |
| seckill_price | DECIMAL(10,2) | 秒杀价格 |
| seckill_stock | INT | 秒杀库存 |
| seckill_start_time | DATETIME | 秒杀开始时间 |
| seckill_end_time | DATETIME | 秒杀结束时间 |
| status | TINYINT | 状态 1-上架 0-下架 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- idx_category_id (category_id)
- idx_teacher_id (teacher_id)
- idx_status (status)
- idx_sales (sales)
- idx_create_time (create_time)

### 2.4 course_chapter - 章节表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 章节ID |
| course_id | BIGINT | 课程ID |
| name | VARCHAR(100) | 章节名称 |
| sort | INT | 排序 |
| create_time | DATETIME | 创建时间 |

### 2.5 course_section - 小节表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 小节ID |
| chapter_id | BIGINT | 章节ID |
| course_id | BIGINT | 课程ID |
| name | VARCHAR(100) | 小节名称 |
| video_id | BIGINT | 视频ID |
| duration | INT | 时长(秒) |
| is_free | TINYINT | 是否免费 1-是 0-否 |
| sort | INT | 排序 |
| create_time | DATETIME | 创建时间 |

---

## 3. edu_order（订单服务数据库）

### 3.1 order_info - 订单表（核心表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 订单ID |
| order_no | VARCHAR(50) | 订单号（唯一） |
| user_id | BIGINT | 用户ID |
| course_id | BIGINT | 课程ID |
| course_name | VARCHAR(100) | 课程名称 |
| course_cover | VARCHAR(255) | 课程封面 |
| amount | DECIMAL(10,2) | 订单金额 |
| coupon_id | BIGINT | 优惠券ID |
| coupon_amount | DECIMAL(10,2) | 优惠金额 |
| pay_amount | DECIMAL(10,2) | 实付金额 |
| pay_type | TINYINT | 支付方式 1-支付宝 2-微信 |
| status | TINYINT | 状态 0-未支付 1-已支付 2-已取消 3-已退款 |
| pay_time | DATETIME | 支付时间 |
| cancel_time | DATETIME | 取消时间 |
| refund_time | DATETIME | 退款时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**索引：**
- idx_order_no (order_no)
- idx_user_id (user_id)
- idx_course_id (course_id)
- idx_status (status)
- idx_create_time (create_time)

### 3.2 order_item - 订单明细表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| order_id | BIGINT | 订单ID |
| course_id | BIGINT | 课程ID |
| course_name | VARCHAR(100) | 课程名称 |
| price | DECIMAL(10,2) | 价格 |
| quantity | INT | 数量 |
| create_time | DATETIME | 创建时间 |

---

## 4. edu_video（视频服务数据库）

### 4.1 video - 视频表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 视频ID |
| course_id | BIGINT | 课程ID |
| section_id | BIGINT | 小节ID |
| title | VARCHAR(100) | 标题 |
| url | VARCHAR(500) | 视频地址 |
| cover | VARCHAR(255) | 封面图 |
| duration | INT | 时长(秒) |
| size | BIGINT | 文件大小(字节) |
| format | VARCHAR(20) | 格式 mp4/m3u8 |
| resolution | VARCHAR(20) | 分辨率 720p/1080p |
| status | TINYINT | 状态 1-正常 0-删除 2-转码中 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 4.2 video_play_record - 视频播放记录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| user_id | BIGINT | 用户ID |
| video_id | BIGINT | 视频ID |
| course_id | BIGINT | 课程ID |
| play_time | INT | 播放时长(秒) |
| progress | INT | 播放进度(%) |
| last_play_position | INT | 最后播放位置(秒) |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**唯一索引：**
- uk_user_video (user_id, video_id)

---

## 5. edu_pay（支付服务数据库）

### 5.1 pay_record - 支付记录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 支付ID |
| order_no | VARCHAR(50) | 订单号 |
| trade_no | VARCHAR(100) | 第三方交易号（唯一） |
| user_id | BIGINT | 用户ID |
| pay_type | TINYINT | 支付方式 1-支付宝 2-微信 |
| amount | DECIMAL(10,2) | 支付金额 |
| status | TINYINT | 状态 0-待支付 1-支付成功 2-支付失败 |
| pay_time | DATETIME | 支付时间 |
| callback_time | DATETIME | 回调时间 |
| callback_content | TEXT | 回调内容 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 5.2 refund_record - 退款记录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 退款ID |
| order_no | VARCHAR(50) | 订单号 |
| refund_no | VARCHAR(50) | 退款单号（唯一） |
| trade_no | VARCHAR(100) | 第三方交易号 |
| user_id | BIGINT | 用户ID |
| refund_amount | DECIMAL(10,2) | 退款金额 |
| refund_reason | VARCHAR(255) | 退款原因 |
| status | TINYINT | 状态 0-退款中 1-退款成功 2-退款失败 |
| refund_time | DATETIME | 退款时间 |
| create_time | DATETIME | 创建时间 |

---

## 6. edu_comment（评论服务数据库）

### 6.1 comment - 评论表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 评论ID |
| course_id | BIGINT | 课程ID |
| user_id | BIGINT | 用户ID |
| username | VARCHAR(50) | 用户名 |
| avatar | VARCHAR(255) | 头像 |
| parent_id | BIGINT | 父评论ID |
| reply_user_id | BIGINT | 回复用户ID |
| reply_username | VARCHAR(50) | 回复用户名 |
| content | TEXT | 评论内容 |
| like_count | INT | 点赞数 |
| reply_count | INT | 回复数 |
| rating | TINYINT | 评分 1-5星 |
| status | TINYINT | 状态 1-正常 0-删除 |
| create_time | DATETIME | 创建时间 |

### 6.2 comment_like - 评论点赞表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| comment_id | BIGINT | 评论ID |
| user_id | BIGINT | 用户ID |
| create_time | DATETIME | 创建时间 |

**唯一索引：**
- uk_comment_user (comment_id, user_id)

---

## 7. edu_message（消息服务数据库）

### 7.1 message - 消息表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 消息ID |
| user_id | BIGINT | 用户ID |
| type | TINYINT | 类型 1-系统 2-订单 3-课程 4-评论 |
| title | VARCHAR(100) | 标题 |
| content | TEXT | 内容 |
| link_url | VARCHAR(255) | 链接地址 |
| is_read | TINYINT | 是否已读 1-是 0-否 |
| read_time | DATETIME | 阅读时间 |
| create_time | DATETIME | 创建时间 |

### 7.2 message_template - 消息模板表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 模板ID |
| code | VARCHAR(50) | 模板编码（唯一） |
| name | VARCHAR(100) | 模板名称 |
| type | TINYINT | 类型 1-站内信 2-邮件 3-短信 |
| title | VARCHAR(100) | 标题 |
| content | TEXT | 内容模板 |
| status | TINYINT | 状态 1-启用 0-禁用 |
| create_time | DATETIME | 创建时间 |

---

## 使用说明

### 1. 执行SQL脚本
```bash
# 方式1：使用MySQL命令行
mysql -uroot -p < sql/init.sql

# 方式2：使用Navicat等工具
# 打开 sql/init.sql 文件，直接执行
```

### 2. 验证数据库
```sql
-- 查看所有数据库
SHOW DATABASES;

-- 查看某个数据库的表
USE edu_user;
SHOW TABLES;

-- 查看测试数据
SELECT * FROM user;
SELECT * FROM course;
```

### 3. 测试账号
- 用户名：admin / zhangsan / lisi
- 密码：123456（加密后的密码已存储）

---

## 注意事项

1. **字符集**：所有数据库使用 utf8mb4 字符集，支持emoji表情
2. **索引**：已为常用查询字段添加索引，提升查询性能
3. **外键**：未使用外键约束，由应用层保证数据一致性
4. **测试数据**：已插入部分测试数据，方便开发调试
5. **密码加密**：用户密码使用BCrypt加密存储

