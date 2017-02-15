package cn.timeface.circle.baby.ui.timelines.Utils;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;


/**
 * author : wangshuai Created on 2017/2/13
 * email : wangs1992321@gmail.com
 */
public class JSONUtils {

    public static String parse2JSONString(Object object){
        if (object==null)return "";
        String json = null;
        try {
            json = LoganSquare.serialize(object);
            LogUtil.showLog("json:" + json);
        } catch (IOException e) {
            LogUtil.showLog("异常!");
            e.printStackTrace();
        }
        return json;
    }
    public static String parse2JSONString(List list){
        if (list==null)return "";
        String json = null;
        try {
            json = LoganSquare.serialize(list);
            LogUtil.showLog("json:" + json);
        } catch (IOException e) {
            LogUtil.showLog("异常!");
            e.printStackTrace();
        }
        return json;
    }

}
