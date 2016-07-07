package cn.timeface.circle.baby.api;

import cn.timeface.open.api.OpenApi;
import cn.timeface.open.api.OpenApiFactory;

/**
 * author: rayboot  Created on 15/11/27.
 * email : sy0725work@gmail.com
 */
public class ApiFactory {
    static Api api = null;
    static OpenApi openApi = null;

    public static Api getApi() {
        if (api == null) {
            api = new Api();
        }
        return api;
    }

    public static OpenApi getOpenApi() {
        if (openApi == null) {
            openApi = OpenApiFactory.getOpenApi();
        }
        return openApi;
    }
}
