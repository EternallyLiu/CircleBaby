package cn.timeface.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yusen on 2014/11/17.
 */
public class StringUtil {
    /**
     * 计算字符串长度，字母长度为1，汉字日韩文为2
     */
    public static int strLength(String str) {
        if (str == null) {
            return 0;
        }
        char[] c = str.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }


    /**
     * 判断是否字母
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

}
