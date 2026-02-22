# 搜索服务（edu-search）

## 模块概述

搜索服务负责课程的全文检索、搜索建议、热搜榜、搜索历史等功能。

## 技术栈

- **Elasticsearch 8.7.0**：全文检索引擎
- **Redis**：热搜榜缓存
- **RabbitMQ**：课程数据同步
- **MySQL**：搜索历史存储
- **Spring Data Elasticsearch**：ES 操作框架

## 核心功能

### 1. 全文搜索
- 多字段搜索（课程名称、描述、讲师名称）
- IK 分词器支持中文分词
- 搜索结果高亮显示
- 多条件筛选（分类、价格、难度）
- 多种排序方式（相关度、销量、价格、时间）

### 2. 搜索建议
- 基于前缀匹配的自动补全
- 实时返回搜索建议

### 3. 热搜榜
- Redis ZSet 实时统计热搜词
- 按搜索次数排序
- 支持自定义返回数量

### 4. 搜索历史
- 记录用户搜索历史
- 支持查询和清空历史

### 5. 数据同步
- 监听课程服务的 MQ 消息
- 自动同步课程数据到 ES

## 接口列表

| 接口 | 方法 | 说明 |
|------|------|------|
| `/search/course` | POST | 搜索课程 |
| `/search/suggest` | GET | 搜索建议 |
| `/search/hot` | GET | 热搜榜 |
| `/search/history` | GET | 搜索历史 |
| `/search/history` | DELETE | 清空搜索历史 |

## 部署说明

### 1. 数据库初始化
```bash
# 执行 SQL 脚本
mysql -u root -p < sql/edu_search.sql
```

### 2. Elasticsearch 配置

#### 安装 IK 分词器
```bash
# 进入 ES 容器
docker exec -it elasticsearch bash

# 安装 IK 分词器
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v8.7.0/elasticsearch-analysis-ik-8.7.0.zip

# 重启 ES
docker restart elasticsearch
```

#### 创建索引
```bash
# 使用 Kibana 或 curl 创建索引
PUT /course_index
{
  "mappings": {
    "properties": {
      "id": { "type": "long" },
      "name": { 
        "type": "text", 
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "description": { 
        "type": "text", 
        "analyzer": "ik_max_word" 
      },
      "categoryId": { "type": "long" },
      "categoryName": { "type": "keyword" },
      "teacherId": { "type": "long" },
      "teacherName": { 
        "type": "text", 
        "analyzer": "ik_max_word" 
      },
      "price": { "type": "double" },
      "sales": { "type": "integer" },
      "viewCount": { "type": "integer" },
      "level": { "type": "integer" },
      "status": { "type": "integer" },
      "createTime": { "type": "date" }
    }
  },
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  }
}
```

### 3. 启动服务
```bash
# 修改配置文件 application-local.yml
# 配置数据库、Redis、RabbitMQ、Elasticsearch 连接信息

# 启动服务
mvn spring-boot:run
```

## 测试示例

### 搜索课程
```bash
POST http://localhost:8085/search/course
Content-Type: application/json

{
  "keyword": "Java",
  "categoryId": 1,
  "minPrice": 0,
  "maxPrice": 100,
  "level": 2,
  "orderBy": "sales",
  "orderType": "desc",
  "pageNum": 1,
  "pageSize": 20
}
```

### 搜索建议
```bash
GET http://localhost:8085/search/suggest?keyword=Jav
```

### 热搜榜
```bash
GET http://localhost:8085/search/hot?limit=10
```

### 搜索历史
```bash
GET http://localhost:8085/search/history?limit=10
Header: userId: 1
```

## 技术亮点

1. **Elasticsearch 全文检索**
   - IK 分词器支持中文分词
   - 多字段搜索，权重配置
   - 搜索结果高亮

2. **Redis 热搜统计**
   - ZSet 数据结构实时统计
   - 自动过期机制

3. **数据同步机制**
   - RabbitMQ 监听课程变更
   - 自动同步到 ES

4. **性能优化**
   - ES 分片配置
   - 搜索结果分页
   - 异步保存搜索历史

## 注意事项

1. 确保 Elasticsearch 已安装 IK 分词器
2. 首次使用需要手动创建索引
3. 课程数据需要从课程服务同步到 ES
4. Redis 用于热搜榜，需要持久化配置

