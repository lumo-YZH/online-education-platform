-- 创建搜索服务数据库
CREATE DATABASE IF NOT EXISTS edu_search DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE edu_search;

-- 搜索历史表
CREATE TABLE IF NOT EXISTS search_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    keyword VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    search_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
    INDEX idx_user_id (user_id),
    INDEX idx_keyword (keyword),
    INDEX idx_search_time (search_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';

-- 插入测试数据
INSERT INTO search_history (user_id, keyword, search_time) VALUES
(1, 'Java', '2024-01-15 10:00:00'),
(1, 'Spring Boot', '2024-01-15 11:00:00'),
(1, 'MySQL', '2024-01-15 12:00:00'),
(2, 'Python', '2024-01-15 13:00:00'),
(2, 'Django', '2024-01-15 14:00:00');

