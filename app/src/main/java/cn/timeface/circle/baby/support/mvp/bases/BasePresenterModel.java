package cn.timeface.circle.baby.support.mvp.bases;

import cn.timeface.circle.baby.support.api.Api;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.services.ApiService;

/**
 * Created by JieGuo on 16/8/31.
 */

public abstract class BasePresenterModel {

    protected ApiService apiService;

    protected BasePresenterModel() {

        Api api = ApiFactory.getApi();
        apiService = api.getApiService();
    }
}
