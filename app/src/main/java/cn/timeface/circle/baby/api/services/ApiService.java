package cn.timeface.circle.baby.api.services;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.responses.RegisterResponse;
import cn.timeface.circle.baby.api.models.responses.RelationIdResponse;
import cn.timeface.circle.baby.api.models.responses.RelationshipResponse;
import cn.timeface.circle.baby.api.models.responses.UserLoginResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public interface ApiService {

    /**
     * 根地址
     */
    String BASE_URL = BuildConfig.API_URL;


    //登录
    @GET("auth/login")
    Observable<UserLoginResponse> login(@Query("account") String account,
                                            @Query("password") String password,
                                            @Query("type") int type);
    //三方登录
    @GET("auth/thirdPartyLogin")
    Observable<UserLoginResponse> vendorLogin(@Query("accessToken") String accessToken,
                                              @Query("avatar") String avatar,
                                              @Query("expiry_in") long expiry_in,
                                              @Query("from") int from,
                                              @Query("gender") int gender,
                                              @Query("nickname") String nickName,
                                              @Query("openid") String openid,
                                              @Query("platId") String platId,
                                              @Query("unionid") String unionid);
    //获取验证码
    @GET("auth/getVeriCode")
    Observable<BaseResponse> getVeriCode(@Query("account") String account);

    //验证码校验
    @GET("auth/verifyCode")
    Observable<BaseResponse> verifyCode(@Query("account") String account,
                                        @Query("code") String code);

    //注册
    @GET("auth/register")
    Observable<RegisterResponse> register(@Query("account") String account,
                                          @Query("nickname") String nickname,
                                          @Query("password") String password);

    //邀请码校验
    @GET("auth/verifiedInviteCode")
    Observable<BaseResponse> verifiedInviteCode(@Query("inviteCode") String inviteCode);

    //关注宝宝
    @GET("baby/babyAttention")
    Observable<UserLoginResponse> babyAttention(@Query("code") String code);

    //创建宝宝
    @GET("baby/createBaby")
    Observable<UserLoginResponse> createBaby(@Query("birthday") long birthday,
                                             @Query("gender") int gender,
                                             @Query("imgUrl") String imgUrl,
                                             @Query("name") String name,
                                             @Query("relationId") int relationId);

    //添加宝宝关系
    @GET("baby/addRelationship")
    Observable<RelationIdResponse> addRelationship(@Query("name") String name);

    //获取关系列表
    @GET("baby/queryBabyFamilyTypeInfoList")
    Observable<RelationshipResponse> queryBabyFamilyTypeInfoList(@Query("key") String key);
}
