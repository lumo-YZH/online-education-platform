-- ====================================
-- 在线教育平台数据库初始化脚本
-- ====================================

-- ====================================
-- 1. 用户服务数据库
-- ====================================
DROP DATABASE IF EXISTS edu_user;
CREATE DATABASE edu_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edu_user;

-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `phone` VARCHAR(11) UNIQUE COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png' COMMENT '头像',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_username (`username`),
  INDEX idx_phone (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户资料表
CREATE TABLE `user_profile` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `gender` TINYINT COMMENT '性别 1-男 2-女 0-未知',
  `birthday` DATE COMMENT '生日',
  `province` VARCHAR(50) COMMENT '省份',
  `city` VARCHAR(50) COMMENT '城市',
  `intro` VARCHAR(500) COMMENT '个人简介',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表';

-- 学习记录表
CREATE TABLE `user_study_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `section_id` BIGINT COMMENT '小节ID',
  `study_time` INT DEFAULT 0 COMMENT '学习时长(秒)',
  `progress` INT DEFAULT 0 COMMENT '学习进度(%)',
  `last_study_time` DATETIME COMMENT '最后学习时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_user_id (`user_id`),
  INDEX idx_course_id (`course_id`),
  INDEX idx_user_course (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习记录表';

-- 用户优惠券表
CREATE TABLE `user_coupon` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `coupon_name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `coupon_type` TINYINT NOT NULL COMMENT '类型 1-满减 2-折扣',
  `discount_amount` DECIMAL(10,2) COMMENT '优惠金额',
  `discount_rate` DECIMAL(3,2) COMMENT '折扣率',
  `min_amount` DECIMAL(10,2) COMMENT '最低消费金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-未使用 1-已使用 2-已过期',
  `expire_time` DATETIME COMMENT '过期时间',
  `use_time` DATETIME COMMENT '使用时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_user_id (`user_id`),
  INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券表';

-- 插入测试数据
INSERT INTO `user` (`username`, `password`, `phone`, `email`, `nickname`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', 'admin@edu.com', '管理员', 1),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138001', 'zhangsan@edu.com', '张三', 1),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138002', 'lisi@edu.com', '李四', 1);


-- ====================================
-- 2. 课程服务数据库
-- ====================================
DROP DATABASE IF EXISTS edu_course;
CREATE DATABASE edu_course DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edu_course;

-- 课程分类表
CREATE TABLE `course_category` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `icon` VARCHAR(255) COMMENT '图标',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_parent_id (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';

-- 讲师表
CREATE TABLE `course_teacher` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '讲师ID',
  `name` VARCHAR(50) NOT NULL COMMENT '讲师姓名',
  `avatar` VARCHAR(255) COMMENT '头像',
  `title` VARCHAR(100) COMMENT '职称',
  `intro` TEXT COMMENT '简介',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_name (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讲师表';

-- 课程表
CREATE TABLE `course` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
  `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `cover` VARCHAR(255) COMMENT '封面图',
  `description` TEXT COMMENT '课程描述',
  `category_id` BIGINT COMMENT '分类ID',
  `teacher_id` BIGINT COMMENT '讲师ID',
  `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
  `original_price` DECIMAL(10,2) COMMENT '原价',
  `stock` INT DEFAULT 0 COMMENT '库存',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `level` TINYINT DEFAULT 1 COMMENT '难度 1-入门 2-初级 3-中级 4-高级',
  `duration` INT DEFAULT 0 COMMENT '总时长(秒)',
  `is_seckill` TINYINT DEFAULT 0 COMMENT '是否秒杀 1-是 0-否',
  `seckill_price` DECIMAL(10,2) COMMENT '秒杀价格',
  `seckill_stock` INT DEFAULT 0 COMMENT '秒杀库存',
  `seckill_start_time` DATETIME COMMENT '秒杀开始时间',
  `seckill_end_time` DATETIME COMMENT '秒杀结束时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-上架 0-下架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_category_id (`category_id`),
  INDEX idx_teacher_id (`teacher_id`),
  INDEX idx_status (`status`),
  INDEX idx_sales (`sales`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 章节表
CREATE TABLE `course_chapter` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '章节ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `name` VARCHAR(100) NOT NULL COMMENT '章节名称',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_course_id (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节表';

-- 小节表
CREATE TABLE `course_section` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '小节ID',
  `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `name` VARCHAR(100) NOT NULL COMMENT '小节名称',
  `video_id` BIGINT COMMENT '视频ID',
  `duration` INT DEFAULT 0 COMMENT '时长(秒)',
  `is_free` TINYINT DEFAULT 0 COMMENT '是否免费 1-是 0-否',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_chapter_id (`chapter_id`),
  INDEX idx_course_id (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小节表';

-- 插入测试数据
INSERT INTO `course_category` (`name`, `parent_id`, `sort`) VALUES
('前端开发', 0, 1),
('后端开发', 0, 2),
('移动开发', 0, 3),
('数据库', 0, 4),
('云计算', 0, 5);

INSERT INTO `course_teacher` (`name`, `title`, `intro`) VALUES
('张老师', '高级架构师', '10年开发经验，精通Java、Spring全家桶'),
('李老师', '前端专家', '资深前端工程师，Vue、React技术专家'),
('王老师', '算法工程师', '算法竞赛金牌得主，擅长数据结构与算法');

INSERT INTO `course` (`name`, `cover`, `description`, `category_id`, `teacher_id`, `price`, `original_price`, `stock`, `sales`, `level`, `status`) VALUES
('Java从入门到精通', 'https://img.example.com/java.jpg', 'Java零基础入门课程，适合初学者', 2, 1, 199.00, 299.00, 1000, 520, 1, 1),
('Vue3实战开发', 'https://img.example.com/vue.jpg', 'Vue3全家桶实战项目开发', 1, 2, 299.00, 399.00, 800, 380, 2, 1),
('Spring Cloud微服务', 'https://img.example.com/springcloud.jpg', 'Spring Cloud微服务架构实战', 2, 1, 399.00, 599.00, 500, 260, 3, 1),
('MySQL数据库优化', 'https://img.example.com/mysql.jpg', 'MySQL性能优化与调优', 4, 1, 199.00, 299.00, 600, 180, 2, 1),
('算法与数据结构', 'https://img.example.com/algorithm.jpg', '算法竞赛必备知识', 2, 3, 299.00, 399.00, 700, 420, 3, 1);


-- ====================================
-- 3. 订单服务数据库
-- ====================================
DROP DATABASE IF EXISTS edu_order;
CREATE DATABASE edu_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edu_order;

-- 订单表
CREATE TABLE `order_info` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `course_name` VARCHAR(100) COMMENT '课程名称',
  `course_cover` VARCHAR(255) COMMENT '课程封面',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `coupon_id` BIGINT COMMENT '优惠券ID',
  `coupon_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `pay_type` TINYINT COMMENT '支付方式 1-支付宝 2-微信',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-未支付 1-已支付 2-已取消 3-已退款',
  `pay_time` DATETIME COMMENT '支付时间',
  `cancel_time` DATETIME COMMENT '取消时间',
  `refund_time` DATETIME COMMENT '退款时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_order_no (`order_no`),
  INDEX idx_user_id (`user_id`),
  INDEX idx_course_id (`course_id`),
  INDEX idx_status (`status`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单明细表
CREATE TABLE `order_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `course_name` VARCHAR(100) COMMENT '课程名称',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_order_id (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';


-- ====================================
-- 4. 视频服务数据库
-- ====================================
DROP DATABASE IF EXISTS edu_video;
CREATE DATABASE edu_video DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edu_video;

-- 视频表
CREATE TABLE `video` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '视频ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `section_id` BIGINT COMMENT '小节ID',
  `title` VARCHAR(100) COMMENT '标题',
  `url` VARCHAR(500) NOT NULL COMMENT '视频地址',
  `cover` VARCHAR(255) COMMENT '封面图',
  `duration` INT DEFAULT 0 COMMENT '时长(秒)',
  `size` BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
  `format` VARCHAR(20) COMMENT '格式 mp4/m3u8',
  `resolution` VARCHAR(20) COMMENT '分辨率 720p/1080p',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-删除 2-转码中',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_course_id (`course_id`),
  INDEX idx_section_id (`section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频表';

-- 视频播放记录表
CREATE TABLE `video_play_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `video_id` BIGINT NOT NULL COMMENT '视频ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `play_time` INT DEFAULT 0 COMMENT '播放时长(秒)',
  `progress` INT DEFAULT 0 COMMENT '播放进度(%)',
  `last_play_position` INT DEFAULT 0 COMMENT '最后播放位置(秒)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id (`user_id`),
  INDEX idx_video_id (`video_id`),
  INDEX idx_course_id (`course_id`),
  UNIQUE KEY uk_user_video (`user_id`, `video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频播放记录表';


-- ====================================
-- 5. 支付服务数据库
-- ====================================
DROP DATABASE IF EXISTS edu_pay;
CREATE DATABASE edu_pay DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edu_pay;

-- 支付记录表
CREATE TABLE `pay_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `trade_no` VARCHAR(100) UNIQUE COMMENT '第三方交易号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `pay_type` TINYINT NOT NULL COMMENT '支付方式 1-支付宝 2-微信',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待支付 1-支付成功 2-支付失败',
  `pay_time` DATETIME COMMENT '支付时间',
  `callback_time` DATETIME COMMENT '回调时间',
  `callback_content` TEXT COMMENT '回调内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_order_no (`order_no`),
  INDEX idx_trade_no (`trade_no`),
  INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 退款记录表
CREATE TABLE `refund_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退款ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `refund_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '退款单号',
  `trade_no` VARCHAR(100) COMMENT '第三方交易号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `refund_amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
  `refund_reason` VARCHAR(255) COMMENT '退款原因',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-退款中 1-退款成功 2-退款失败',
  `refund_time` DATETIME COMMENT '退款时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_order_no (`order_no`),
  INDEX idx_refund_no (`refund_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款记录表';


-- ====================================
-- 6. 评论服务数据库
-- ====================================
DROP DATABASE IF EXISTS edu_comment;
CREATE DATABASE edu_comment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edu_comment;

-- 评论表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 评论点赞表
CREATE TABLE `comment_like` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` BIGINT NOT NULL COMMENT '评论ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_comment_user (`comment_id`, `user_id`),
  INDEX idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';


-- ====================================
-- 7. 消息服务数据库
-- ====================================
DROP DATABASE IF EXISTS edu_message;
CREATE DATABASE edu_message DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edu_message;

-- 消息表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 消息模板表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息模板表';

-- 插入消息模板
INSERT INTO `message_template` (`code`, `name`, `type`, `title`, `content`) VALUES
('ORDER_CREATE', '订单创建通知', 1, '订单创建成功', '您的订单{orderNo}已创建成功，请在30分钟内完成支付'),
('ORDER_PAY', '订单支付通知', 1, '支付成功', '您的订单{orderNo}已支付成功，金额{amount}元'),
('ORDER_CANCEL', '订单取消通知', 1, '订单已取消', '您的订单{orderNo}已取消'),
('COURSE_UPDATE', '课程更新通知', 1, '课程更新', '您购买的课程{courseName}有新内容更新'),
('COMMENT_REPLY', '评论回复通知', 1, '收到新回复', '{username}回复了您的评论');


-- ====================================
-- 完成
-- ====================================
SELECT '数据库初始化完成！' AS message;

