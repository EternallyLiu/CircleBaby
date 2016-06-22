package cn.timeface.circle.baby.constants;

//常量
public class URLConstant {

    /**
     * 日志服务器
     */
    public static final String BASE_LOG_URL = "http://timefaceapi.timeface.cn/log_service";

    /**
     * 支付服务协议
     */
    public static final String PAY_SERVICE_AGREEMENT = "http://m.timeface.cn/app/APP-Help/html/payNeedKnow.html";

    /**
     * 分享Pod根地址
     */
    private static final String SharePodRoot = "http://m.timeface.cn/podview?";
//    private static final String SharePodRoot = "http://h5.stg1.v5time.net/podview?";

    //****************************QQ相册书接口*******************************
    /**
     * 话题分享根地址
     */
    private static final String ShareTopicRoot = "times/story/";
    /**
     * 时光分享根地址
     */
    private static final String ShareTimeRoot = "time/";


    /**
     * 分享根地址
     */
//    private static final String ShareRoot = "http://www.timeface.cn/";
    private static final String ShareRoot = "http://h5.stg1.v5time.net/";
    private static final String ShareCircle = ShareRoot + "joinTimeCommunity?groupId=";

    /**
     * 获取话题分享地址
     *
     * @param id
     * @return
     */
    public static String getTopicShareUrl(String id) {
        return ShareRoot + ShareTopicRoot + id;
    }

    /**
     * 获取时光分享URL
     *
     * @param id
     * @return
     */
    public static String getTimeShareUrl(String id) {
        return ShareRoot + ShareTimeRoot + id;
    }

    /**
     * 获取时光圈分享URL
     *
     * @param id
     * @return
     */
    public static String getCircleShareUrl(String id) {
        return ShareCircle + id;
    }

    /**
     * 帮助文档
     */
    public static String helpUrl = "http://m.timeface.cn/app/APP-Help/html/help.html";

    /**
     * 服务条款
     */
    public static String clauseUrl = "http://m.timeface.cn/app/APP-Help/html/Clause.html";

    /**
     * 获取pod分享URL
     *
     * @param id
     * @return
     */
    public static String getPODShareUrl(String id, int podType, String authorId, int from) {
        //from 判断前端刚好跟我们是反的
        if (podType == TypeConstant.POD_TYPE_TIME_BOOK_QQ) {
            return SharePodRoot + "infoid=" + id + "&type=1" + "&muid=" + authorId + "&publish=" + (from == 1 ? 0 : 1);
        } else if (podType == TypeConstant.POD_TYPE_TIME_BOOK_WECHAT) {
            return SharePodRoot + "infoid=" + id + "&type=2" + "&muid=" + authorId + "&publish=" + (from == 1 ? 0 : 1);
        } else if (podType == TypeConstant.POD_TYPE_TIME_BOOK_BLOG) {
            return SharePodRoot + "infoid=" + id + "&type=3" + "&muid=" + authorId + "&publish=" + (from == 1 ? 0 : 1);
        }
        return SharePodRoot + "infoid=" + id + "&type=0" + "&muid=" + authorId + "&publish=" + (from == 1 ? 0 : 1);
    }
}
