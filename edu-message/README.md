# 消息服务 (edu-message)

## 服务简介

消息服务负责站内消息、邮件通知、短信通知等功能，通过 RabbitMQ 异步接收其他服务的消息通知。

## 技术亮点

### 1. RabbitMQ 异步消息
- 监听订单、课程、评论等业务消息
- 异步发送站内消息，不阻塞主业务
- 消息持久化，保证消息不丢失

### 2. 消息模板管理
- 支持消息模板配置
- 动态参数替换（如：订单号、课程名称等）
- 模板类型：站内信、邮件、短信

### 3. 消息分类统计
- 按类型统计未读消息数
- 支持消息筛选（类型、已读状态）
- 消息已读/未读状态管理

## 核心功能

### 1. 站内消息
- 发送站内消息
- 消息列表查询（分页、筛选）
- 标记已读/全部已读
- 删除消息
- 消息统计

### 2. RabbitMQ 消息队列
- 监听订单消息队列（message.order.queue）
- 监听课程消息队列（message.course.queue）
- 监听评论消息队列（message.comment.queue）

### 3. 邮件通知（可选）
- 支持 SMTP 邮件发送
- 配置邮件模板
- 异步发送邮件

## 数据库表

### message（消息表）
```sql
CREATE TABLE `message` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` TINYINT NOT NULL COMMENT '类型 1-系统 2-订单 3-课程 4-评论',
  `title` VARCHAR(100) COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `link_url` VARCHAR(255) COMMENT '链接地址',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读 1-是 0-否',
  `read_time` DATETIME COMMENT '阅读时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_user_id (`user_id`),
  INDEX idx_type (`type`),
  INDEX idx_is_read (`is_read`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';
```

### message_template（消息模板表）
```sql
CREATE TABLE `message_template` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
  `code` VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
  `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `type` TINYINT NOT NULL COMMENT '类型 1-站内信 2-邮件 3-短信',
  `title` VARCHAR(100) COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容模板',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_code (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';
```

## API 接口

### 1. 消息列表
```
POST /message/list
```

请求参数：
```json
{
  "type": 2,        // 消息类型（可选）
  "isRead": 0,      // 已读状态（可选）
  "pageNum": 1,
  "pageSize": 10
}
```

### 2. 标记已读
```
PUT /message/{messageId}/read
```

### 3. 全部已读
```
PUT /message/read-all
```

### 4. 删除消息
```
DELETE /message/{messageId}
```

### 5. 消息统计
```
GET /message/stat
```

响应示例：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "unreadCount": 10,
    "systemUnreadCount": 2,
    "orderUnreadCount": 5,
    "courseUnreadCount": 2,
    "commentUnreadCount": 1
  }
}
```

### 6. 未读数量
```
GET /message/unread-count
```

## RabbitMQ 配置

### 交换机和队列
```
交换机：message.exchange (Topic)

队列：
- message.order.queue   (订单消息)
- message.course.queue  (课程消息)
- message.comment.queue (评论消息)

路由键：
- message.order
- message.course
- message.comment
```

### 其他服务如何发送消息

**示例：订单服务发送消息**
```java
@Autowired
private RabbitTemplate rabbitTemplate;

// 发送订单创建消息
SendMessageDTO dto = new SendMessageDTO();
dto.setUserId(userId);
dto.setType(MessageConstant.TYPE_ORDER);
dto.setTemplateCode("ORDER_CREATE");
dto.setLinkUrl("/order/" + orderId);

Map<String, Object> params = new HashMap<>();
params.put("orderNo", orderNo);
dto.setParams(params);

rabbitTemplate.convertAndSend("message.exchange", "message.order", dto);
```

## 消息模板

### 模板参数替换
模板内容使用 `{参数名}` 占位符，系统会自动替换：

```
模板：您的订单{orderNo}已创建成功，请在30分钟内完成支付
参数：{"orderNo": "202301010001"}
结果：您的订单202301010001已创建成功，请在30分钟内完成支付
```

### 预置模板
- `ORDER_CREATE` - 订单创建通知
- `ORDER_PAY` - 订单支付通知
- `ORDER_CANCEL` - 订单取消通知
- `COURSE_UPDATE` - 课程更新通知
- `COMMENT_REPLY` - 评论回复通知

## 邮件配置（可选）

如需启用邮件功能，需要配置 SMTP：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your_email@qq.com
    password: your_auth_code  # QQ邮箱授权码
```

## 配置说明

### application-local.yml
```yaml
server:
  port: 8085

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/edu_message
    username: root
    password: root
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 启动方式

1. 确保 MySQL 和 RabbitMQ 已启动
2. 执行 SQL 脚本创建数据库和表
3. 启动 Nacos（如果使用服务注册）
4. 运行 `MessageApplication.main()`

## 开发顺序

### ✅ 第一步：站内消息（已完成）
- 消息实体和 Mapper
- 消息服务（发送、查询、已读、删除）
- 消息控制器
- 消息模板管理

### ✅ 第二步：RabbitMQ 异步发送（已完成）
- RabbitMQ 配置（交换机、队列、绑定）
- 消息消费者（监听订单、课程、评论消息）
- 异步处理消息

### 🔲 第三步：邮件通知（可选）
- 邮件服务
- 邮件模板
- 异步发送邮件

## 注意事项

1. 消息模板需要提前在数据库中配置
2. RabbitMQ 消息消费失败会记录日志，不影响主流程
3. 消息删除是物理删除，不是逻辑删除
4. 支持直接访问（测试）和通过网关访问（生产）
5. 邮件功能需要配置 SMTP 服务器

## 扩展功能

### 可以添加的功能
1. 消息推送（WebSocket 实时推送）
2. 短信通知（对接阿里云短信服务）
3. 消息撤回
4. 消息定时发送
5. 消息优先级

