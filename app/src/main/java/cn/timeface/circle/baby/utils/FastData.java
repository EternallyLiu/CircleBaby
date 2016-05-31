package cn.timeface.circle.baby.utils;

import android.text.TextUtils;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.api.models.objs.BabyObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;


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
    public static final String IS_CREATOR = "relation_name";

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
     * 获取默认SharedPreferencesName
     */
    public static String getDefaultSharedPreferencesName() {
        return BuildConfig.APPLICATION_ID + "_preferences";
    }

    public static String getAccount() {
        return getString(ACCOUNT, "");
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
        putString(USER_ID, userId);
    }

    public static int getUserFrom() {
        return getInt(USER_FROM, 0);
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

    private static void setRelationName(String relationName) {
        putString(RELATION_NAME, relationName);
    }

    private static String getRelationName() {
        return getString(RELATION_NAME,"");
    }

    private static void setIsCreator(int creator) {
        putInt(IS_CREATOR, creator);
    }

    private static int getIsCreator() {
        return getInt(IS_CREATOR, 0);
    }

    public static void setUserInfo(UserObj userObj) {
        setUserName(userObj.getNickName());
        setUserId(userObj.getUserId());
        setAvatar(userObj.getAvatar());
        setBabyObj(userObj.getBabyObj());
        setRelationName(userObj.getRelationName());
        setIsCreator(userObj.getIsCreator());
    }

    public static UserObj getUserInfo() {
        String userName = getUserName();
        String userId = getUserId();
        String avatar = getAvatar();
        BabyObj babyObj = getBabyObj();
        String relationName = getRelationName();
        int isCreator = getIsCreator();

        return new UserObj(avatar, babyObj, userName, userId,relationName,isCreator);
    }

    public static void setBabyObj(BabyObj babyObj) {
        if (null == babyObj) {
            return;
        }
        setBabyAge(babyObj.getAge());
        setBabyAvatar(babyObj.getAvatar());
        setBabyId(babyObj.getBabyId());
        setBabyBithday(babyObj.getBithday());
        setBabyBlood(babyObj.getBlood());
        setBabyConstellation(babyObj.getConstellation());
        setBabyGender(babyObj.getGender());
        setBabyName(babyObj.getName());
    }

    public static BabyObj getBabyObj() {
        String babyAge = getBabyAge();
        String babyAvatar = getBabyAvatar();
        int babyId = getBabyId();
        long babyBithday = getBabyBithday();
        String babyBlood = getBabyBlood();
        String babyConstellation = getBabyConstellation();
        int babyGender = getBabyGender();
        String babyName = getBabyName();
        BabyObj babyObj = new BabyObj(babyAge, babyAvatar, babyId, babyBithday, babyBlood, babyConstellation, babyGender, babyName);
        return babyObj;
    }

    public static void setBabyName(String name) {
        putString(BABY_Name, name);
    }

    public static String getBabyName() {
        return getString(BABY_Name, "");
    }

    public static void setBabyGender(int gender) {
        putInt(BABY_Gender, gender);
    }

    public static int getBabyGender() {
        return getInt(BABY_Gender, 0);
    }

    public static void setBabyConstellation(String constellation) {
        putString(BABY_Constellation, constellation);
    }

    public static String getBabyConstellation() {
        return getString(BABY_Constellation, "");
    }

    public static void setBabyBlood(String blood) {
        putString(BABY_Blood, blood);
    }

    public static String getBabyBlood() {
        return getString(BABY_Blood, "");
    }

    public static void setBabyBithday(long bithday) {
        putLong(BABY_Bithday, bithday);
    }

    public static long getBabyBithday() {
        return getLong(BABY_Bithday,0);
    }

    public static void setBabyId(int babyId) {
        putInt(BABY_ID, babyId);
    }

    public static int getBabyId() {
        return getInt(BABY_ID, 0);
    }

    public static void setBabyAvatar(String avatar) {
        putString(BABY_Avatar, avatar);
    }

    public static String getBabyAvatar() {
        return getString(BABY_Avatar, "");
    }

    public static void setBabyAge(String age) {
        putString(BABY_AGE, age);
    }

    public static String getBabyAge() {
        return getString(BABY_AGE, "");
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

}
