package cn.timeface.open;

import com.google.gson.Gson;

import cn.timeface.open.api.OpenApiFactory;
import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.UserObj;
import cn.timeface.open.api.models.response.Authorize;
import cn.timeface.open.constants.Constant;
import rx.functions.Action1;

/**
 * author: rayboot  Created on 16/7/8.
 * email : sy0725work@gmail.com
 */
public class GlobalSetting {

    public static void init(String app_id, String secret, UserObj user) {
        OpenApiFactory.getOpenApi()
                .getApiService()
                .authorize(app_id, secret, new Gson().toJson(user))
                .subscribe(
                        new Action1<BaseResponse<Authorize>>() {
                            @Override
                            public void call(BaseResponse<Authorize> response) {
                                if (response.success()) {
                                    Constant.ACCESS_TOKEN = response.getData().getAccessToken();
                                    Constant.EXPIRES_IN = response.getData().getExpiresIn();
                                    Constant.UNIONID = response.getData().getUnionId();
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
}
