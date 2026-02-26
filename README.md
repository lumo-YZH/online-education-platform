# 在线教育平台

一个基于 Spring Cloud 的在线教育平台，微服务架构。

## 项目结构

```
online-education-platform/
├── edu-gateway          # 网关服务，统一入口
├── edu-user             # 用户服务，注册登录那些
├── edu-course           # 课程服务，核心业务
├── edu-order            # 订单服务，处理交易
├── edu-video            # 视频服务，存储和播放
├── edu-search           # 搜索服务，ES 全文检索
├── edu-pay              # 支付服务，对接支付宝
├── edu-comment          # 评论服务，课程评价
├── edu-message          # 消息服务，通知推送
└── edu-common           # 公共模块，工具类
```

## 技术栈

**后端框架**
- Spring Boot 3.0.5
- Spring Cloud 2022.0.2
- Spring Cloud Alibaba 2022.0.0.0

**数据存储**
- MySQL 8.0 - 主数据库
- Redis - 缓存和分布式锁
- Elasticsearch 8.7 - 搜索引擎
- MinIO - 视频文件存储

**中间件**
- Nacos - 服务注册和配置中心
- Sentinel - 流量控制
- Seata - 分布式事务
- RabbitMQ - 消息队列

**其他**
- MyBatis Plus - ORM 框架
- JWT - 认证
- Knife4j - 接口文档

## 核心功能

### 用户模块
- 手机号注册登录，验证码那套
- JWT 认证，Redis 存 token
- 个人信息管理
- 学习记录追踪

### 课程模块
- 课程分类、章节管理
- 热门课程 Redis 缓存
- 秒杀抢课（Lua 脚本防超卖）
- 课程详情页

### 订单模块
- 下单流程
- Seata 分布式事务（扣库存、扣余额、创建订单）
- 30 分钟未支付自动取消（RabbitMQ 延迟队列）
- 订单状态机

### 视频模块
- MinIO 存储视频文件
- 防盗链（签名 URL）
- 播放进度记录
- 只有购买课程才能看视频

### 搜索模块
- Elasticsearch 全文检索
- 关键词高亮
- 热搜榜（Redis ZSet）
- 搜索历史

### 支付模块
- 支付宝沙箱支付
- 异步回调处理
- 退款功能

### 评论模块
- 课程评价
- 点赞、回复
- 树形评论结构

### 消息模块
- 站内消息
- 邮件通知
- 短信通知（RabbitMQ 异步）

## 快速开始

### 环境要求
- JDK 17
- Maven 3.6+
- MySQL 8.0
- Redis
- Docker（可选，用于快速启动中间件）

### 数据库初始化

```bash
# 导入 SQL 文件
mysql -u root -p < sql/init.sql
mysql -u root -p < sql/nacos-mysql.sql
mysql -u root -p < sql/seata-server.sql
mysql -u root -p < sql/seata-client.sql
mysql -u root -p < sql/edu_search.sql
```

### 启动中间件

用 Docker Compose 一键启动所有中间件：

```bash
docker-compose up -d
```

包含：
- Nacos (18848)
- RabbitMQ (5672, 15672)
- Elasticsearch (19200)
- MinIO (9000, 9001)
- Sentinel (8858)
- Seata (8091)

### 启动服务

按顺序启动：

1. 先启动网关
```bash
cd edu-gateway
mvn spring-boot:run
```

2. 再启动各个业务服务
```bash
# 用户服务
cd edu-user && mvn spring-boot:run

# 课程服务
cd edu-course && mvn spring-boot:run

# 订单服务
cd edu-order && mvn spring-boot:run

# 其他服务同理...
```

### 访问地址

- 网关：http://localhost:8080
- Nacos：http://localhost:18848/nacos (nacos/nacos)
- RabbitMQ：http://localhost:15672 (admin/admin123)
- Sentinel：http://localhost:8858
- MinIO：http://localhost:9001 (minioadmin/minioadmin123)
- 接口文档：http://localhost:8080/doc.html

## 技术亮点

### 1. 秒杀抢课
- Redis + Lua 脚本保证库存扣减原子性
- 防止超卖
- MQ 异步创建订单，削峰

### 2. 分布式事务
- Seata AT 模式
- 订单创建涉及三个服务：扣库存、扣余额、创建订单
- 任何一步失败全部回滚

### 3. 订单超时取消
- RabbitMQ 延迟队列
- 30 分钟未支付自动取消
- 自动恢复库存

### 4. 视频防盗链
- 签名 URL，有效期 1 小时
- 校验用户是否购买课程
- 防止视频链接泄露

### 5. 缓存优化
- 热门课程 Redis 缓存
- 缓存穿透：布隆过滤器
- 缓存击穿：互斥锁
- 缓存雪崩：随机过期时间
- 延迟双删保证一致性

### 6. 搜索优化
- Elasticsearch 分词搜索
- 关键词高亮
- 按相关度排序
- 热搜榜实时更新

## 配置说明

主要配置在 Nacos 配置中心，本地配置文件在各服务的 `resources` 目录：

- `application.yml` - 基础配置
- `application-dev.yml` - 开发环境
- `application-local.yml` - 本地环境

需要修改的配置：
- 数据库连接信息
- Redis 地址
- Nacos 地址
- MinIO 密钥
- 支付宝密钥
