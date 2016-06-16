package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import de.greenrobot.event.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.EventTabMainWake;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.login.LoginApi;
import cn.timeface.circle.baby.utils.login.OnLoginListener;
import cn.timeface.common.utils.ShareSdkUtil;
import cn.timeface.common.utils.encode.AES;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseAppCompatActivity implements IEventBus {
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.btn_sign_in)
    Button btnSignIn;
    @Bind(R.id.btn_wechat)
    ImageButton btnWechat;
    @Bind(R.id.btn_qq)
    ImageButton btnQq;
    @Bind(R.id.btn_sina)
    ImageButton btnSina;
    @Bind(R.id.toolbar)
    Toolbar toolBar;
    @Bind(R.id.tv_register)
    TextView tvRegister;
    @Bind(R.id.tv_forget)
    TextView tvForget;
    private MyHandler handler = new MyHandler(this);

    public static void open(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);

        ShareSDK.initSDK(this);

        String account = FastData.getAccount();
        String password = FastData.getPassword();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            login(account, password, 0);
        }

    }

    private ShareSdkUtil.LoginCallback loginCallback = new ShareSdkUtil.LoginCallback() {
        @Override
        public void callback(Platform plat) {
            Message msg = handler.obtainMessage();
            msg.what = 1;
            msg.obj = plat;
            handler.sendMessage(msg);
        }

        @Override
        public void loginCancel() {
            handler.sendEmptyMessage(3);
        }

        @Override
        public void loginErr() {
            Message msg = handler.obtainMessage();
            msg.what = 4;
            msg.obj = getString(R.string.sdk_login_error);
            handler.sendMessage(msg);
        }
    };

    @OnClick({R.id.btn_wechat, R.id.btn_qq, R.id.btn_sina, R.id.tv_register, R.id.tv_forget, R.id.btn_sign_in})
    public void clickBtn(View view) {
        Subscription s = null;
        switch (view.getId()) {
            case R.id.btn_wechat:
//                ShareSdkUtil.login(this, ShareSDK.getPlatform(this, Wechat.NAME), loginCallback);
                break;
            case R.id.btn_qq:
//                login(QQ.NAME);
                break;
            case R.id.btn_sina:
//                login(SinaWeibo.NAME);
                break;
            case R.id.tv_register:
                RegisterActivity.open(this);
                break;
            case R.id.tv_forget:
                ForgetPasswordActivity.open(this);
                break;
            case R.id.btn_sign_in:
                String account = etPhone.getText().toString().trim();
                String psw = new AES().encrypt(etPassword.getText().toString().trim().getBytes());
                s = login(account, psw, 0);
                break;
        }
        addSubscription(s);
    }


    private Subscription login(String account, String psw, int type) {
        Subscription s;
        s = apiService.login(account, psw, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userLoginResponse -> {
                    ToastUtil.showToast(userLoginResponse.getInfo());
                    if (userLoginResponse.success()) {
                        FastData.setUserFrom(TypeConstants.USER_FROM_LOCAL);
                        FastData.setAccount(account);
                        FastData.setPassword(psw);

                        Gson gson = new Gson();
                        FastData.putString("userObj", gson.toJson(userLoginResponse.getUserInfo()));
//                        String userObj = FastData.getString("userObj", "");
//                        UserObj o = gson.fromJson(userObj, UserObj.class);
                        if (userLoginResponse.getUserInfo().getBabyObj() == null || userLoginResponse.getUserInfo().getBabyObj().getBabyId() == 0) {
                            CreateBabyActivity.open(this);
                        } else {
                            FastData.setUserInfo(userLoginResponse.getUserInfo());
                            startActivity(new Intent(this, TabMainActivity.class));
                        }
//                     else if (userLoginResponse.getUserInfo().getNickName() == null || TextUtils.isEmpty(userLoginResponse.getUserInfo().getNickName())) {
//                        Remember.putInt("ensureRelation",0);
//                        startActivity(new Intent(this, TabMainActivity.class));
//                    }

                    }

                }, throwable -> {
                    Log.e(TAG, "login:", throwable);
                });
        return s;
    }


    private void login(String platformName) {
        LoginApi api = new LoginApi();
        //设置登陆的平台后执行登陆的方法
        api.setPlatform(platformName);
        api.setOnLoginListener(new OnLoginListener() {
            @Override
            public void onLogin(String platform, HashMap<String, Object> res) {
                int from = TypeConstants.USER_FROM_LOCAL;
                if (platform.equals(SinaWeibo.NAME)) {
                    from = TypeConstants.USER_FROM_SINA;
                } else if (platform.equals(QQ.NAME)) {
                    from = TypeConstants.USER_FROM_QQ;
                } else if (platform.equals(Wechat.NAME)) {
                    from = TypeConstants.USER_FROM_WECHAT;
                }
                Platform plat = ShareSDK.getPlatform(platform);
                final int finalFrom = from;
                thirdLogin(plat.getDb().getToken(),
                        plat.getDb().getUserIcon(),
                        plat.getDb().getExpiresIn(),
                        from,
                        "m".equals(plat.getDb().getUserGender()) ? 1 : 0,
                        plat.getDb().getUserName(),
                        "",
                        plat.getDb().getUserId(),
                        "", LoginActivity.this);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }
        });
        api.login(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
    }

    @Override
    public void onEvent(Object event) {

    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(EventTabMainWake event) {
        finish();
    }

    class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<LoginActivity> mOuter;

        public MyHandler(LoginActivity activity) {
            mOuter = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity outer = mOuter.get();
            if (outer != null) {
                // Do something with outer as your wish.
                if (msg.what == 1) {
                    Platform plat = (Platform) msg.obj;
                    String type = "1";
                    if (plat.getName().equals(QZone.NAME)) {
                        type = "2";
                    } else if (plat.getName().equals(SinaWeibo.NAME)) {
                        type = "1";
                    } else if (plat.getName().equals(Wechat.NAME)) {
                        type = "3";
                    }
                    thirdLogin(plat.getDb().getToken(),
                            plat.getDb().getUserIcon(),
                            plat.getDb().getExpiresIn(),
                            3,
                            "m".equals(plat.getDb().getUserGender()) ? 1 : 0,
                            plat.getDb().getUserName(),
                            plat.getDb().get("openid"),
                            plat.getDb().getUserId(),
                            plat.getDb().get("unionid"), outer);
                } else if (msg.what == 3) {
                    Toast.makeText(outer, "登录取消", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 4) {
                    Toast.makeText(outer, "登录发生了错误", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void thirdLogin(String accessToken,
                           String avatar,
                           long expiry_in,
                           int from,
                           int gender,
                           String nickName,
                           String openid,
                           String platId,
                           String unionid, Context context) {
        apiService.vendorLogin(accessToken, avatar, expiry_in, from, gender, Uri.encode(nickName), openid, platId, unionid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
//                            if (response.getUserInfo().getUserType() == 0) {
//                                FastData.setUserId(response.getUserInfo().getUserId());
//                                SelectUserTypeActivity.open(context, "");
//                                return;
//                            }
//                            EMChatUtil.regUser(response.getUserInfo().getUserId() + "",
//                                    "888888");
//                            FastData.setUserFrom(from);
//                            FastData.setUserToken(response.getToken());
//                            FastData.setUserInfo(response.getUserInfo());
//                            TabMainActivity.open(context);
                }, error -> {

                });
    }

}
