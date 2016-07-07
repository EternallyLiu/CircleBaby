package cn.timeface.open.api;

/**
 * author: rayboot  Created on 15/11/27.
 * email : sy0725work@gmail.com
 */
public class OpenApiFactory {
    static OpenApi openApi = null;

    public static OpenApi getOpenApi() {
        if (openApi == null) {
            openApi = new OpenApi();
        }
        return openApi;
    }
}
