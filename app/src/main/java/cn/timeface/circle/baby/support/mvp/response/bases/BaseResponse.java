package cn.timeface.circle.baby.support.mvp.response.bases;

import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonObject;


/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014年8月20日上午9:36:09
 * @TODO 请求返回结果对象{“status”:1,”info”:”操作成功”}
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BaseResponse extends BaseModule {
    private static final long serialVersionUID = 1L;
    private static final int USER_FORBIDDEN = 1012;
    private static final int USER_NOSPEAK = 1011;

    /**
     * 操作是否成功
     */
    public int status;

    /**
     * 操作结果描述
     */
    public String info;

    /**
     * 错误编号
     */
    public String errorCode;

    public String dataId;

    public boolean success() {
        return status == 1;
    }

    public int getErrorCode() {
        return TextUtils.isEmpty(errorCode) ? 0 : Integer.valueOf(errorCode);
    }

    public boolean forbidden() {
        return USER_FORBIDDEN == getErrorCode();
    }

    public boolean noSpeak() {
        return USER_NOSPEAK == getErrorCode();
    }

}
