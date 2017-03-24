package cn.timeface.circle.baby.ui.timelines.Utils;

import com.bluelinelabs.logansquare.LoganSquare;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.PhotoRecode;


/**
 * author : wangshuai Created on 2017/2/13
 * email : wangs1992321@gmail.com
 */
public class JSONUtils {

    private static final Gson gson = new Gson();

    private static Gson getGson() {
        return gson;
    }

    public static <T extends Object> T parse2Object(String json, Class jsonObjectClass) {
        try {
            return (T) LoganSquare.parse(json, jsonObjectClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String parse2JSONString(Object object) {
        if (object == null) return "";
        String json = null;
        json = getGson().toJson(object);
        return json;
    }

    public static String parse2JSONString(List list) {
        if (list == null) return "";
        String json = null;
        json = getGson().toJson(list);
        return json;
    }

}
