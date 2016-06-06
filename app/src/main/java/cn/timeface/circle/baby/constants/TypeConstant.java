package cn.timeface.circle.baby.constants;

/**
 * author: rayboot  Created on 15/9/28.
 * email : sy0725work@gmail.com
 */
public class TypeConstant {
    /**
     * 图片质量
     */
    public static final int QUALITY_AUTO = 0;
    public static final int QUALITY_LOW = 1;
    public static final int QUALITY_HIGH = 2;


    // push设置相关
    public static final String PUSH_SETTING_ALL_TAG = "push_setting_all_tag";
    public static final String PUSH_SETTING_VOICE_TAG = "push_setting_voice_tag";
    public static final String PUSH_SETTING_VIBRATE_TAG = "push_setting_vibrate_tag";
    public static final String PUSH_SETTING_PRIVATE_MSG_TAG = "push_setting_private_msg_tag";
    public static final String PUSH_SETTING_NOTIFICATION_TAG = "push_setting_notification_tag";
    //    public static final String PUSH_SETTING_MAIL_TAG = "push_setting_mail_tag";
    //    public static final String PUSH_SETTING_GOOD_TAG = "push_setting_good_tag";
    //    public static final String PUSH_SETTING_FANS_TAG = "push_setting_fans_tag";
    //    public static final String PUSH_SETTING_PERSON_REQ_TAG = "push_setting_person_req_tag";
    //    public static final String PUSH_SETTING_REQ_OK_TAG = "push_setting_req_ok_tag";
    //    public static final String PUSH_SETTING_ADD_STAR_TAG = "push_setting_add_star_tag";


    // 订单状态
    public static final int STATUS_CHECKING = 0; // 审核中
    public static final int STATUS_CHECK_FAILED = 1; // 审核未通过
    public static final int STATUS_PRINTING = 2; // 印刷中
    public static final int STATUS_DELIVERING = 3; // 配送中
    public static final int STATUS_DELIVERY_SUCCESS = 5; // 已送达
    public static final int STATUS_TRANSACATION_CLOSED = 7; // 交易关闭
    //    public static final int STATUS_NOT_CONFIRM = 98; // 未确认
    public static final int STATUS_NOT_PAY = 99; // 未支付

    // 时光书上下架状态
    public static final int BOOK_STATUS_APPLY_UP = 1; // 申请上架
    public static final int BOOK_STATUS_CHECKING = 2; // 已提交申请
    public static final int BOOK_STATUS_UP = 3; // 上架
    public static final int BOOK_STATUS_DOWN = 4; // 下架

    //PodInfo 请求参数type
    public static final int POD_TYPE_TIME_FRAGMENT = 1;//时光碎片
    public static final int POD_TYPE_TIME_BOOK = 2;//时光片
    public static final int POD_TYPE_TIME_BOOK_WECHAT = 3;//微信书
    public static final int POD_TYPE_TIME_BOOK_CIRCLE = 4;//时光圈时光书
    public static final int POD_TYPE_CIRCLE_CONTACT = 5;//时光圈通讯录
    public static final int POD_TYPE_TIME_BOOK_QQ = 6;//QQ相册时光书
    public static final int POD_TYPE_TIME_BOOK_BLOG = 7;//博客时光书
    public static final int POD_TYPE_FAST_CREATE_ALBUM = 8;//极速成书图册

    //BookInfo 请求参数bookType
    public static final int BOOK_TYPE_TIME = 0;//时光书（默认）
    public static final int BOOK_TYPE_WECHAT = 1;//微信书
    public static final int BOOK_TYPE_CIRCLE = 2;//时光圈时光书
    public static final int BOOK_TYPE_CIRCLE_CONTACT = 3;//时光圈通讯录
    public static final int BOOK_TYPE_QQ = 4;//QQ相册时光书
    public static final int BOOK_TYPE_BLOG = 5;//博客时光书
    public static final int BOOK_TYPE_DESK_CALENDAR = 6;//时光台历数据

    //印刷车printCode
    public static final int PRINT_CODE_NORMAL = 8800;//可印刷
    public static final int PRINT_CODE_LIMIT_SOFT_PACK = 8801;//可印刷，限软装（12-90页）
    public static final int PRINT_CODE_LIMIT_LESS = 8802;//少于12页，不可印刷
    public static final int PRINT_CODE_LIMIT_MORE = 8803;//超出300页，不可印刷
    public static final int PRINT_CODE_LIMIT_HAD_DELETE = 8804;//该时光书已被删除，不可印刷
    public static final int PRINT_CODE_LIMIT_MORE_SPLIT = 8805;//超出300页，可拆分印刷

    //recyclerview type
    public static final int VIEW_ITEM_TYPE_NORMAL = 0;
    public static final int VIEW_ITEM_TYPE_PROG = 1;
    public static final int VIEW_ITEM_TYPE_HEAD = 2;
    public static final int VIEW_ITEM_TYPE_FOOT = 3;

