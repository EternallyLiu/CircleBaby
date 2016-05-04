package cn.timeface.circle.baby.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * @author rayboot
 * @from 2014-3-24上午10:17:07
 * @TODO 日期相关类
 */
public class DateUtil {

    public final static String YYYYMMDD_P = "yyyy.MM.dd";

    /**
     * @return yyyy年MM月dd日
     */
    public static String getDate(long timeInMillis) {
        String format = "yyyy年MM月dd日";

        return formatDate(format, timeInMillis);
    }

    /**
     * @return yyyy年MM月dd日
     */
    public static String getYear(long timeInMillis) {
        String format = "yyyy-MM-dd";

        return formatDate(format, timeInMillis);
    }

    /**
     * @return kk点mm分
     */
    public static String getTime(long timeInMillis) {
        String format = "kk点mm分";

        return formatDate(format, timeInMillis);
    }

    public static String getTime2(long timeInMillis) {
        String format = "kk:mm";

        return formatDate(format, timeInMillis);
    }

    public static String getTime3(long timeInMillis) {
        String format = "MM.dd";
        return formatDate(format, timeInMillis);
    }

    /**
     * 返回默认格式24小时制日期（"MM-dd kk:mm"） 例子：03-24 18：18
     */
    public static String formatDate(long timeInMillis) {

        String format = (isCurrentYear(timeInMillis) ? "MM-dd kk:mm" : "yyyy-MM-dd kk:mm");
        return formatDate(format, timeInMillis);
    }

    public static String formatDateHaveYear(long timeInMillis) {

        String format = ("yyyy-MM-dd kk:mm");
        return formatDate(format, timeInMillis);
    }

    /**
     * 周几
     *
     * @param time
     * long 系统时间的long类型
     * @return 周一到周日
     *
     * */
    public static String getWeekOfDate(long time) {

        Date date = new Date(time);
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * 判断当前年
     */
    public static boolean isCurrentYear(long timeMillis) {
        String year = formatDate("yyyy", timeMillis);
        String currentY = formatDate("yyyy", System.currentTimeMillis());
        return year.equals(currentY);
    }

    public static String formatDate(String timeInMillis) {
        return formatDate(Long.valueOf(timeInMillis));
    }

    public static String formatDate(String format, String timeInMillis) {

        return formatDate(format, Long.parseLong(timeInMillis));
    }

    /**
     * 返回默认格式24小时制日期（"MM月dd日 kk:mm"） 例子：03月24日 18:18
     */
    public static String formatDateByZH(long timeInMillis) {
        String format = "MM月dd日 kk:mm";

        return formatDate(format, timeInMillis);
    }

    /**
     * 返回自定义格式化日期
     *
     * @param format {@link java.text.SimpleDateFormat}
     */
    public static String formatDate(String format, long timeInMillis) {
        Calendar dealTime = Calendar.getInstance();
        dealTime.setTimeInMillis(timeInMillis);
        return DateFormat.format(format, dealTime).toString();
    }

    /**
     * 返回当前时间的默认格式24小时制日期（"MM-dd kk:mm"） 例子：03-24 18：18
     */
    public static String getFormattedDate() {
        return formatDate(System.currentTimeMillis());
    }

    /**
     * 返回当前时间的自定义格式化时间
     *
     * @param format {@link java.text.SimpleDateFormat}
     */
    public static String getFormattedDate(String format) {
        return formatDate(format, System.currentTimeMillis());
    }

    /**
     * @return xx天前/xx小时前/xx分钟前/刚刚
     */
    public static String getDisTime(long timeInMillis) {
        if (String.valueOf(timeInMillis).length() == 10) {
            timeInMillis = timeInMillis * 1000;
        }
        String disTime;
        long time = System.currentTimeMillis() - timeInMillis;
        if (time > 3 * 24 * 60 * 60 * 1000) {
            disTime = formatDate("yyyy-MM-dd kk:mm", timeInMillis);
        } else if (time > 24 * 60 * 60 * 1000 && time < 3 * 24 * 60 * 60 * 1000) {
            disTime = time / (24 * 60 * 60 * 1000) + "天前";
        } else if (time > 60 * 60 * 1000) {
            disTime = time / (60 * 60 * 1000) + "小时前";
        } else if (time > 60 * 1000) {
            disTime = time / (60 * 1000) + "分钟前";
        } else {
            disTime = "刚刚";
        }
        return disTime;
    }
}
