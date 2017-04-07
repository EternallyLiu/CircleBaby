package cn.timeface.circle.baby.constants;

/**
 * 小米推送相关常量
 */
public class MiPushConstant {

    /*---------------------------PUSH消息类型---------------------------*/
    public static final int PUSH_TYPE_CIRCLE_NEW_MEMBER = 103; // 新成员加圈（定位圈首页）
    public static final int PUSH_TYPE_CIRCLE_TEACHER_NEW_PRODUCTION = 117; // 老师创建新作品（定位到该作品的预览页）
    public static final int PUSH_TYPE_CIRCLE_TEACHER_NEW_TIME_LINE = 104; // 老师发布动态（定位到该条动态）
    public static final int PUSH_TYPE_CIRCLE_NEW_TEACHER_AUTHORIZATION = 105; // 管理员发起老师认证（定位到认证列表页面）
    public static final int PUSH_TYPE_CIRCLE_PRODUCTION_REFERENCED = 118; // 发布的照片被别人引用做书并订单支付成功（定位到该作品的预览页）
    public static final int PUSH_TYPE_CIRCLE_NEW_SCHOOL_TASK = 106; // 老师发起新作业（定位到作业该详情页）
    public static final int PUSH_TYPE_CIRCLE_MEMBER_REMOVED = 112; // 被圈主移出（定位圈列表页）
    public static final int PUSH_TYPE_CIRCLE_NEW_SHCOOL_BOOK = 113; // 每月/每学期系统自动生成的家校纪念册 （定位到该作品的预览页）
    public static final int PUSH_TYPE_CIRCLE_NEW_COMMENTS = 108; // 发布信息被评论（定位到该条动态）
    public static final int PUSH_TYPE_CIRCLE_NEW_GOOD = 109; // 发布信息被点赞（定位到该条动态）
    public static final int PUSH_TYPE_CIRCLE_NEW_PHOTO_TAGGED = 110; // 发布的照片被别人加标签（定位到该图片的预览页）
    public static final int PUSH_TYPE_CIRCLE_HOMEWORK_COMMENTS = 107; // 老师点评了宝宝的作业（定位到作业该详情页）
    public static final int PUSH_TYPE_CIRCLE_NEW_PHOTO_LIKED = 111; // 发布的照片被别人加喜欢（定位到该图片的预览页）


    /*---------------------------透传消息类型---------------------------*/
    public static final int TYPE_UNREAD_MSG = 1; // 未读消息
    public static final int TYPE_CIRCLE_MEMBER_REMOVED = 2; // 被圈主移出了圈
    public static final int TYPE_CIRCLE_TEACHER_AUTHORIZATION = 3; // 新的教师认证
    public static final int TYPE_CIRCLE_SCHOOL_TASK = 4; // 新的作业
    public static final int TYPE_CIRCLE_TEACHER_AUTHORIZED = 5; // 成为老师身份
    public static final int TYPE_CIRCLE_TEACHER_UNAUTHORIZED = 6; // 取消老师身份
    public static final int TYPE_CIRCLE_DISBANDED = 7; // 圈被解散
    public static final int TYPE_CIRCLE_COMMIT_HOMEWORK = 8; // 最近提交作业
}
