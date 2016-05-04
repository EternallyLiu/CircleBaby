package cn.timeface.circle.baby.utils.login;

import java.util.HashMap;

public interface OnLoginListener {

    void onLogin(String platform, HashMap<String, Object> res);

    void onError();

    void onCancel();


}
