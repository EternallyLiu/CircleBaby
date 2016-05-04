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
    public static final String APP_ID = "wxa8dcf6056e485fb7";


    public static final String APP_SECRET_KEY = "c0bcf6150453850b44df5f300de86d70";
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
