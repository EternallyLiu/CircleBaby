package cn.timeface.open.activities;

import android.os.Bundle;

import com.google.gson.Gson;

import cn.timeface.open.R;
import cn.timeface.open.activities.base.BaseAppCompatActivity;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.UserObj;
import cn.timeface.open.api.models.response.Authorize;
import cn.timeface.open.constants.Constant;
import cn.timeface.open.utils.rxutils.SchedulersCompat;
import rx.functions.Action1;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends BaseAppCompatActivity {

    final String APP_ID = "123456789";
    final String APP_SECRET = "987654321";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        apiService.authorize(APP_ID, APP_SECRET, new Gson().toJson(UserObj.genUserObj()))
                .compose(SchedulersCompat.<BaseResponse<Authorize>>applyIoSchedulers())
                .subscribe(new Action1<BaseResponse<Authorize>>() {
                               @Override
                               public void call(BaseResponse<Authorize> response) {
                                   if (response.success()) {
                                       Constant.ACCESS_TOKEN=response.getData().getAccessToken();
                                       Constant.EXPIRES_IN=response.getData().getExpiresIn();
                                       Constant.UNIONID=response.getData().getUnionId();
                                       PODActivity.open(SplashActivity.this);
                                   }
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        });
    }

}
