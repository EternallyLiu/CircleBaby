package cn.timeface.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014-3-18上午11:31:45
 * @TODO 相关输入格式验证工具类
 */
public class CheckedUtil {
    /**
     * 检查是否为合法手机号
     *
     * @return false:不是手机号
     */
    public static boolean isMobileNum(String num) {
        String numberPatt = "^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[^4,\\D]))\\d{8}$";
        Pattern p = Pattern.compile(numberPatt);
        Matcher m = p.matcher(num);
        return m.matches();
    }

    /**
     * 检查是否为合法email地址
     *
     * @return false:不是email地址
     */
    public static boolean isEmail(String email) {
        String emailPatt =
                "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(emailPatt);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    //判断是否为数字
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    //判断是否为字母
    public static boolean isAlphabet(char c) {
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    //判断字符是否为汉字
    public static boolean isChineseLetter(char str) {
        return Character.isLetter(str);
    }
}
