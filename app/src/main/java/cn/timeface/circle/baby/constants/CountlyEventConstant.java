package cn.timeface.circle.baby.constants;

/**
 * countly统计事件定义
 * author : YW.SUN Created on 2017/3/7
 * email : sunyw10@gmail.com
 */
public class CountlyEventConstant {
    //登录行为（记录事件应留deviceID和用户userID）
    public final static String LOGIN_EVENT_KEY = "loginEvent";
    public final static String LOGIN_EVENT_PARAM_DEVICE_ID = "deviceID";
    public final static String LOGIN_EVENT_PARAM_USER_ID = "userID";//如无或记录失败  默认为0
    public final static String LOGIN_EVENT_PARAM_LOGIN_TIME = "loginTime";//登陆时间,使用unix 时间戳
    public final static String LOGIN_EVENT_PARAM_FROM = "from";//用户来源渠道  0-时光留影 1—微博  2-微信  3-qq  4-自行注册
    public final static String LOGIN_EVENT_FROM_TIME_FACE = "0";
    public final static String LOGIN_EVENT_FROM_WEIBO = "1";
    public final static String LOGIN_EVENT_FROM_WECHAT = "2";
    public final static String LOGIN_EVENT_FROM_QQ = "3";
    public final static String LOGIN_EVENT_FROM_SELF = "4";

    //注册行为 用户来源渠道（时光流影/微博/微信/qq/自行注册）
    public final static String REGISTER_EVENT_KEY = "registerEvent";
    public final static String REGISTER_EVENT_PARAM_USER_ID = "userID";//如无或记录失败  默认为0
    public final static String REGISTER_EVENT_PARAM_REGITER_TIME = "registerTime";//注册时间

    // 用户使用做书 和加入购物车行为
    public final static String SHOPPING_EVENT_KEY = "shoppingEvent";
    public final static String SHOPPING_EVENT_PARAM_BOOK_ID = "bookID";//书id
    public final static String SHOPPING_EVENT_PARAM_SHOPPING_TIME = "shoppingTime";//加入购物车时间
    public final static String SHOPPING_EVENT_PARAM_ORDER_STATUS = "orderSTATUS";//是否点击进行结算
    public final static String SHOPPING_EVENT_PARAM_USER_ID = "userID";//用户ID  如无，默认为0

    public final static String PRINT_EVENT_KEY = "printEvent";
    public final static String PRINT_EVENT_PARAM_BOOK_ID = "bookID";//书id
    public final static String PRINT_EVENT_PARAM_USER_ID = "userID";//用户ID  如无，默认为0

    //首页三按钮
    public final static String CALENDAR_EVENT_KEY = "calendarEvent";
    public final static String CALENDAR_EVENT_PARAM_USER_ID = "userID";
    public final static String CALENDAR_EVENT_PARAM_TIME = "time";

    public final static String INVITE_EVENT_KEY = "inviteEvent";
    public final static String INVITE_EVENT_PARAM_USER_ID = "userID";
    public final static String INVITE_EVENT_PARAM_TIME = "time";

    public final static String PUBLISH_EVENT_KEY = "publishEvent";
    public final static String PUBLISH_EVENT_PARAM_USER_ID = "userID";
    public final static String PUBLISH_EVENT_PARAM_TIME = "time";

}
