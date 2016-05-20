package cn.timeface.circle.baby.api.services;

import java.util.List;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.api.models.PhotoRecode;
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.responses.BabyInfoListResponse;
import cn.timeface.circle.baby.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryTextResponse;
import cn.timeface.circle.baby.api.models.responses.FamilyListResponse;
import cn.timeface.circle.baby.api.models.responses.InviteResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneIdResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneResponse;
import cn.timeface.circle.baby.api.models.responses.MsgListResponse;
import cn.timeface.circle.baby.api.models.responses.RegisterResponse;
import cn.timeface.circle.baby.api.models.responses.RelationIdResponse;
import cn.timeface.circle.baby.api.models.responses.RelationshipResponse;
import cn.timeface.circle.baby.api.models.responses.SystemMsgListResponse;
import cn.timeface.circle.baby.api.models.responses.UserLoginResponse;
import cn.timeface.circle.baby.fragments.DiaryTextFragment;
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

    String IMAGE_BASE_URL = "http://img1.timeface.cn/";


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

    //宝宝详情
    @GET("baby/queryBabyInfoDetail")
    Observable<BabyInfoResponse> queryBabyInfoDetail(@Query("babyId") int babyId);

    //获取关系列表
    @GET("baby/queryBabyFamilyTypeInfoList")
    Observable<RelationshipResponse> queryBabyFamilyTypeInfoList(@Query("key") String key);

    //获取亲友团列表
    @GET("baby/queryBabyFamilyLoginInfoList")
    Observable<FamilyListResponse> queryBabyFamilyLoginInfoList();

    //亲友团-邀请家人
    @GET("baby/inviteFamily")
    Observable<InviteResponse> inviteFamily(@Query("relationName") String relationName);

    //亲友团-删除亲友
    @GET("baby/delRelationship")
    Observable<BaseResponse> delRelationship(@Query("userId") String userId);

    //亲友团-修改亲友信息
    @GET("baby/updateFamilyRelationshipInfo")
    Observable<BaseResponse> updateFamilyRelationshipInfo(@Query("nickName") String nickName,
                                                          @Query("relationName") String relationName,
                                                          @Query("userId") String userId);

    //获取宝宝列表
    @GET("baby/queryBabyInfoList")
    Observable<BabyInfoListResponse> queryBabyInfoList();

    //获取消息列表
    @GET("baby/queryMsgList")
    Observable<MsgListResponse> queryMsgList();

    //获取系统消息列表
    @GET("baby/querySystemMsgList")
    Observable<SystemMsgListResponse> querySystemMsgList();

    //发布
//    @GET("babyTime/publish")
//    Observable<BaseResponse> publish(@Query("dataList") List<PhotoRecode>);

    //获取里程碑列表
    @GET("babyTime/queryMilestoneList")
    Observable<MilestoneResponse> queryMilestoneList(@Query("key") String key);

    //添加里程碑
    @GET("babyTime/addMilestone")
    Observable<MilestoneIdResponse> addMilestone(@Query("milestoneName") String milestoneName);

    //日记发布-获取日记文本列表
    @GET("babyTime/getTextList")
    Observable<DiaryTextResponse> getTextList();

    //日记发布-获取日记背景信纸列表
    @GET("babyTime/getPaperList")
    Observable<DiaryPaperResponse> getPaperList();
}
