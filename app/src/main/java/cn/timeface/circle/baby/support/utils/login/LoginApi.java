package cn.timeface.circle.baby.support.utils.login;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import static com.mob.tools.utils.R.getStringRes;

public class LoginApi implements Callback {
    private static final int MSG_AUTH_CANCEL = 1;
    private static final int MSG_AUTH_ERROR = 2;
    private static final int MSG_AUTH_COMPLETE = 3;

    private OnLoginListener loginListener;
    private String platform;
    private Context context;
    private Handler handler;

    public LoginApi() {
        handler = new Handler(Looper.getMainLooper(), this);
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setOnLoginListener(OnLoginListener login) {
        this.loginListener = login;
    }

    public void login(Context context) {
        this.context = context.getApplicationContext();
        if (platform == null) {
            return;
        }

        //初始化SDK
        ShareSDK.initSDK(context);
        Platform plat = ShareSDK.getPlatform(platform);
        if (plat == null) {
            return;
        }

        if (plat.isAuthValid()) {
            plat.removeAccount(true);
            return;
        }

        //使用SSO授权，通过客户单授权
        plat.SSOSetting(false);//设置false表示使用SSO授权方式
        plat.setPlatformActionListener(new PlatformActionListener() {
            public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Message msg = new Message();
                    msg.what = MSG_AUTH_COMPLETE;
                    msg.arg2 = action;
                    msg.obj = new Object[]{plat.getName(), res};
                    handler.sendMessage(msg);
                }
            }

            public void onError(Platform plat, int action, Throwable t) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Message msg = new Message();
                    msg.what = MSG_AUTH_ERROR;
                    msg.arg2 = action;
                    msg.obj = t;
                    handler.sendMessage(msg);
                }
                t.printStackTrace();
            }

            public void onCancel(Platform plat, int action) {
                if (action == Platform.ACTION_USER_INFOR) {
                    Message msg = new Message();
                    msg.what = MSG_AUTH_CANCEL;
                    msg.arg2 = action;
                    msg.obj = plat;
                    handler.sendMessage(msg);
                }
            }
        });
        plat.showUser(null);
    }

    /**
     * 处理操作结果
     */
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH_CANCEL: {
                // 取消
//                Toast.makeText(context, "canceled", Toast.LENGTH_SHORT).show();
                if (loginListener != null) {
                    loginListener.onCancel();
                }
            }
            break;
            case MSG_AUTH_ERROR: {
                // 失败
                String expName = msg.obj.getClass().getSimpleName();
                if ("WechatClientNotExistException".equals(expName)
                        || "WechatTimelineNotSupportedException".equals(expName)
                        || "WechatFavoriteNotSupportedException".equals(expName)) {
                    int resId = getStringRes(context, "wechat_client_inavailable");
                    if (resId > 0) {
                        showNotification(context.getString(resId));
                    }
                } else if ("GooglePlusClientNotExistException".equals(expName)) {
                    int resId = getStringRes(context, "google_plus_client_inavailable");
                    if (resId > 0) {
                        showNotification(context.getString(resId));
                    }
                }
                /*Throwable t = (Throwable) msg.obj;
                String text = "caught error: " + t.getMessage();
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                t.printStackTrace();*/
                if (loginListener != null) {
                    loginListener.onError();
                }
            }
            break;
            case MSG_AUTH_COMPLETE: {
                // 成功
                Object[] objs = (Object[]) msg.obj;
                String plat = (String) objs[0];
                HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                if (loginListener != null) {
                    loginListener.onLogin(plat, res);
                }
            }
            break;
        }
        return false;
    }

    private void showNotification(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


}
