package cn.timeface.circle.baby.api.services;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.responses.BabyInfoListResponse;
import cn.timeface.circle.baby.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.api.models.responses.CardListResponse;
import cn.timeface.circle.baby.api.models.responses.CloudAlbumListResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryComposedResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryTextResponse;
import cn.timeface.circle.baby.api.models.responses.FamilyListResponse;
import cn.timeface.circle.baby.api.models.responses.InviteResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneListResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneTimeResponse;
import cn.timeface.circle.baby.api.models.responses.MsgListResponse;
import cn.timeface.circle.baby.api.models.responses.RegisterResponse;
import cn.timeface.circle.baby.api.models.responses.RelationIdResponse;
import cn.timeface.circle.baby.api.models.responses.RelationshipResponse;
import cn.timeface.circle.baby.api.models.responses.SystemMsgListResponse;
import cn.timeface.circle.baby.api.models.responses.TimeDetailResponse;
import cn.timeface.circle.baby.api.models.responses.TimelineResponse;
import cn.timeface.circle.baby.api.models.responses.UserLoginResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
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

    //退出登录
    @GET("auth/logout")
    Observable<BaseResponse> logout();

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
    @FormUrlEncoded
    @POST("auth/register")
    Observable<RegisterResponse> register(@Field("account") String account,
                                          @Field("nickname") String nickname,
                                          @Field("password") String password);

    //邀请码校验
    @GET("auth/verifiedInviteCode")
    Observable<BaseResponse> verifiedInviteCode(@Query("inviteCode") String inviteCode);

    //关注宝宝
    @GET("baby/babyAttention")
    Observable<UserLoginResponse> babyAttention(@Query("code") String code,
                                                @Query("relationName") String relationName);

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
    Observable<RelationshipResponse> queryBabyFamilyTypeInfoList();

    //查询关系列表
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

    //首页-宝宝里程碑
    @GET("babyTime/milestone")
    Observable<MilestoneTimeResponse> milestone();

    //亲友团-修改亲友信息
    @GET("baby/updateFamilyRelationshipInfo")
    Observable<BaseResponse> updateFamilyRelationshipInfo(@Query("nickName") String nickName,
                                                          @Query("relationName") String relationName,
                                                          @Query("userId") String userId);

    //获取宝宝列表
    @GET("baby/queryBabyInfoList")
    Observable<BabyInfoListResponse> queryBabyInfoList();

    //修改宝宝信息
    @GET("baby/editBabyInfo")
    Observable<BaseResponse> editBabyInfo(@Query("birthday") long birthday,
                                          @Query("blood") String blood,
                                          @Query("gender") int gender,
                                          @Query("name") String name,
                                          @Query("objectKey") String objectKey);

    //删除宝宝
    @GET("baby/delBabyInfo")
    Observable<BaseResponse> delBabyInfo(@Query("babyId") int babyId);

    //取消关注宝宝
    @GET("baby/attentionCancel")
    Observable<BaseResponse> attentionCancel(@Query("babyId") int babyId);

    //获取消息列表
    @GET("babyMsgInfo/queryMsgList")
    Observable<MsgListResponse> queryMsgList();

    //获取系统消息列表
    @GET("babyMsgInfo/querySystemMsgList")
    Observable<SystemMsgListResponse> querySystemMsgList();

    //标记消息为已读
    @GET("babyMsgInfo/read")
    Observable<BaseResponse> read(@Query("id") int id);

    //发布
    @GET("babyTime/publish")
    Observable<BaseResponse> publish(@Query("dataList") String dataList,
                                     @Query("type") int type);

    //获取里程碑列表
    @GET("babyTime/queryMilestoneList")
    Observable<MilestoneListResponse> queryMilestoneList();

    //添加里程碑
    @GET("babyTime/addMilestone")
    Observable<MilestoneResponse> addMilestone(@Query("milestoneName") String milestoneName);

    //日记发布-获取日记文本列表
    @GET("babyTime/getDiaryDefaultTextList")
    Observable<DiaryTextResponse> getDiaryDefaultTextList();

    //日记发布-获取日记背景信纸列表
    @GET("babyTime/getPaperList")
    Observable<DiaryPaperResponse> getPaperList();

    //日记发布-合成
    @GET("babyTime/diaryComposed")
    Observable<DiaryComposedResponse> diaryComposed(@Query("templateObj") String templateObj);

    //识图卡片-列表
    @GET("babyTime/getComposedCardList")
    Observable<CardListResponse> getComposedCardList();

    //识图卡片-删除
    @GET("babyTime/delCard")
    Observable<BaseResponse> delCard(@Query("id") int id);

    //识图卡片-合成
    @GET("babyTime/cardComposed")
    Observable<DiaryComposedResponse> cardComposed(@Query("content") String content,
                                                   @Query("imageInfo") String imageInfo,
                                                   @Query("pinyin") String pinyin);

    //首页-宝宝时光
    @GET("babyTime/timeline")
    Observable<TimelineResponse> timeline(@Query("currentPage") int currentPage,
                                          @Query("pageSize") int pageSize);

    //首页-时光详情
    @GET("babyTime/queryBabyTimeDetail")
    Observable<TimeDetailResponse> queryBabyTimeDetail(@Query("timeId") int timeId);

    //回复评论
    @GET("babyTime/comment")
    Observable<BaseResponse> comment(@Query("content") String content,
                                           @Query("date") long date,
                                           @Query("timeId") int timeId,
                                           @Query("commentId") int commentId);

    //评论时光
    @GET("babyTime/comment")
    Observable<BaseResponse> comment(@Query("content") String content,
                                     @Query("date") long date,
                                     @Query("timeId") int timeId);

    //删除时光
    @GET("babyTime/delTime")
    Observable<BaseResponse> delTime(@Query("timeId") int timeId);

    //点赞/取消点赞
    @GET("babyTime/like")
    Observable<BaseResponse> like(@Query("timeId") int timeId,
                                     @Query("type") int type);

    //云相册列表
    @GET("babyCloudAlbums/queryCloudAlbumList")
    Observable<CloudAlbumListResponse> queryCloudAlbumList();

    //我的-修改用户资料
    @GET("member/profile")
    Observable<UserLoginResponse> profile(@Query("nickName") String nickName,
                                          @Query("pic") String pic);

}
