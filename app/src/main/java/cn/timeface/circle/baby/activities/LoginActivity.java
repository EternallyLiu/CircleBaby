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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.CountlyEventConstant;
import cn.timeface.circle.baby.constants.CountlyEventHelper;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.EventTabMainWake;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.DeviceUuidFactory;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.NetUtils;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.login.LoginApi;
import cn.timeface.circle.baby.support.utils.login.OnLoginListener;
import cn.timeface.circle.baby.ui.settings.fragments.BindPhoneFragment;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.ShareSdkUtil;
import cn.timeface.common.utils.encode.AES;
import ly.count.android.sdk.Countly;
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
    private TFProgressDialog tfProgressDialog;

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
        Remember.putBoolean("showtimelinehead", true);
        tfProgressDialog = TFProgressDialog.getInstance("");

        /*if (FastData.getUserFrom() == TypeConstants.USER_FROM_LOCAL) {
            //上次登录为手机号登录
            String account = FastData.getAccount();
            String password = FastData.getPassword();
            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
                login(account, password, 0);
            }
        } else if (FastData.getUserFrom() == -1) {

        } else {
            //上次登录为三方账号登录
            String platform = Remember.getString("platform", "");
            if (!TextUtils.isEmpty(platform)) {
                Platform plat = ShareSDK.getPlatform(platform);
                thirdLogin(plat.getDb().getToken(),
                        plat.getDb().getUserIcon(),
                        plat.getDb().getExpiresIn(),
                        FastData.getUserFrom(),
                        "m".equals(plat.getDb().getUserGender()) ? 1 : 0,
                        plat.getDb().getUserName(),
                        plat.getDb().get("openid"),
                        plat.getDb().getUserId(),
                        plat.getDb().get("unionid"), LoginActivity.this);
            }
        }*/
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
                login(Wechat.NAME);
                break;
            case R.id.btn_qq:
                login(QQ.NAME);
                break;
            case R.id.btn_sina:
                login(SinaWeibo.NAME);
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
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(psw)) {
                    Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                s = login(account, psw, 0);
                break;
        }
        addSubscription(s);
    }


    private Subscription login(String account, String psw, int type) {
        tfProgressDialog.setTvMessage("登录中…");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        Subscription s;
        s = apiService.login(Uri.encode(account), Uri.encode(psw), type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(loginResponse -> {
            tfProgressDialog.dismiss();
            ToastUtil.showToast(loginResponse.getInfo());
            if (loginResponse.success()) {
                FastData.setUserInfo(loginResponse.getUserInfo());
                FastData.setUserFrom(TypeConstants.USER_FROM_LOCAL);
                FastData.setAccount(account);
                FastData.setPassword(psw);
                FastData.setBabyCount(loginResponse.getBabycount());
                if (TextUtils.isEmpty(loginResponse.getUserInfo().getPhoneNumber())) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    FragmentBridgeActivity.open(this, BindPhoneFragment.class.getSimpleName(), bundle);
                } else if (loginResponse.getBabycount() == 0) {
                    CreateBabyActivity.open(this, true);
                } else {
                    startActivity(new Intent(this, TabMainActivity.class));
                }
                //统计登录行为事件
                CountlyEventHelper.getInstance().loginEvent(loginResponse.getUserInfo().getUserId(), CountlyEventConstant.LOGIN_EVENT_FROM_TIME_FACE);
            }

        }, throwable -> {
            tfProgressDialog.dismiss();
            Log.e(TAG, "login:", throwable);
            throwable.printStackTrace();
            ToastUtil.showToast("服务器异常，请稍后重试");
        });
        return s;
    }


    private void login(String platformName) {
        FastData.setBabyId(0);
        LoginApi api = new LoginApi();
        //设置登陆的平台后执行登陆的方法
        api.setPlatform(platformName);
        api.setOnLoginListener(new OnLoginListener() {
            @Override
            public void onLogin(String platform, HashMap<String, Object> res) {
                Remember.putString("platform", platform);
                int from = TypeConstants.USER_FROM_LOCAL;
                if (platform.equals(SinaWeibo.NAME)) {
                    from = TypeConstants.USER_FROM_SINA;
                } else if (platform.equals(QQ.NAME)) {
                    from = TypeConstants.USER_FROM_QQ;
                } else if (platform.equals(Wechat.NAME)) {
                    from = TypeConstants.USER_FROM_WECHAT;
                }
                FastData.setUserFrom(from);
                Platform plat = ShareSDK.getPlatform(platform);
                final int finalFrom = from;
                thirdLogin(plat.getDb().getToken(), plat.getDb().getUserIcon(), plat.getDb().getExpiresIn(), from, "m".equals(plat.getDb().getUserGender()) ? 1 : 0, plat.getDb().getUserName(), plat.getDb().get("openid"), plat.getDb().getUserId(), plat.getDb().get("unionid"), LoginActivity.this);
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
                    int type = 1;
                    String avatar = plat.getDb().getUserIcon();
                    if (plat.getName().equals(SinaWeibo.NAME)) {
                        type = 1;
                    } else if (plat.getName().equals(QQ.NAME)) {
                        type = 2;
                        avatar = plat.getDb().get("figureurl_qq_2");
                    } else if (plat.getName().equals(Wechat.NAME)) {
                        type = 3;
                    }
                    thirdLogin(plat.getDb().getToken(), avatar, plat.getDb().getExpiresIn(), type, "m".equals(plat.getDb().getUserGender()) ? 1 : 0, plat.getDb().getUserName(), plat.getDb().get("openid"), plat.getDb().getUserId(), plat.getDb().get("unionid"), outer);
                } else if (msg.what == 3) {
                    Toast.makeText(outer, "登录取消", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 4) {
                    Toast.makeText(outer, "登录发生了错误", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void thirdLogin(String accessToken, String avatar, long expiry_in, int from, int gender, String nickName, String openid, String platId, String unionid, Context context) {
        tfProgressDialog.setTvMessage("登录中…");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        apiService.vendorLogin(accessToken, avatar, expiry_in, from, gender, Uri.encode(nickName), openid, platId, unionid).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(loginResponse -> {
            tfProgressDialog.dismiss();
            if (loginResponse.success()) {
                FastData.setUserInfo(loginResponse.getUserInfo());
                FastData.setUserFrom(from);
                FastData.setBabyCount(loginResponse.getBabycount());
                if (TextUtils.isEmpty(loginResponse.getUserInfo().getPhoneNumber())) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    FragmentBridgeActivity.open(this, BindPhoneFragment.class.getSimpleName(), bundle);
                } else {
                    if (loginResponse.getBabycount() == 0) {
                        CreateBabyActivity.open(this, true);
                    } else {
                        startActivity(new Intent(this, TabMainActivity.class));
                    }
                }
                switch (from) {
                    case TypeConstants.USER_FROM_LOCAL:
                        CountlyEventHelper.getInstance().loginEvent(loginResponse.getUserInfo().getUserId(), CountlyEventConstant.LOGIN_EVENT_FROM_TIME_FACE);
                        break;

                    case TypeConstants.USER_FROM_QQ:
                        CountlyEventHelper.getInstance().loginEvent(loginResponse.getUserInfo().getUserId(), CountlyEventConstant.LOGIN_EVENT_FROM_QQ);
                        break;

                    case TypeConstants.USER_FROM_SINA:
                        CountlyEventHelper.getInstance().loginEvent(loginResponse.getUserInfo().getUserId(), CountlyEventConstant.LOGIN_EVENT_FROM_WEIBO);
                        break;

                    case TypeConstants.USER_FROM_WECHAT:
                        CountlyEventHelper.getInstance().loginEvent(loginResponse.getUserInfo().getUserId(), CountlyEventConstant.LOGIN_EVENT_FROM_WECHAT);
                        break;
                }
            }
        }, error -> {
            tfProgressDialog.dismiss();
            Log.e(TAG, "vendorLogin:", error);
            error.printStackTrace();
        });
    }

}
