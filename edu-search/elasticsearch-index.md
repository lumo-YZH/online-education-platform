# Elasticsearch 索引创建脚本

## 1. 创建课程索引

```json
PUT /course_index
{
  "mappings": {
    "properties": {
      "id": {
        "type": "long"
      },
      "name": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword"
          }
        }
      },
      "description": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "cover": {
        "type": "keyword"
      },
      "categoryId": {
        "type": "long"
      },
      "categoryName": {
        "type": "keyword"
      },
      "teacherId": {
        "type": "long"
      },
      "teacherName": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fields": {
          "keyword": {
            "type": "keyword"
          }
        }
      },
      "price": {
        "type": "double"
      },
      "sales": {
        "type": "integer"
      },
      "viewCount": {
        "type": "integer"
      },
      "level": {
        "type": "integer"
      },
      "status": {
        "type": "integer"
      },
      "createTime": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
      }
    }
  },
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "ik_max_word": {
          "type": "custom",
          "tokenizer": "ik_max_word"
        },
        "ik_smart": {
          "type": "custom",
          "tokenizer": "ik_smart"
        }
      }
    }
  }
}
```

## 2. 查看索引信息

```json
GET /course_index
```

## 3. 删除索引（谨慎使用）

```json
DELETE /course_index
```

## 4. 插入测试数据

```json
POST /course_index/_doc/1
{
  "id": 1,
  "name": "Java 从入门到精通",
  "description": "全面系统地学习 Java 编程语言，包括基础语法、面向对象、集合框架、IO 流、多线程等核心知识",
  "cover": "https://example.com/cover1.jpg",
  "categoryId": 1,
  "categoryName": "后端开发",
  "teacherId": 1,
  "teacherName": "张老师",
  "price": 99.00,
  "sales": 1500,
  "viewCount": 5000,
  "level": 1,
  "status": 1,
  "createTime": "2024-01-01 10:00:00"
}

POST /course_index/_doc/2
{
  "id": 2,
  "name": "Spring Boot 微服务实战",
  "description": "深入学习 Spring Boot 框架，掌握微服务架构设计与实现，包括 Spring Cloud、Docker、Kubernetes 等技术",
  "cover": "https://example.com/cover2.jpg",
  "categoryId": 1,
  "categoryName": "后端开发",
  "teacherId": 2,
  "teacherName": "李老师",
  "price": 199.00,
  "sales": 2000,
  "viewCount": 8000,
  "level": 3,
  "status": 1,
  "createTime": "2024-01-05 14:00:00"
}

POST /course_index/_doc/3
{
  "id": 3,
  "name": "Python 数据分析",
  "description": "使用 Python 进行数据分析，学习 NumPy、Pandas、Matplotlib 等数据分析工具",
  "cover": "https://example.com/cover3.jpg",
  "categoryId": 2,
  "categoryName": "数据分析",
  "teacherId": 3,
  "teacherName": "王老师",
  "price": 149.00,
  "sales": 1200,
  "viewCount": 4500,
  "level": 2,
  "status": 1,
  "createTime": "2024-01-10 09:00:00"
}
```

## 5. 搜索测试

### 5.1 全文搜索
```json
GET /course_index/_search
{
  "query": {
    "multi_match": {
      "query": "Java",
      "fields": ["name^3", "description^2", "teacherName^1"]
    }
  }
}
```

### 5.2 带高亮的搜索
```json
GET /course_index/_search
{
  "query": {
    "multi_match": {
      "query": "Spring Boot",
      "fields": ["name^3", "description^2", "teacherName^1"]
    }
  },
  "highlight": {
    "fields": {
      "name": {
        "pre_tags": ["<em class='highlight'>"],
        "post_tags": ["</em>"]
      },
      "description": {
        "pre_tags": ["<em class='highlight'>"],
        "post_tags": ["</em>"]
      }
    }
  }
}
```

### 5.3 前缀搜索（搜索建议）
```json
GET /course_index/_search
{
  "query": {
    "prefix": {
      "name": "Jav"
    }
  }
}
```

### 5.4 组合查询
```json
GET /course_index/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "Java",
            "fields": ["name^3", "description^2"]
          }
        }
      ],
      "filter": [
        {
          "term": {
            "categoryId": 1
          }
        },
        {
          "range": {
            "price": {
              "gte": 0,
              "lte": 200
            }
          }
        },
        {
          "term": {
            "status": 1
          }
        }
      ]
    }
  },
  "sort": [
    {
      "sales": {
        "order": "desc"
      }
    }
  ]
}
```

## 6. IK 分词器测试

```json
GET /_analyze
{
  "analyzer": "ik_max_word",
  "text": "Java 从入门到精通"
}

GET /_analyze
{
  "analyzer": "ik_smart",
  "text": "Java 从入门到精通"
}
```

