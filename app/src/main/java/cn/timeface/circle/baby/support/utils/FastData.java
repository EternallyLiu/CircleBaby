package cn.timeface.circle.baby.support.utils;

import android.text.TextUtils;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;


/**
 * @author wxw
 * @from 2015/9/15
 * @TODO 快速存取数据类
 */
public class FastData extends Remember {

    /**
     * User ID
     */
    public static final String USER_ID = "user_uid";
    /**
     * User Type
     */
    public static final String USER_TYPE = "user_type";
    /**
     * User Level
     */
    public static final String USER_LEVEL = "user_level";

    /**
     * User Token
     */
    public static final String USER_TOKEN = "user_token";

    /**
     * User Name
     */
    public static final String USER_NAME = "username";

    /**
     * User Avatar
     */
    public static final String AVATAR = "avatar";

    /**
     * RELATION_NAME
     */
    public static final String RELATION_NAME = "relation_name";

    /**
     * is_creator
     */
    public static final String IS_CREATOR = "is_creator";

    /**
     * Account
     */
    public static final String ACCOUNT = "account";

    /**
     * password
     */
    public static final String PASSWORD = "password";

    /**
     * AK
     */
    public static final String UPLOADAK = "UPLOADAK";

    /**
     * SK
     */
    public static final String UPLOADSK = "UPLOADSK";

    /**
     * USER_FROM
     */
    public static final String USER_FROM = "USER_FROM";

    /**
     * PUSH功能是否打开
     */
    public static final String PUSH_OPEN = "PUSH_OPEN";

    /**
     * BABY_AGE
     */
    public static final String BABY_AGE = "BabyAge";

    /**
     * BABY_Avatar
     */
    public static final String BABY_Avatar = "BabyAvatar";
    /**
     * BABY_ID
     */
    public static final String BABY_ID = "BabyId";
    /**
     * BABY_Bithday
     */
    public static final String BABY_Bithday = "BabyBithday";
    /**
     * BABY_Blood
     */
    public static final String BABY_Blood = "BabyBlood";

    public static final String BABY_RealName = "BabyRealName";
    public static final String BABY_ShowRealName = "babyShowRealName";

    /**
     * BABY_Constellation星座
     */
    public static final String BABY_Constellation = "BabyConstellation";
    /**
     * BABY_Gender
     */
    public static final String BABY_Gender = "BabyGender";
    /**
     * BABY_Name
     */
    public static final String BABY_Name = "BabyName";
    /**
     * 地区数据库更新时间
     */
    public static final String REGION_DB_UPDATE_TIME = "up_date";
    /**
     * 下载的apk的路径
     */
    public static final String DOWNLOAD_APK_PATH = "apk_path";

    /**
     * 是否强制升级标识
     */
    public static final String IS_ENFORCE_UPGRADE = "is_enforce";
    /**
     * 上一个用户ID，用于切换账号
     */
    public static final String PRE_USER_UID = "pre_user_uid";

    /**
     * 是否显示印成长 产品介绍
     */
    public static final String PRODUCTION_INTRO = "production_intro";

    /**
     * 获取下载的apk绝对路径
     */
    public static String getApkDownloadPath() {
        return getString(DOWNLOAD_APK_PATH, "");
    }

    /**
     * 存储下载的apk的路径
     */
    public static void setApkDownloadPath(String apkPath) {
        putString(DOWNLOAD_APK_PATH, apkPath);
    }

    /**
     * 获取是否强制升级标识
     */
    public static int getIsEnforceUpgrade() {
        return getInt(IS_ENFORCE_UPGRADE, -1);
    }

    /**
     * 存储是否强制升级标识
     */
    public static void setIsEnforceUpgrade(int enforce) {
        putInt(IS_ENFORCE_UPGRADE, enforce);
    }


    /**
     * 获取默认SharedPreferencesName
     */
    public static String getDefaultSharedPreferencesName() {
        return BuildConfig.APPLICATION_ID + "_preferences";
    }

    /**
     * 获取上一个用户ID，用于切换账号
     */
    public static String getPreUserId() {
        return getString(PRE_USER_UID, null);
    }

    /**
     * 设置上一个用户ID，用于切换账号
     */
    public static void setPreUserId(String userId) {
        putString(PRE_USER_UID, userId);
    }

    public static String getAccount() {
        return getString(ACCOUNT, "17051030354");
    }

    public static void setAccount(String account) {
        putString(ACCOUNT, account);
    }

    public static Boolean getPush() {
        return getBoolean("PUSH_OPEN", true);
    }

    public static void setPush(Boolean open) {
        putBoolean(PUSH_OPEN, open);
    }

    public static String getPassword() {
        return getString(PASSWORD, "");
    }

    public static String BABY_COUNT = "baby_count";

    public static int getBabyCount() {
        return getInt(BABY_COUNT, 0);
    }

    public static void setBabyCount(int count) {
        putInt(BABY_COUNT, count);
    }

    public static void setPassword(String password) {
        putString(PASSWORD, password);
    }

    /**
     * 获取用户ID
     */
    public static String getUserId() {
        return getString(USER_ID, "");
    }

