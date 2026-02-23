package com.edu.message.constant;

/**
 * 消息常量
 */
public class MessageConstant {
    
    /**
     * 消息类型
     */
    public static final int TYPE_SYSTEM = 1;  // 系统消息
    public static final int TYPE_ORDER = 2;   // 订单消息
    public static final int TYPE_COURSE = 3;  // 课程消息
    public static final int TYPE_COMMENT = 4; // 评论消息
    
    /**
     * 消息模板编码
     */
    public static final String TEMPLATE_ORDER_CREATE = "ORDER_CREATE";   // 订单创建
    public static final String TEMPLATE_ORDER_PAY = "ORDER_PAY";         // 订单支付
    public static final String TEMPLATE_ORDER_CANCEL = "ORDER_CANCEL";   // 订单取消
    public static final String TEMPLATE_COURSE_UPDATE = "COURSE_UPDATE"; // 课程更新
    public static final String TEMPLATE_COMMENT_REPLY = "COMMENT_REPLY"; // 评论回复
    
    /**
     * 消息类型名称
     */
    public static String getTypeName(Integer type) {
        if (type == null) {
            return "未知";
        }
        switch (type) {
            case TYPE_SYSTEM:
                return "系统消息";
            case TYPE_ORDER:
                return "订单消息";
            case TYPE_COURSE:
                return "课程消息";
            case TYPE_COMMENT:
                return "评论消息";
            default:
                return "未知";
        }
    }
}

