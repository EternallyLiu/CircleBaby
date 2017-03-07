package cn.timeface.circle.baby.constants;

import java.util.HashMap;
import java.util.Map;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.support.utils.DeviceUuidFactory;
import ly.count.android.sdk.Countly;

/**
 * countly 事件统计helper
 * author : YW.SUN Created on 2017/3/7
 * email : sunyw10@gmail.com
 */
public class CountlyEventHelper {

    private static class SingletonHolder {
        private static final CountlyEventHelper INSTANCE = new CountlyEventHelper();
    }

    private CountlyEventHelper() {}

    public final static CountlyEventHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 登录行为事件统计
     * @param userId
     */
    public void loginEvent(String userId, String from){
        Map<String, String> params = new HashMap<>();
        params.put(CountlyEventConstant.LOGIN_EVENT_PARAM_DEVICE_ID, new DeviceUuidFactory(App.getInstance()).getDeviceId());
        params.put(CountlyEventConstant.LOGIN_EVENT_PARAM_FROM, from);
        params.put(CountlyEventConstant.LOGIN_EVENT_PARAM_LOGIN_TIME, String.valueOf(System.currentTimeMillis()/1000));
        params.put(CountlyEventConstant.LOGIN_EVENT_PARAM_USER_ID, userId);
        Countly.sharedInstance().recordEvent(CountlyEventConstant.LOGIN_EVENT_KEY, params, 1);
    }

    /**
     * 注册行为事件统计
     * @param userId
     */
    public void registerEvent(String userId){
        Map<String, String> params = new HashMap<>();
        params.put(CountlyEventConstant.REGISTER_EVENT_PARAM_REGITER_TIME, String.valueOf(System.currentTimeMillis()/1000));
        params.put(CountlyEventConstant.REGISTER_EVENT_PARAM_USER_ID, userId);
        Countly.sharedInstance().recordEvent(CountlyEventConstant.REGISTER_EVENT_KEY, params, 1);
    }

    public void shoppingEvent(){

    }

    /**
     * 做书事件统计
     * @param userId
     * @param bookId
     */
    public void printEvent(String userId, String bookId){
        Map<String, String> params = new HashMap<>();
        params.put(CountlyEventConstant.PRINT_EVENT_PARAM_BOOK_ID, bookId);
        params.put(CountlyEventConstant.PRINT_EVENT_PARAM_USER_ID, userId);
        Countly.sharedInstance().recordEvent(CountlyEventConstant.PRINT_EVENT_KEY, params, 1);
    }

    /**
     * 首页台历小图标点击事件统计
     * @param userId
     */
    public void calendarEvent(String userId){
        Map<String, String> params = new HashMap<>();
        params.put(CountlyEventConstant.CALENDAR_EVENT_PARAM_TIME, String.valueOf(System.currentTimeMillis()/1000));
        params.put(CountlyEventConstant.CALENDAR_EVENT_PARAM_USER_ID, userId);
        Countly.sharedInstance().recordEvent(CountlyEventConstant.CALENDAR_EVENT_KEY, params, 1);
    }

    /**
     * 首页亲友团点击事件统计
     * @param userId
     */
    public void inviteEvent(String userId){
        Map<String, String> params = new HashMap<>();
        params.put(CountlyEventConstant.INVITE_EVENT_PARAM_TIME, String.valueOf(System.currentTimeMillis()/1000));
        params.put(CountlyEventConstant.INVITE_EVENT_PARAM_USER_ID, userId);
        Countly.sharedInstance().recordEvent(CountlyEventConstant.INVITE_EVENT_KEY, params, 1);
    }

    /**
     * 首页发布点击事件统计
     * @param userId
     */
    public void publishEvent(String userId){
        Map<String, String> params = new HashMap<>();
        params.put(CountlyEventConstant.PUBLISH_EVENT_PARAM_TIME, String.valueOf(System.currentTimeMillis()/1000));
        params.put(CountlyEventConstant.PUBLISH_EVENT_PARAM_USER_ID, userId);
        Countly.sharedInstance().recordEvent(CountlyEventConstant.PUBLISH_EVENT_KEY, params, 1);
    }

}
