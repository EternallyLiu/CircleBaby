package cn.timeface.circle.baby.constants;

/**
 * author: rayboot  Created on 16/1/22.
 * email : sy0725work@gmail.com
 */
public class TypeConstants {
    public static final int USER_FROM_LOCAL = 0;
    public static final int USER_FROM_SINA = 1;
    public static final int USER_FROM_QQ = 2;
    public static final int USER_FROM_WECHAT = 3;

    //粉丝或者关注
    public static final int ATTENTION = 0;
    public static final int FAN = 1;

    //关注状态 0 未关注 1 已关注 2 相互关注
    public static final int ATTENTION_NO = 0;
    public static final int ATTENTION_YES = 1;
    public static final int ATTENTION_DITTO = 2;

    //0 孩子 1 老师 2 妈妈 3 爸爸
    public static final int TYPE_CHILD = 0;
    public static final int TYPE_TEACHER = 1;
    public static final int TYPE_MOM = 2;
    public static final int TYPE_DAD = 3;

    //修改不同的东西
    public static final int EDIT_NAME = 0;
    public static final int EDIT_BLOOD = 1;
    public static final int EDIT_RELATIONNAME = 2;
    public static final int EDIT_NICKNAME = 3;

    public static final int PUSH_OPEN = 0;
    public static final int PUSH_CLOSE = 1;
    public static final String MI_PUSH_APP_ID = "2882303761517467205";
    public static final String MI_PUSH_APP_KEY = "5501746742205";
    public static final int PUSH_VOICE_SHAKE = 3;
    public static final int PUSH_VOICE = 1;
    public static final int PUSH_SHAKE = 2;
    public static final int PUSH_LIGHT= 4;
    public static final int PUSH_NULL = 0;
    //图片上传文件夹
    public static final String UPLOAD_FOLDER = "baby";
    public static final int PHOTO_COUNT_MAX = 100;

    public static final int PHOTO = 0;//照片
    public static final int VIDEO = 1;//视频
    public static final int DIARY = 2;//日记卡片
    public static final int CARD = 3;//识图卡片
    public static final int QUOTATION = 4;//成长语录

    //分享url
    public static final String SHARE_URL_TIME = "http://www.timeface.cn/baby/babyShare/groupPicture.html?timeId=%1$d";//照片组
    public static final String SHARE_URL_MILESTONR = "http://www.timeface.cn/baby/babyShare/milestone.html?timeId=%1$d";//里程碑
    public static final String SHARE_URL_GENERALMAP = "http://www.timeface.cn/baby/babyShare/generalMap.html?timeId=%1$d";//识图卡片
    public static final String SHARE_URL_DIARY = "http://www.timeface.cn/baby/babyShare/diary.html?timeId=%1$d";//日记卡片
    public static final String SHARE_URL_VIDEO = "http://www.timeface.cn/baby/babyShare/video.html?timeId=%1$d";//视频分享
    public static final String SHARE_URL_INVITE = "http://www.timeface.cn/baby/babyShare/invite.html?babyId=%1$d&inviteCode=%2$d";//邀请码分享

    public static final int PHOTO_TYPE_TIME = 0;//按时间
    public static final int PHOTO_TYPE_USER = 1;//按发布者
    public static final int PHOTO_TYPE_LOCATION = 2;//按位置
    public static final int PHOTO_TYPE_LABEL = 3;//按标签

    // 开放平台BookType
    public static final int OPEN_BOOK_TYPE_PAINTING = 111; //绘画集：111
    public static final int OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK = 113; // 成长纪念册：113
    public static final int OPEN_BOOK_TYPE_GROWTH_QUOTATIONS = 114; // 成长语录：114

}
