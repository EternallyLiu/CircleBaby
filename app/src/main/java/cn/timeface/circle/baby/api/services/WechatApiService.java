package cn.timeface.circle.baby.api.services;

import cn.timeface.circle.baby.api.models.responses.WxAccessTokenResponse;
import cn.timeface.circle.baby.api.models.responses.WxLoginInfoResponse;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * author: rayboot  Created on 15/12/23.
 * email : sy0725work@gmail.com
 */
public interface WechatApiService {
    /**
     * 跟地址
     */
    String BASE_URL = "https://api.weixin.qq.com/sns/";


    /**
     * wx登陆获取access token
     */
    @POST("oauth2/access_token")
    Observable<WxAccessTokenResponse> getAccessToken(@Query("appid") String appid,
                                                     @Query("secret") String secret,
                                                     @Query("code") String code,
                                                     @Query("grant_type") String grant_type);


    /**
     * wx获取用户信息
     */
    @POST("userinfo")
    Observable<WxLoginInfoResponse> getPersonInfo(@Query("access_token") String access_token,
                                                  @Query("openid") String openid);
}
