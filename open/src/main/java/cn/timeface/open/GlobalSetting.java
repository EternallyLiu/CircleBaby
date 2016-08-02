package cn.timeface.open;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import cn.timeface.open.api.OpenApiFactory;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOUserObj;
import cn.timeface.open.api.models.response.Authorize;
import cn.timeface.open.constants.Constant;
import cn.timeface.open.managers.interfaces.IUploadServices;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author: rayboot  Created on 16/7/8.
 * email : sy0725work@gmail.com
 */
public class GlobalSetting {
    IUploadServices uploadServices;
    private static volatile GlobalSetting sInst = null;  // volatile

    public static GlobalSetting getInstance() {
        GlobalSetting inst = sInst;
        if (inst == null) {
            synchronized (GlobalSetting.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new GlobalSetting();
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    public void init(@NonNull String app_id,@NonNull String secret,@NonNull TFOUserObj user,@NonNull IUploadServices uploadServices) {
        this.setUploadServices(uploadServices);
        OpenApiFactory.getOpenApi()
                .getApiService()
                .authorize(app_id, secret, new Gson().toJson(user))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        new Action1<BaseResponse<Authorize>>() {
                            @Override
                            public void call(BaseResponse<Authorize> response) {
                                if (response.success()) {
                                    Constant.ACCESS_TOKEN = response.getData().getAccessToken();
                                    Constant.EXPIRES_IN = response.getData().getExpiresIn();
                                    Constant.UNIONID = response.getData().getUnionId();

                                    Log.d("Constant.UNIONID=======", Constant.UNIONID);
                                    Log.d("Constant.ACCESS_TOKEN", Constant.ACCESS_TOKEN);
                                }
                            }
                        }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        }
                );
    }

    public void setUploadServices(IUploadServices uploadServices) {
        this.uploadServices = uploadServices;
    }

    public IUploadServices getUploadServices() {
        return uploadServices;
    }
}
