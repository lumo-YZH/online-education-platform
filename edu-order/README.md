# 订单模块 - 阶段一：基础 CRUD

## ✅ 已完成的功能

### 1. 实体类 (Entity)
- ✅ OrderInfo - 订单实体类

### 2. 数据访问层 (Mapper)
- ✅ OrderInfoMapper

### 3. 数据传输对象 (DTO/VO)
- ✅ CreateOrderDTO - 创建订单请求
- ✅ OrderQueryDTO - 订单查询条件
- ✅ OrderListVO - 订单列表展示
- ✅ OrderDetailVO - 订单详情展示

### 4. Feign 客户端
- ✅ CourseClient - 调用课程服务

### 5. 业务逻辑层 (Service)
- ✅ OrderService 接口
- ✅ OrderServiceImpl 实现类

### 6. 控制器层 (Controller)
- ✅ OrderController

### 7. 配置类
- ✅ WebMvcConfig - 拦截器配置（所有接口需要登录）

---

## 🚀 启动步骤

### 1. 确保数据库已初始化
```sql
-- 执行 sql/init.sql 中的订单服务相关表
USE edu_order;
```

### 2. 启动订单服务
```bash
# 端口：8083
# 运行 OrderApplication.java
```

### 3. 访问接口文档
```
http://localhost:8083/doc.html
```

---

## 📋 API 接口列表

### 1. 创建订单（需要登录）
```
POST /order/create
Authorization: Bearer {token}

Request Body:
{
  "courseId": 1,
  "couponId": null
}

Response:
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "ORDER1771575811654321012",
    "courseName": "Java从入门到精通",
    "amount": 199.00,
    "payAmount": 199.00,
    "status": 0,
    "statusDesc": "待支付"
  }
}
```

### 2. 订单列表（需要登录）
```
GET /order/list?status=0&pageNum=1&pageSize=10
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "data": {
    "records": [...],
    "total": 10
  }
}
```

### 3. 订单详情（需要登录）
```
GET /order/1
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "ORDER1771575811654321012",
    "courseName": "Java从入门到精通",
    "amount": 199.00,
    "status": 0,
    "statusDesc": "待支付"
  }
}
```

### 4. 取消订单（需要登录）
```
POST /order/cancel/1
Authorization: Bearer {token}

Response:
{
  "code": 200,
  "message": "订单已取消"
}
```

---

## 🎯 核心功能说明

### 1. 创建订单流程
```
1. 验证用户登录（拦截器）
2. 通过 Feign 调用课程服务获取课程信息
3. 检查课程状态（是否下架）
4. 检查用户是否已购买该课程
5. 生成订单号（ORDER + 时间戳 + 6位随机数）
6. 计算订单金额（暂不考虑优惠券）
7. 创建订单记录
8. 返回订单详情
```

### 2. 订单列表查询
- 支持按订单号、课程名称、订单状态筛选
- 分页查询
- 按创建时间降序排序

### 3. 订单详情
- 验证订单归属（只能查看自己的订单）
- 返回完整的订单信息

### 4. 取消订单
- 只能取消未支付的订单
- 验证订单归属
- 更新订单状态和取消时间

---

## 🔄 与其他模块的对比

| 特性 | 用户模块 | 课程模块 | 订单模块 |
|------|---------|---------|---------|
| 认证要求 | 部分需要登录 | 浏览功能无需登录 | 全部需要登录 |
| 微服务调用 | 无 | 无 | Feign 调用课程服务 |
| 业务复杂度 | 注册、登录 | 多表关联查询 | 订单流程、状态管理 |

---

## 🎨 代码风格一致性

✅ 使用 Lombok 简化代码
✅ 统一的异常处理
✅ 统一的结果封装
✅ 规范的注释和文档
✅ MyBatis Plus 简化 CRUD
✅ Knife4j 接口文档
✅ Feign 微服务调用

---

## 🚧 后续开发计划

### 阶段二：RabbitMQ 延迟队列（明天）
- [ ] 安装 RabbitMQ 和延迟插件
- [ ] 配置交换机和队列
- [ ] 实现消息生产者
- [ ] 实现消息消费者
- [ ] 订单超时自动取消

### 阶段三：订单状态机（后天）
- [ ] 订单状态枚举
- [ ] 订单事件枚举
- [ ] 状态流转逻辑
- [ ] 状态校验

### 阶段四：高级功能（可选）
- [ ] 库存扣减（Redis + Lua）
- [ ] 幂等性保证
- [ ] Seata 分布式事务
- [ ] 订单超时提醒

---

## 📝 测试建议

### 1. 测试创建订单
```bash
# 先登录获取 token
POST http://localhost:8081/user/login

# 创建订单
POST http://localhost:8083/order/create
Authorization: Bearer {token}
{
  "courseId": 1
}
```

### 2. 测试订单列表
```bash
GET http://localhost:8083/order/list
Authorization: Bearer {token}
```

### 3. 测试订单详情
```bash
GET http://localhost:8083/order/1
Authorization: Bearer {token}
```

### 4. 测试取消订单
```bash
POST http://localhost:8083/order/cancel/1
Authorization: Bearer {token}
```

---

## ⚠️ 注意事项

### 1. Feign 调用
- 确保课程服务（8082）已启动
- 确保 Nacos 已启动并注册成功

### 2. 数据库
- 确保 edu_order 数据库已创建
- 确保订单表已创建

### 3. Redis
- 确保 Redis 已启动（database: 2）

### 4. 认证
- 所有订单接口都需要登录
- 需要在请求头中携带 Authorization: Bearer {token}

---

## ✨ 总结

订单模块阶段一已完成，包含：
- ✅ 完整的 CRUD 功能
- ✅ Feign 微服务调用
- ✅ 订单状态管理
- ✅ 权限验证
- ✅ 业务逻辑校验

代码风格与用户、课程模块保持一致，可以直接启动测试！

