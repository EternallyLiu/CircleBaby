package cn.timeface.circle.baby.support.mvp;

import cn.timeface.circle.baby.support.api.Api;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.services.ApiService;

/**
 * Created by JieGuo on 16/8/31.
 */

public abstract class BasePresenterModel {

    protected ApiService apiServiceV2;

    protected BasePresenterModel() {

        Api api = ApiFactory.getApi();
        apiServiceV2 = api.getApiService();
    }
}