    //cardview type
    public static final int CARD_VIEW_TYPE_DYNAMIC = 0;
    public static final int CARD_VIEW_TYPE_TIME = 1;
    public static final int CARD_VIEW_TYPE_MINE_TIME = 2;
    public static final int CARD_VIEW_TYPE_CIRCLE_TIME = 3;

    //订单来源
    public static final int FROM_PHONE = 0;
    public static final int FROM_ITV = 1;
    public static final int FROM_EVENT = 2;

    //图片上传文件夹
    public static final String UPLOAD_FOLDER = "baby";
    public static final String PRINT_UPLOAD_FOLDER = "prints";
    public static final int HOMETYPE_BANNER = 1;
    public static final int HOMETYPE_NAV = 2;
    public static final int HOMETYPE_MODULE = 3;
    //首页模块类型
    /**
     * 时光
     */
    public static final int HOME_ITEM_TIME = 0;
    /**
     * 时光书
     */
    public static final int HOME_ITEM_TIME_BOOK = 1;
    /**
     * 话题
     */
    public static final int HOME_ITEM_TOPIC = 2;
    /**
     * 广告
     */
    public static final int HOME_ITEM_BANNER = 3;
    /**
     * 视频
     */
    public static final int HOME_ITEM_VIDEO = 4;
    /**
     * 天气
     */
    public static final int HOME_ITEM_WEATHER = 5;
    /**
     * 积分抽奖
     */
    public static final int HOME_ITEM_LOTTERY = 6;
    /**
     * 游戏
     */
    public static final int HOME_ITEM_GAME = 7;
    /**
     * 活动
     */
    public static final int HOME_ITEM_ACTIVE = 8;
    public static final int WONDER_TIME = 9;
    public static final int HOME_ITEM_TOP = 10;
    //视频音频
    public static final int MUSIC = 0;
    public static final int VIDEO = 1;

    //寄语状态
    public static final int UPLOAD = 1;
    public static final int COMPLETE = 2;

    //新手任务
    public static final int NEW_USER = 1;
    public static final int COMMON_USER = 0;

    //新手任务执行
    public static final int NEW_USER_JUMP = 0;
    public static final int NEW_USER_COMPLETE = 1;

    //    微信状态
    public static final int INPUT_DATA_FAIL = -3;//    -3 用户微信朋友圈未公开--(导入失败)
    public static final int NO_BIND_ACCOUNT = -2;//    -2 用户没有绑定---绑定账号
    public static final int NO_ATTENTION = -1;//    -1审核未通过----验证-未关注
    public static final int WAIT_FOR_VERIFY = 0;//    0已提交申请 ----等待验证
    public static final int GEN_BOOK_ING = 1;//    1已绑定小编号，正在生成微信书----验证-已关注
    public static final int NEW_BOOK_ING = 2;//    2 新做一本微信书---列表-制作中
    public static final int BOOK_SUCCESS = 3;//    3 微信书制作完成  -列表-


    public static final int FROM_PERSON = 1;//查询个人中心时光书
    public static final int FROM_BOOKSHELF = 0;//查询上架时光书
    public static final int FORM_ORDER_UP = 2;//查询线上订单时光书 orderId 为线上订单号码
    public static final int FORM_ORDER_DOWN = 3;//查询线下订单时光书 orderId 为线下订单号码

    public static final int PUBLISH_TYPE = 0;
    public static final int PUBLISH_EDIT_TYPE = 1;

    public static final int NORMAL_POD = 0;
    public static final int FAST_BOOK = 1;
    public static final int PUBLISH_PREVIEW = 2;

    public static final int QQ_BOOK_STATUS_UNAUTHORIZED = 9901; //9901 未授权QQ
    public static final int QQ_BOOK_STATUS_IMPORT_DATA = 9902; //9902 正在导入数据
    public static final int QQ_BOOK_STATUS_NOT_MAKE = 9903; //9903 已导入数据，未制作QQ书
    public static final int QQ_BOOK_STATUS_NOT_IMPORT = 9904; //9904 已授权QQ，未导入数据
    public static final int QQ_BOOK_STATUS_NOT_HAVE_DATA = 9905; //9905 没有相册书数据

    //字数长度限制相关
    public static final int TEXT_LENGTH_LIMIT_TIME_TITLE = 40; //时光标题限制40字
    public static final int TEXT_LENGTH_LIMIT_SUB_TIME_TITLE = 24; //时光标题限制24字
    public static final int TYPE_NOT_SELECT_MODE = 100;
    public static final int TYPE_SELECT_MODE = 200;
    public static final int TYPE_SELECT_AT_MODE = 300;

    //活动类型
    public static final int EVENT_TYPE_NORMAL = 0;//默认活动
    public static final int EVENT_TYPE_SUBJECT = 1;//专题活动

    /**
     * 单选模式
     */
    public static final int TYPE_SELECT_ONE_MODE = 400;
}