    /**
     * 存储用户ID
     */
    public static void setUserId(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            putString(USER_ID, userId);
        }
    }

    public static int getUserFrom() {
        return getInt(USER_FROM, -1);
    }

    public static void setUserFrom(int userFrom) {
        putInt(USER_FROM, userFrom);
    }

    /**
     * 获取UserToken
     */
    public static String getUserToken() {
        return getString(USER_TOKEN, "Baby-Android");
    }

    /**
     * 存储UserToken
     */
    public static void setUserToken(String userToken) {
        putString(USER_TOKEN, TextUtils.isEmpty(userToken) ? "Baby-Android" : userToken);
    }

    public static String getUploadak() {
        return getString(UPLOADAK, null);
    }

    public static void setUploadak(String uploadak) {
        putString(UPLOADAK, uploadak);
    }

    public static String getUploadsk() {
        return getString(UPLOADSK, null);
    }

    public static void setUploadsk(String uploadsk) {
        putString(UPLOADSK, uploadsk);
    }

    /**
     * 获取UserName
     */
    public static String getUserName() {
        return getString(USER_NAME, "");
    }

    /**
     * 存储UserName
     */
    public static void setUserName(String userName) {
        putString(USER_NAME, userName);
    }

    public static void setRelationName(String relationName) {
        putString(RELATION_NAME, relationName);
    }

    public static String getRelationName() {
        return getString(RELATION_NAME, "");
    }

    private static void setIsCreator(int creator) {
        putInt(IS_CREATOR, creator);
    }

    private static int getIsCreator() {
        return getInt(IS_CREATOR, 0);
    }

    private static void setPhoneNumber(String phoneNumber) {
        putString("phoneNumber", phoneNumber);
    }

    private static String getPhoneNumber() {
        return getString("phoneNumber", "");
    }

    private static void setUniId(String uniId) {
        putString("uniId", uniId);
    }

    private static String getUniId() {
        return getString("uniId", "");
    }

    public static void setUserInfo(UserObj userObj) {
        if (userObj != null) {
            BabyObj.deleteAll();
            setUserName(userObj.getNickName());
            setUserId(userObj.getUserId());
            setAvatar(userObj.getAvatar());
            setBabyObj(userObj.getBabyObj());
            setBabyCount(userObj.getBabycount());
            setRelationName(userObj.getRelationName());
            setIsCreator(userObj.getIsCreator());
            setPhoneNumber(userObj.getPhoneNumber());
            setUniId(userObj.getUniId());
            BabyObj.saveAll(userObj.getBabies());
        }
    }

    public static int getSendSms() {
        return getInt("send_sms", 0);
    }

    public static void setSendSms(int sendSms) {
        putInt("send_sms", sendSms);
    }

    public static UserObj getUserInfo() {
        String userName = getUserName();
        String userId = getUserId();
        String avatar = getAvatar();
        BabyObj babyObj = getBabyObj();
        String relationName = getRelationName();
        int isCreator = getIsCreator();
        String phoneNumber = getPhoneNumber();
        String uniId = getUniId();
        int sendMessag = getSendSms();
        return new UserObj(avatar, babyObj, userName, userId, relationName, isCreator, phoneNumber, uniId, sendMessag);
    }

    public static void setBabyObj(BabyObj babyObj) {
        if (babyObj != null) {
            setBabyId(babyObj.getBabyId());
            babyObj.setUserId(FastData.getUserId());
            babyObj.save();
            BabyObj.refreshBaby(babyObj.getBabyId());
//            setBabyAge(babyObj.getAge());
//            setBabyAvatar(babyObj.getAvatar());
//            setBabyBithday(babyObj.getBithday());
//            setBabyBlood(babyObj.getBlood());
//            setBabyConstellation(babyObj.getConstellation());
//            setBabyGender(babyObj.getGender());
//            setBabyName(babyObj.getName());
//            setBabyRealName(babyObj.getRealName());
//            setShowRealName(babyObj.getShowRealName());
        }
    }

    public static BabyObj getBabyObj() {
//        String babyAge = getBabyAge();
//        String babyAvatar = getBabyAvatar();
//        int babyId = getBabyId();
//        long babyBithday = getBabyBithday();
//        String babyBlood = getBabyBlood();
//        String babyConstellation = getBabyConstellation();
//        int babyGender = getBabyGender();
//        String babyName = getBabyName();
//        BabyObj babyObj = new BabyObj(babyAge, babyAvatar, babyId, babyBithday, babyBlood, babyConstellation, babyGender, babyName);
//        babyObj.setRealName(getBabyRealName());
//        babyObj.setShowRealName(getShowRealName());
//        return babyObj;
        return BabyObj.getInstance(getBabyId());
    }

    public static void setBabyName(String name) {
        if (!TextUtils.isEmpty(name)) {
            BabyObj babyObj = getBabyObj();
            if (babyObj != null) {
                babyObj.setName(name);
                babyObj.update();
            }
//            putString(BABY_Name, name);
        }
    }

    public static String getBabyName() {
//        return getString(BABY_Name, "");
        return getBabyObj().getName();
    }

    public static void setBabyRealName(String name) {
        if (!TextUtils.isEmpty(name)) {
            BabyObj babyObj = getBabyObj();
            if (babyObj != null) {
                babyObj.setRealName(name);
                babyObj.update();
            }
//            putString(BABY_RealName, name);
        }
    }

    public static String getBabyRealName() {
        return getBabyObj().getRealName();
//        return getString(BABY_RealName, "");
    }

    public static void setShowRealName(int code) {
        BabyObj babyObj = getBabyObj();
        if (babyObj != null) {
            babyObj.setShowRealName(code);
            babyObj.update();
        }
//        putInt(BABY_ShowRealName, code);
    }

    public static int getShowRealName() {
        return getBabyObj().getShowRealName();
//        return getInt(BABY_ShowRealName, 0);
    }

    public static void setBabyGender(int gender) {
        BabyObj babyObj = getBabyObj();
        if (babyObj != null) {
            babyObj.setGender(gender);
            babyObj.update();
        }
//        putInt(BABY_Gender, gender);
    }

    public static int getBabyGender() {
        return getBabyObj().getGender();
//        return getInt(BABY_Gender, 0);
    }

    public static void setBabyConstellation(String constellation) {
        if (!TextUtils.isEmpty(constellation)) {
            BabyObj babyObj = getBabyObj();
            if (babyObj != null) {
                babyObj.setConstellation(constellation);
                babyObj.update();
            }
//            putString(BABY_Constellation, constellation);
        }
    }

    public static String getBabyConstellation() {
        return getBabyObj().getConstellation();
//        return getString(BABY_Constellation, "");
    }

    public static void setBabyBlood(String blood) {
        if (!TextUtils.isEmpty(blood)) {
            BabyObj babyObj = BabyObj.getInstance(getBabyId());
            if (babyObj != null) {
                babyObj.setBlood(blood);
                babyObj.update();
            }
//            putString(BABY_Blood, blood);
        }
    }

    public static String getBabyBlood() {
        return BabyObj.getInstance(getBabyId()).getBlood();
//        return getString(BABY_Blood, "");
    }

    public static void setBabyBithday(long bithday) {
        if (bithday != 0) {
            BabyObj babyObj = BabyObj.getInstance(getBabyId());
            if (babyObj != null) {
                babyObj.setBithday(bithday);
                babyObj.update();
            }
        }
    }

    public static long getBabyBithday() {
        return BabyObj.getInstance(getBabyId()).getBithday();
//        return getLong(BABY_Bithday, 0);
    }

    public static void setBabyId(int babyId) {
        putInt(BABY_ID, babyId);
    }

    public static int getBabyId() {
        return getInt(BABY_ID, 0);
    }

    public static void setBabyAvatar(String avatar) {
        if (!TextUtils.isEmpty(avatar)) {
            BabyObj babyObj = BabyObj.getInstance(getBabyId());
            if (babyObj != null) {
                babyObj.setAvatar(avatar);
                babyObj.update();
            }
        }
    }

    public static String getBabyAvatar() {
        return BabyObj.getInstance(getBabyId()).getAvatar();
//        return getString(BABY_Avatar, "");
    }

    public static void setBabyAge(String age) {
        if (!TextUtils.isEmpty(age)) {
            BabyObj babyObj = getBabyObj();
            if (babyObj != null) {
                babyObj.setAge(age);
                babyObj.update();
            }
        }
    }

    public static String getBabyAge() {
        return getBabyObj().getAge();
//        return getString(BABY_AGE, "");
    }

    /**
     * 获取头像
     */
    public static String getAvatar() {
        return TextUtils.isEmpty(getString(AVATAR, null)) ? null : getString(AVATAR, null);
    }

    /**
     * 存储头像
     */
    public static void setAvatar(String avatar) {
        putString(AVATAR, avatar);
    }

    /**
     * 获取等级
     */
    public static int getLevel() {
        return getInt(USER_LEVEL, 0);
    }

    /**
     * 等级
     */
    public static void setLevel(int level) {
        putInt(USER_LEVEL, level);
    }

    /**
     * 获取用户类型
     */
    public static int getUserType() {
        return getInt(USER_TYPE, 0);
    }

    /**
     * 存储用户类型
     */
    public static void setUserType(int userType) {
        putInt(USER_TYPE, userType);
    }

    /**
     * 存储地区数据更新时间
     */
    public static void setRegionDBUpdateTime(long date) {
        putLong(REGION_DB_UPDATE_TIME, date);
    }

    /**
     * 获取地区数据更新时间
     */
    public static long getRegionDBUpdateTime(long defaultValue) {
        return getLong(REGION_DB_UPDATE_TIME, defaultValue);
    }

    /**
     * 是否显示印成长 产品介绍
     */
    public static Boolean showProductionIntro() {
        return getBoolean(PRODUCTION_INTRO, true);
    }

    public static void setProductionIntro(boolean show) {
        putBoolean(PRODUCTION_INTRO, show);
    }
}
