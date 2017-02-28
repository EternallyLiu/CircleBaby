package cn.timeface.circle.baby.support.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public static String getDateYear(long date){
        String format = "yyyy-MM-dd";
        String s = formatDate(format, date);
        String[] split = s.split("-");
        String year = split[0];
        return year;
    }

    public static String getYear2(long timeInMillis) {
        String format = "yyyy-MM-dd";

        return formatDate(format, timeInMillis);
    }

    public static String getMonth(long timeInMillis) {
        String format = "MM:dd";
        String s = formatDate(format, timeInMillis);
        String[] split = s.split(":");
        String month = split[0];
        if (month.startsWith("0")) {
            month = month.substring(1);
        }
        return month;
    }

    public static String getDay(long timeInMillis) {
        String format = "MM:dd";
        String s = formatDate(format, timeInMillis);
        String[] split = s.split(":");
        String day = split[1];
        return day;
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

    public static String getTime4(long timeInMillis) {
        String mm;
        String ss;
        int s = (int) (timeInMillis / 1000);
        int m = s / 60;
        if (m < 10) {
            mm = "0" + m;
        } else {
            mm = m + "";
        }
        s = s - 60 * m;
        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = s + "";
        }
        return mm+":"+ss;
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
     * @param time long 系统时间的long类型
     * @return 周一到周日
     */
    public static String getWeekOfDate(long time) {

        Date date = new Date(time);
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
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
        String s = String.valueOf(timeInMillis);
        if (s.length() < 13) {
            s = s + "000";
            timeInMillis = Long.valueOf(s);
        }
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

    public static long getTime(String time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        long time1 = 0;
        try {
            time1 = simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time1;
    }

    public static String getDate2(long date) {
        String str = date + "";
        str = str.length() < 13 ? str + "000" : str;
        String s = formatDate("yyyy.MM.dd", Long.valueOf(str));
        String today = formatDate("yyyy.MM.dd", System.currentTimeMillis());
        String yestoday = formatDate("yyyy.MM.dd", System.currentTimeMillis() - (24 * 3600000));
        if (s.equals(today)) {
            s = "今天";
        } else if (s.equals(yestoday)) {
            s = "昨天";
        }
        return s;
    }

    /**
     * 计算年龄
     * @param birthday 出生日期
     * @param currentDate 当前时间
     * @return
     */
    public static String getAge(long birthday, long currentDate) {
        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.setTimeInMillis(birthday);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentDate);

        int year = currentCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) - birthdayCalendar.get(Calendar.MONTH);
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH) - birthdayCalendar.get(Calendar.DAY_OF_MONTH);

        return (year == 0 ? "" : (year + "岁")) + (month == 0 ? "" : (month + "月")) + (day == 0 ? "" : (day + "天"));
    }


}
