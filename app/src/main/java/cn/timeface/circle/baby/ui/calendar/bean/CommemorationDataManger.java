package cn.timeface.circle.baby.ui.calendar.bean;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.LoganSquare;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

import cn.timeface.circle.baby.ui.calendar.exceptions.AlradyExistException;
import cn.timeface.circle.baby.ui.calendar.exceptions.OutOfLimitException;
import cn.timeface.circle.baby.ui.calendar.response.DateResponse;


/**
 * Created by JieGuo on 16/10/11.
 */

public class CommemorationDataManger {

    private static CommemorationDataManger manger;

    private static final int maxSizeInMonth = 3;

    private Map<String, List<DateObj>> datas = new ArrayMap<>();

    private CommemorationDataManger() {
    }

    public static CommemorationDataManger getInstance() {
        if (manger == null) {
            manger = new CommemorationDataManger();
            manger.datas = new ArrayMap<>();

            for (int i = 1; i < 13; i++) {
                manger.datas.put(String.valueOf(i), new ArrayList<>());
            }
        }
        return manger;
    }

    public void add(String month, DateObj obj) throws OutOfLimitException, AlradyExistException {

        if (!datas.containsKey(month)) {
            datas.put(month, new ArrayList<>());
        }

        if (datas.get(month).size() >= maxSizeInMonth) {
            throw new OutOfLimitException("最多只能添加" + maxSizeInMonth + "条");
        }

        if (isExits(month, obj)) {
            throw new AlradyExistException("一天只能添加一个纪念日");
        }

        datas.get(month).add(obj);
    }

    public void update(DateObj dateObj, String oldDay) {

        DateObj dd = new DateObj();
        dd.setDay(oldDay);
        dd.setMonth(dateObj.getMonth());
        delete(dd);

        add(dateObj.getMonth(), dateObj);
    }

    public void delete(DateObj dateObj) {
        if (datas.containsKey(dateObj.getMonth())) {
            Iterator<DateObj> iterator = datas.get(dateObj.getMonth()).iterator();
            while (iterator.hasNext()) {
                DateObj item = iterator.next();
                if (item.isSameDay(dateObj)) {
                    datas.get(dateObj.getMonth()).remove(item);
                    break;
                }
            }
        }
    }

    private boolean isExits(String month, DateObj obj) {
        for (DateObj item : datas.get(month)) {

            if (item.getDateStringYMD().equals(obj.getDateStringYMD())) {
                return true;
            }
        }
        return false;
    }

    public DateResponse list(String month) {

        DateResponse dateResponse = new DateResponse();
        dateResponse.status = 1;
        if (datas.containsKey(month))
            dateResponse.setData(datas.get(month));
        else {
            dateResponse.setData(new ArrayList<>());
            dateResponse.status = 0;
        }
        return dateResponse;
    }

    public void destroy() {
        manger = null;
        datas.clear();
    }

    public String toData() {

        StringBuilder builder = new StringBuilder();
        try {
            builder.append("[");
            for (String key : datas.keySet()) {
                for (int i = 0; i < datas.get(key).size(); i++) {
                    DateObj item = datas.get(key).get(i);
                    String jsString = LoganSquare.serialize(item);
                    Log.e("jString", jsString);
                    builder.append(jsString);
                    if (i != datas.get(key).size() - 1) {
                        builder.append(",");
                    }
                }
            }
            builder.append("]");
        } catch (IOException e) {
            Log.e("Mannager", "error", e);
        }
        return builder.toString();
    }

    public void loadData(String src) {

        if (TextUtils.isEmpty(src)) {
            return;
        }

        try {
            src = URLDecoder.decode(src, "utf-8");

            JSONArray jsonArray = new JSONArray(src);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //            String content = jsonObject.getString("content");
                String month = jsonObject.getString("month");
                //            String day = jsonObject.getString("day");
                DateObj obj = LoganSquare.parse(jsonObject.toString(), DateObj.class);
                datas.get(month).add(obj);
            }
        } catch (Exception e) {
            Log.e("error", "", e);
        }
    }

    public Map<String, List<DateObj>> getSource() {
        return datas;
    }

    public DateObj getByDay(String month, String day) {
        if (datas.containsKey(month)) {
            for (DateObj item : datas.get(month)) {
                if (item.getDay().equals(day)) {
                    return item;
                }
            }
        }
        return null;
    }
}
