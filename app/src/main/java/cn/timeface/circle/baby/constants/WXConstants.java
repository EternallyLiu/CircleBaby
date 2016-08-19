package cn.timeface.circle.baby.constants;

/**
 * 微信支付常量
 *
 * @author yusen (QQ:1522289706)
 * @from 2014年9月12日上午9:17:50
 * @TODO 微信支付常量
 */
public class WXConstants {
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx2dba2bf6ed35cc7b";

    /**
     * 商家向财付通申请的商家id
     */
    public static final String PARTNER_ID = "1220490301";

    public static final String APP_SECRET_KEY = "3c93b362e53309f8ac2e4780f04b7004";
    // wx登陆获取access token
    public static final String Get_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    // wx获取用户信息
    public static final String Get_Person_Info = "https://api.weixin.qq.com/sns/userinfo";

    public static class ShowMsgActivity {
        public static final String STitle = "showmsg_title";
        public static final String SMessage = "showmsg_message";
        public static final String BAThumbData = "showmsg_thumb_data";
    }
}
