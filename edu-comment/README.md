# 评论服务 (edu-comment)

## 服务简介

评论服务负责课程评论、点赞、回复等功能，支持树形评论结构和热门评论缓存。

## 技术亮点

### 1. Redis 缓存热门评论
- 评论统计信息缓存（总数、平均分、各星级数量）
- 缓存过期时间加随机值，防止缓存雪崩
- 评论变更时自动删除相关缓存

### 2. 评论树形结构
- 支持顶级评论和回复评论
- 自动统计回复数量
- 查询评论时自动加载前N条回复

### 3. 点赞功能
- 支持点赞/取消点赞
- 防止重复点赞（数据库唯一索引）
- 实时更新点赞数

## 核心功能

### 1. 评论管理
- 发表评论（支持评分）
- 回复评论
- 删除评论（只能删除自己的）
- 评论列表查询（支持时间/热度排序）

### 2. 点赞功能
- 点赞评论
- 取消点赞
- 查询用户是否已点赞

### 3. 评论统计
- 总评论数
- 平均评分
- 各星级数量统计

## 数据库表

### comment（评论表）
```sql
CREATE TABLE `comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `avatar` VARCHAR(255) COMMENT '头像',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID',
  `reply_user_id` BIGINT COMMENT '回复用户ID',
  `reply_username` VARCHAR(50) COMMENT '回复用户名',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT DEFAULT 0 COMMENT '回复数',
  `rating` TINYINT COMMENT '评分 1-5星',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_course_id (`course_id`),
  INDEX idx_user_id (`user_id`),
  INDEX idx_parent_id (`parent_id`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
```

### comment_like（评论点赞表）
```sql
CREATE TABLE `comment_like` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` BIGINT NOT NULL COMMENT '评论ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_comment_user (`comment_id`, `user_id`),
  INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';
```

## API 接口

### 1. 评论列表
```
POST /comment/list
```

请求参数：
```json
{
  "courseId": 1,
  "pageNum": 1,
  "pageSize": 10,
  "orderBy": "time"  // time-时间排序, hot-热度排序
}
```

### 2. 添加评论
```
POST /comment/add
```

请求参数：
```json
{
  "courseId": 1,
  "parentId": 0,  // 0表示顶级评论
  "replyUserId": null,
  "content": "课程很不错",
  "rating": 5  // 只有顶级评论才有评分
}
```

### 3. 删除评论
```
DELETE /comment/{commentId}
```

### 4. 点赞评论
```
POST /comment/{commentId}/like
```

### 5. 评论统计
```
GET /comment/stat/{courseId}
```

响应示例：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 100,
    "avgRating": 4.5,
    "star5Count": 60,
    "star4Count": 30,
    "star3Count": 8,
    "star2Count": 1,
    "star1Count": 1
  }
}
```

## 缓存策略

### 1. 评论统计缓存
- Key: `comment:stat:{courseId}`
- 过期时间: 1800秒 + 随机值（0-300秒）
- 更新策略: 评论变更时删除缓存

### 2. 热门评论缓存
- Key: `comment:hot:{courseId}`
- 过期时间: 1800秒 + 随机值
- 更新策略: 评论变更时删除缓存

## 配置说明

### application-local.yml
```yaml
server:
  port: 8084

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/edu_comment
    username: root
    password: root
  
  data:
    redis:
      host: localhost
      port: 6379
```

## 启动方式

1. 确保 MySQL 和 Redis 已启动
2. 执行 SQL 脚本创建数据库和表
3. 启动 Nacos（如果使用服务注册）
4. 运行 `CommentApplication.main()`

## 用户信息传递方案

### 方案说明
评论服务从请求头获取用户信息（userId、username、avatar），这些信息由**网关统一处理**：

1. **网关验证 Token**：解析 JWT 获取 userId
2. **网关查询 Redis**：从 Redis 获取用户缓存信息（username、avatar）
3. **网关传递请求头**：将用户信息添加到请求头传递给下游服务

### 优势
- **性能好**：Redis 查询速度快，避免每次调用用户服务
- **解耦**：评论服务不依赖用户服务，降低服务间耦合
- **统一**：所有服务都能从请求头获取用户信息，保持一致性
- **容错**：即使用户服务挂了，评论功能也不受影响

### 请求头说明
```
userId: 用户ID（必传）
username: 用户名（可选，默认"匿名用户"）
avatar: 头像URL（可选，默认空字符串）
```

## 注意事项

1. **用户信息传递**：用户信息（username、avatar）由网关统一从 Redis 获取并通过请求头传递，无需调用用户服务
2. 评论内容需要进行敏感词过滤（可扩展）
3. 评论点赞使用数据库唯一索引防止重复
4. 删除评论采用逻辑删除，不物理删除
5. 回复评论时会自动更新父评论的回复数
6. 只有顶级评论才有评分字段

