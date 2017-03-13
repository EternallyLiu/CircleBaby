package cn.timeface.circle.baby.support.api.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseModule;

/**
 * @author wxw
 * @from 2015/8/26
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PushItem extends BaseModule {
    /**
     * 9 WEB URL(WEB广告)
     */
    public static final int WEB_AD = 9;
    /**
     * 10 @消息
     */
    public static final int At = 10;
    /**
     * 11 密函
     */
    public static final int PrivateMsg = 11;
    /**
     * 12 评论
     */
    public static final int Comment = 12;
    /**
     * 13 观众
     */
    public static final int Follower = 13;
    /**
     * 21.时光被推荐
     */
    public static final int TimeRecommend = 21;
    /**
     * 22.时光书被推荐
     */
    public static final int TimeBookRecommend = 22;
    /**
     * 23.时光被删除
     */
    public static final int TimeDelete = 23;
    /**
     * 24.时光书通过审核上架
     */
    public static final int TimeBookPass = 24;
    /**
     * 25.时光书申请上架未通过审核
     */
    public static final int TimeBookUnPass = 25;
    /**
     * 26.订单未支付
     */
    public static final int OrderNotPay = 26;
    /**
     * 27.成功提交印刷申请
     */
    public static final int OrderChecking = 27;
    /**
     * 28.订单申请印刷审核未通过
     */
    public static final int OrderCheckFailed = 28;
    /**
     * 29.配送中
     */
    public static final int OrderDelivering = 29;
    /**
     * 30.印刷的书已送达
     */
    public static final int OrderDeliveringSuccess = 30;
    /**
     * 31.新活动上线
     */
    public static final int NewActivity = 31;
    /**
     * 时光书缓存
     */
    public static final int Cache = 32;
    /**
     * 升级包下载成功
     */
    public static final int DownloadSuccess = 33;
    /**
     * 收到印书劵
     */
    public static final int BookCoupons = 64;

    /**
     * 小编推荐 1001-时光书
     */
    public static final int DATA_TYPE_RECOMMEND_TIME_BOOK = 1001;
    /**
     * 小编推荐 1002-微信书
     */
    public static final int DATA_TYPE_RECOMMEND_WECHAT_BOOK = 1002;
    /**
     * 小编推荐 1003-QQ书
     */
    public static final int DATA_TYPE_RECOMMEND_QQ_BOOK = 1003;
    /**
     * 小编推荐 1004-博客书
     */
    public static final int DATA_TYPE_RECOMMEND_BLOG_BOOK = 1004;
    /**
     * 小编推荐 1005-时光故事
     */
    public static final int DATA_TYPE_RECOMMEND_TOPIC = 1005;
    /**
     * 小编推荐 1006-时光
     */
    public static final int DATA_TYPE_RECOMMEND_TIME = 1006;
    /**
     * 小编推荐 1007-活动
     */
    public static final int DATA_TYPE_RECOMMEND_EVENT = 1007;


    private static final long serialVersionUID = 1L;
    private int pushId;// PUSH ID
    /**
     * 1：时光书 2：话时代(时光故事) 3：WEB广告 4：时光 5：圈子 8.活动 99：系统消息
     */
    private int from;
    private String dataId;// Push消息体数据ID(时光集,话时代)
    private String url;// Push消息体URL(广告,活动)
    private int badge;// App 图标数量标识
    private String sound;// 消息声音
    private String title;// 消息title
    private String alert;// 内容

//        Push消息体类型
//        9 WEB URL
//        10 @消息
//        11 密函
//        12 评论
//        13 观众

    //        21.时光被推荐
//        22.时光书被推荐
//        23.时光被删除
//        24.书通过审核上架
//        25.书申请上架未通过审核
//        26.订单未支付
//        27.成功提交印刷申请
//        28.申请印刷未通过审核
//        29.配送中
//        30.印刷的书已送达
//        31.新活动上线
    private int dataType;

    public int getPushId() {
        return pushId;
    }

    public void setPushId(int pushId) {
        this.pushId = pushId;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "title ="
                + title
                + "  alert = "
                + alert
                + "   dataType = "
                + dataType
                + "   pushId = "
                + pushId
                + "   from = "
                + from;
    }
}
