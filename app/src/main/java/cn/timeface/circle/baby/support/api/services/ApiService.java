package cn.timeface.circle.baby.support.api.services;

import java.util.Map;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.responses.ADResponse;
import cn.timeface.circle.baby.support.api.models.responses.AddAddressResponse;
import cn.timeface.circle.baby.support.api.models.responses.AddCartItemResponse;
import cn.timeface.circle.baby.support.api.models.responses.AddressListResponse;
import cn.timeface.circle.baby.support.api.models.responses.AlbumDetailResponse;
import cn.timeface.circle.baby.support.api.models.responses.AliPayResponse;
import cn.timeface.circle.baby.support.api.models.responses.BabyInfoListResponse;
import cn.timeface.circle.baby.support.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.support.api.models.responses.BookListResponse;
import cn.timeface.circle.baby.support.api.models.responses.BookTypeListResponse;
import cn.timeface.circle.baby.support.api.models.responses.CardBookSizeResponse;
import cn.timeface.circle.baby.support.api.models.responses.CardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.CloudAlbumDownloadImageResponse;
import cn.timeface.circle.baby.support.api.models.responses.CloudAlbumListResponse;
import cn.timeface.circle.baby.support.api.models.responses.CreateBookResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryComposedResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryTextResponse;
import cn.timeface.circle.baby.support.api.models.responses.DistrictListResponse;
import cn.timeface.circle.baby.support.api.models.responses.EditBookResponse;
import cn.timeface.circle.baby.support.api.models.responses.FamilyListResponse;
import cn.timeface.circle.baby.support.api.models.responses.GetThemeResponse;
import cn.timeface.circle.baby.support.api.models.responses.GroupPhotoByLocationResponse;
import cn.timeface.circle.baby.support.api.models.responses.ImageExInfoResponse;
import cn.timeface.circle.baby.support.api.models.responses.ImageInfoListResponse;
import cn.timeface.circle.baby.support.api.models.responses.InviteResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeComposedResponse;
import cn.timeface.circle.baby.support.api.models.responses.LessResponse;
import cn.timeface.circle.baby.support.api.models.responses.LocationInfoResponse;
import cn.timeface.circle.baby.support.api.models.responses.LoginResponse;
import cn.timeface.circle.baby.support.api.models.responses.MilestoneInfoResponse;
import cn.timeface.circle.baby.support.api.models.responses.MilestoneListResponse;
import cn.timeface.circle.baby.support.api.models.responses.MilestoneResponse;
import cn.timeface.circle.baby.support.api.models.responses.MilestoneTimeResponse;
import cn.timeface.circle.baby.support.api.models.responses.MineBookListResponse;
import cn.timeface.circle.baby.support.api.models.responses.MineResponse;
import cn.timeface.circle.baby.support.api.models.responses.MsgListResponse;
import cn.timeface.circle.baby.support.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.circle.baby.support.api.models.responses.MyOrderListResponse;
import cn.timeface.circle.baby.support.api.models.responses.ParamListResponse;
import cn.timeface.circle.baby.support.api.models.responses.PrintCartListResponse;
import cn.timeface.circle.baby.support.api.models.responses.PrintDispatchListResponse;
import cn.timeface.circle.baby.support.api.models.responses.PrintStatusResponse;
import cn.timeface.circle.baby.support.api.models.responses.ProductionIntroListResponse;
import cn.timeface.circle.baby.support.api.models.responses.QueryPhotoByLocationResponse;
import cn.timeface.circle.baby.support.api.models.responses.QueryPhotoResponse;
import cn.timeface.circle.baby.support.api.models.responses.QueryProductionExtraResponse;
import cn.timeface.circle.baby.support.api.models.responses.QuerySelectedPhotoResponse;
import cn.timeface.circle.baby.support.api.models.responses.QueryTimeLineResponse;
import cn.timeface.circle.baby.support.api.models.responses.RegisterResponse;
import cn.timeface.circle.baby.support.api.models.responses.RelationIdResponse;
import cn.timeface.circle.baby.support.api.models.responses.RelationshipResponse;
import cn.timeface.circle.baby.support.api.models.responses.SystemMsgListResponse;
import cn.timeface.circle.baby.support.api.models.responses.TimeDetailResponse;
import cn.timeface.circle.baby.support.api.models.responses.TimelineResponse;
import cn.timeface.circle.baby.support.api.models.responses.UnReadMsgResponse;
import cn.timeface.circle.baby.support.api.models.responses.UpdateResponse;
import cn.timeface.circle.baby.support.api.models.responses.UserLoginResponse;
import cn.timeface.circle.baby.support.api.models.responses.UsersInfoResponse;
import cn.timeface.circle.baby.support.mvp.model.GeneralBookItemResponse;
import cn.timeface.circle.baby.support.mvp.model.GeneralBookResponse;
import cn.timeface.circle.baby.support.payment.timeface.WxPrepayResponse;
import cn.timeface.circle.baby.ui.babyInfo.beans.IconHisResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.bean.GetCircleAllBabyObj;
import cn.timeface.circle.baby.ui.circle.bean.GetCirclePhotoByUserObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleBabyObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleUserObj;
import cn.timeface.circle.baby.ui.circle.bean.TeacherAuthObj;
import cn.timeface.circle.baby.ui.circle.groupmembers.responses.MediasResponse;
import cn.timeface.circle.baby.ui.circle.groupmembers.responses.MemberListResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleCommentResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleCreateResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleDetailResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleIndexInfoResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleIndexResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleListResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleMemberListResponse;
import cn.timeface.circle.baby.ui.circle.response.CirclePublishResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleTimeLinesResponse;
import cn.timeface.circle.baby.ui.circle.response.CircleSearchListResponse;
import cn.timeface.circle.baby.ui.circle.response.HomeWorkDetailResponse;
import cn.timeface.circle.baby.ui.circle.response.HomeWorkListResponse;
import cn.timeface.circle.baby.ui.circle.response.HomeWorkPublishResponse;
import cn.timeface.circle.baby.ui.circle.response.HomeWorkSubmitResponse;
import cn.timeface.circle.baby.ui.circle.response.QrcodeResponse;
import cn.timeface.circle.baby.ui.circle.response.QueryCircleByTimeResponse;
import cn.timeface.circle.baby.ui.circle.response.QueryCircleLastActivityResponse;
import cn.timeface.circle.baby.ui.circle.response.QueryCirclePhotoResponse;
import cn.timeface.circle.baby.ui.circle.response.TeacherAuthIsHasResponse;
import cn.timeface.circle.baby.ui.circle.response.UpdateTimeLineResponse;
import cn.timeface.circle.baby.ui.circle.timelines.responses.ActiveSelectListResponse;
import cn.timeface.circle.baby.ui.circle.timelines.responses.CircleTimeLineDetailResponse;
import cn.timeface.circle.baby.ui.circle.timelines.responses.CreateActiveResponse;
import cn.timeface.circle.baby.ui.circle.timelines.responses.TimeLineSendResponse;
import cn.timeface.circle.baby.ui.growth.responses.PrintGrowthHomeResponse;
import cn.timeface.circle.baby.ui.images.beans.AddTagResponse;
import cn.timeface.circle.baby.ui.images.beans.LikeResponse;
import cn.timeface.circle.baby.ui.images.beans.TipResponse;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocalResponse;
import cn.timeface.circle.baby.ui.timelines.beans.QueryLocationInfoResponse;
import cn.timeface.circle.baby.ui.timelines.beans.SendTimeLineResponse;
import cn.timeface.circle.baby.ui.timelines.beans.TimeAxixResponse;
import cn.timeface.circle.baby.ui.timelines.beans.TimeOfPageResponse;
import cn.timeface.circle.baby.ui.timelines.fragments.MediaIdResponse;
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

    /**
     * 支付宝通知URL
     */
    String ZFB_NOTIFY = BASE_URL + "pod/zfbNotify";

    String IMAGE_BASE_URL = "http://img1.timeface.cn/";

    /**
     * 手机基本信息注册
     */
    @POST("auth/firstRun?platform=2")
    Observable<BaseResponse> firstRun(@Query("deviceName") String deviceName,
                                      @Query("osVersion") String osVersion,
                                      @Query("clientVersion") String clientVersion,
                                      @Query("screen") String screen);

    /**
     * App更新检测
     */
    @POST("auth/latestVersion")
    Observable<UpdateResponse> latestVersion(@Query("version") int version,
                                             @Query("platform") int platform);

    /**
     * 首页全屏广告
     */
    @POST("auth/screenAd")
    Observable<ADResponse> getAD(@Query("screen") String screen);

    //登录
    @GET("auth/login")
    Observable<LoginResponse> login(@Query("account") String account,
                                    @Query("password") String password,
                                    @Query("type") int type);

    //退出登录
    @GET("auth/logout")
    Observable<BaseResponse> logout();

    //三方登录
    @GET("auth/thirdPartyLogin")
    Observable<LoginResponse> vendorLogin(@Query("accessToken") String accessToken,
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
    Observable<BaseResponse> getVeriCode(@Query("account") String account,
                                         @Query("type") int type);

    //验证码校验
    @GET("auth/verifyCode")
    Observable<BaseResponse> verifyCode(@Query("account") String account,
                                        @Query("code") String code,
                                        @Query("type") int type);

    //注册
    @FormUrlEncoded
    @POST("auth/register")
    Observable<RegisterResponse> register(@Field("account") String account,
                                          @Field("nickname") String nickname,
                                          @Field("password") String password);

    //邀请码校验
    @GET("auth/verifiedInviteCode")
    Observable<UserLoginResponse> verifiedInviteCode(@Query("inviteCode") String inviteCode);

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

    //首页-里程碑详情
    @GET("babyTime/milestoneInfo")
    Observable<MilestoneInfoResponse> milestoneInfo(@Query("milestoneId") int milestoneId);

    //首页-里程碑已读
    @GET("babyTime/milestoneRead")
    Observable<BaseResponse> milestoneRead(@Query("milestoneId") int milestoneId);

    //里程碑删除
    @GET("babyTime/delMilestone")
    Observable<BaseResponse> delMilestone(@Query("id") int id);

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

    @GET("baby/editBabyInfo")
    Observable<BaseResponse> editBabyInfo(@Query("birthday") long birthday,
                                          @Query("blood") String blood,
                                          @Query("gender") int gender,
                                          @Query("name") String name,
                                          @Query("realName") String realName,
                                          @Query("showRealName") String showRealName,
                                          @Query("objectKey") String objectKey);

    //删除宝宝
    @GET("baby/delBabyInfo")
    Observable<BaseResponse> delBabyInfo(@Query("babyId") int babyId);

    //取消关注宝宝
    @GET("baby/attentionCancel")
    Observable<BaseResponse> attentionCancel(@Query("babyId") int babyId);

    //更新访问记录
    @GET("baby/updateLoginInfo")
    Observable<BaseResponse> updateLoginInfo();

    //获取消息列表
    @GET("babyMsgInfo/queryMsgList")
    Observable<MsgListResponse> queryMsgList();

    //获取未读消息
    @GET("babyMsgInfo/noReadMsg")
    Observable<UnReadMsgResponse> noReadMsg();

    //获取系统消息列表
    @GET("babyMsgInfo/querySystemMsgList")
    Observable<SystemMsgListResponse> querySystemMsgList();

    //消息删除
    @GET("babyMsgInfo/delMsg")
    Observable<BaseResponse> delMsg(@Query("id") int id,
                                    @Query("type") int type);

    //标记消息为已读
    @GET("babyMsgInfo/read")
    Observable<BaseResponse> read(@Query("id") int id,
                                  @Query("type") int type);

    //发布
    @FormUrlEncoded
    @POST("babyTime/publish")
    Observable<SendTimeLineResponse> publish(@Field("dataList") String dataList,
                                             @Field("type") int type);

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
    Observable<CardListResponse> getComposedCardList(@Query("babyId") int babyId);

    //识图卡片-删除
    @GET("babyTime/delCard")
    Observable<BaseResponse> delCard(@Query("cardIds") String cardIds);

    //识图卡片-合成
    @GET("babyTime/cardComposed")
    Observable<KnowledgeComposedResponse> cardComposed(@Query("cardId") String cardId,
                                                       @Query("content") String content,
                                                       @Query("imageInfo") String imageInfo,
                                                       @Query("pinyin") String pinyin);

    //首页-宝宝时光
    @GET("babyTime/timeline")
    Observable<TimelineResponse> timeline(@Query("currentPage") int currentPage,
                                          @Query("pageSize") int pageSize);

    @GET("babyTime/timeline")
    Observable<TimelineResponse> timeline(
            @Query("month") int month,
            @Query("year") int year,
            @Query("currentPage") int currentPage,
            @Query("pageSize") int pageSize);

    //首页-时光详情
    @GET("babyTime/queryBabyTimeDetail")
    Observable<TimeDetailResponse> queryBabyTimeDetail(@Query("timeId") int timeId);

    //编辑时光
    @FormUrlEncoded
    @POST("babyTime/editTime")
    Observable<BaseResponse> editTime(@Field("locationInfo") String locationInfo,
                                      @Field("content") String content,
                                      @Field("mediaList") String mediaList,
                                      @Field("milestone") int milestone,
                                      @Field("time") long time,
                                      @Field("timeId") int timeId);

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

    //删除评论
    @GET("babyTime/delComment")
    Observable<BaseResponse> delComment(@Query("commentId") int commentId);

    //删除时光
    @GET("babyTime/delTime")
    Observable<BaseResponse> delTime(@Query("timeId") int timeId);

    /**
     * 删除时光里面的照片
     *
     * @param mediaId
     * @param timeId
     * @return
     */
    @POST("babyTime/delTimeOfMedia")
    Observable<BaseResponse> delTimeOfMedia(@Query("mediaId") int mediaId,
                                            @Query("timeId") int timeId);

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

    //云相册详情
    @GET("babyCloudAlbums/queryCloudAlbumDetail")
    Observable<AlbumDetailResponse> queryCloudAlbumDetail(@Query("cloudAlbumId") String cloudAlbumId);

    //我的作品-作品类型列表
    @GET("babyBook/getBabyBookWorksTypeList")
    Observable<BookTypeListResponse> getBabyBookWorksTypeList();

    //云相册编辑
    @FormUrlEncoded
    @POST("babyCloudAlbums/editCloudAlbum")
    Observable<BaseResponse> editCloudAlbum(@Field("cloudAlbumId") String cloudAlbumId,
                                            @Field("dataList") String dataList);

    //删除云相册
    @GET("babyCloudAlbums/deleteCloudAlbum")
    Observable<BaseResponse> deleteCloudAlbum(@Query("cloudAlbumId") String cloudAlbumId);

    //删除相册照片
    @GET("babyCloudAlbums/deleteSingleImage")
    Observable<BaseResponse> deleteSingleImage(@Query("mediaId") String cloudAlbumId);

    //识图卡片下载 platform:1 iOS 2 Android
    @GET("babyCloudAlbums/imageUpload?platform=2")
    Observable<CloudAlbumDownloadImageResponse> downloadCloudAlbumImage(@Query("mediaId") int mediaId);

    //获取收货地址列表
    @GET("babyOrder/getAddressList")
    Observable<AddressListResponse> getAddressList();

    //添加收货地址
    @GET("babyOrder/addAddress")
    Observable<AddAddressResponse> addAddress(@Query("address") String address,
                                              @Query("area") String area,
                                              @Query("city") String city,
                                              @Query("contacts") String contacts,
                                              @Query("contactsPhone") String contactsPhone,
                                              @Query("id") String id,
                                              @Query("prov") String prov);

    /**
     * 添加/修改收货地址
     */
    @POST("babyOrder/addAddress")
    Observable<AddAddressResponse> changeAdd(@QueryMap Map<String, String> params);

    //获取收货地址列表
    @GET("babyOrder/delAddress")
    Observable<BaseResponse> delAddress(@Query("id") String id);

    //获取区划基础数据
    @GET("babyOrder/locationList?isGzip=0")
    Observable<DistrictListResponse> getLocationList();

    //我的订单列表
    @GET("babyOrder/queryOrderList")
    Observable<MyOrderListResponse> queryOrderList(@Query("pageSize") String pageSize,
                                                   @Query("currentPage") String currentPage);

    //获取订单配置参数
    @GET("babyOrder/queryParamList")
    Observable<BaseResponse> queryParamList(@Query("bookId") String bookId,
                                            @Query("bookType") String bookType);

    /**
     * 确认订单（订单详情）信息查询
     */
    @POST("babyOrder/findOrderMDetail")
    Observable<MyOrderConfirmListResponse> findOrderDetail(@Query("orderId") String orderId);

    //加入印刷车
    @POST("babyOrder/addCartitem")
    Observable<AddCartItemResponse> addCartItem(@QueryMap Map<String, String> params);

    /**
     * 确认订单
     */
    @POST("babyOrder/addOrder")
    Observable<LessResponse> addOrderNew(@Query("orderId") String orderId,
                                         @Query("addressId") String addressId,
                                         @Query("dispatch") String dispatch,
                                         @Query("pointsExchange") String pointsExchange,
                                         @Query("couponId") String couponId,
                                         @Query("personType") String personType,
                                         @Query("couponType") String couponType,
                                         @Query("from") String from);

    /**
     * 提交订单
     */
    @POST("babyOrder/addOrder")
    Observable<LessResponse> addOrderNew(@Query("dataList") String dataList);

    /**
     * 提交订单
     */
    @FormUrlEncoded
    @POST("babyOrder/addOrder")
    Observable<LessResponse> addOrder(@Field("dataList") String dataList,
                                      @Field("appId") String appId);

    /**
     * 提交订单-新增订单
     */
    @POST("babyOrder/addOrder")
    Observable<LessResponse> addOrder(@Query("addressId") int addressId,
                                      @Query("dataList") String dataList,
                                      @Query("expressId") int expressId,
                                      @Query("orderId") String orderId,
                                      @Query("appId") String appId);


    /**
     * 印刷车列表
     */
    @POST("babyOrder/cartlist")
    Observable<PrintCartListResponse> getCartList(@Query("pageSize") String pageSize,
                                                  @Query("currentPage") String currentPage);

    /**
     * 时光书单价查询
     */
    @POST("babyOrder/queryBookPrice")
    Observable<LessResponse> queryBookPrice(@QueryMap Map<String, String> params);

    /**
     * 时光书单价查询
     */
    @POST("babyOrder/queryBookPrice")
    Observable<LessResponse> queryBookPrice(@Query("bookType") int bookType,
                                            @Query("color") int color,
                                            @Query("pack") int pack,
                                            @Query("pageNum") int pageNum,
                                            @Query("paper") int paper,
                                            @Query("size") int size);


    /**
     * 获取当前作品印刷状态
     */
    @POST("babyOrder/printStatus")
    Observable<PrintStatusResponse> printStatus(@Query("bookType") int bookType,
                                                @Query("pageNum") int pageNum,
                                                @Query("bookSizeId") int bookSizeId);

    /**
     * 获取订单配置参数
     */
    @POST("babyOrder/queryParamList")
    Observable<ParamListResponse> queryParamList(@Query("bookType") int bookType,
                                                 @Query("pageNum") int pageNum);

    /**
     * 查询订单运费接口
     */
    @POST("babyOrder/queryDispatchList")
    Observable<PrintDispatchListResponse> queryDispatchList(@Query("orderId") String orderId,
                                                            @Query("addressId") String addressId);

    /**
     * 请求微信、支付宝支付
     */
    @POST("babyOrder/prepay")
    Observable<WxPrepayResponse> wexinPay(@Query("orderId") String orderId,
                                          @Query("payType") String payType);

    /**
     * 向相册中添加照片
     */
    @FormUrlEncoded
    @POST("babyCloudAlbums/addPicToCloudAlbum")
    Observable<BaseResponse> addPicToCloudAlbum(@Field("cloudAlbumId") String cloudAlbumId,
                                                @Field("mediaList") String mediaList);

    /**
     * 印刷车条目删除
     */
    @GET("babyOrder/delCartitem")
    Observable<BaseResponse> delCartitem(@Query("printId") String printId);

    /**
     * 获取图片数据
     */
    @GET("babyTime/queryImageInfoList")
    Observable<ImageInfoListResponse> queryImageInfoList(@Query("bookId") String bookId,
                                                         @Query("type") int type);

    /**
     * 创建作品
     */
    @FormUrlEncoded
    @POST("babyBook/createBook")
    Observable<CreateBookResponse> createBook(@Field("author") String author,
                                              @Field("babyId") int babyId,
                                              @Field("bookCover") String bookCover,
                                              @Field("bookId") String bookId,
                                              @Field("bookName") String bookName,
                                              @Field("bookSizeId") String bookSizeId,
                                              @Field("bookType") int bookType,
                                              @Field("dataList") String dataList,
                                              @Field("description") String description,
                                              @Field("openBookId") long openBookId,
                                              @Field("pageNum") int pageNum,
                                              @Field("openBookType") int openBookType);

    /**
     * 更换封面
     */
    @FormUrlEncoded
    @POST("babyBook/createBook")
    Observable<CreateBookResponse> editBookCover(@Field("bookId") String bookId,
                                                 @Field("babyId") int babyId,
                                                 @Field("bookCover") String bookCover,
                                                 @Field("author") String author,
                                                 @Field("bookName") String bookName,
                                                 @Field("description") String description);

    /**
     * 作品列表
     */
    @POST("babyBook/getBookList")
    Observable<MineBookListResponse> getBabyBookList(@Query("pageSize") int pageSize,
                                                     @Query("currentPage") int currentPage);

    /**
     * 新建识图卡片书-获取尺寸
     */
    @POST("babyBook/getSubBabyBookWorksTypeList")
    Observable<CardBookSizeResponse> getSubBabyBookWorksTypeList(@Query("id") int id);

    /**
     * 我的作品-删除作品
     */
    @POST("babyBook/deleteBook")
    Observable<BaseResponse> deleteBook(@Query("bookId") String bookId);

    /**
     * 取消订单
     */
    @POST("babyOrder/operOrder")
    Observable<BaseResponse> cancelOrder(@Query("orderId") String orderId);

    /**
     * 确认收货
     */
    @POST("babyOrder/receipt")
    Observable<BaseResponse> receipt(@Query("orderId") String orderId);

    /**
     * 意见反馈
     */
    @POST("member/feedback")
    Observable<BaseResponse> feedback(@Query("content") String content,
                                      @Query("userId") String userId);

    /**
     * 支付宝支付
     */
    @POST("babyOrder/alipay")
    Observable<AliPayResponse> aliPay(@Query("orderId") String orderId);

    /**
     * 获取时间内容列表
     *
     * @param userId
     * @return
     */
    @GET("timeAxis/list")
    Observable<TimeAxixResponse> getTimeAxisList(@Query("userId") String userId);

    @GET("timeAxis/list")
    Observable<TimeAxixResponse> getTimeAxisList();


    @POST("baby/getAvatarJson")
    Observable<IconHisResponse> getIconHistory();

    @POST("auth/binding")
    Observable<BaseResponse> bindPhone(@Query("phoneNum") String phoneNum,
                                       @Query("verifycode") String verifycode);

    /**
     * @param newPwd
     * @param oldPwd
     * @return
     */
    @POST("auth/modifyPassword\n")
    Observable<BaseResponse> modifyPassword
    (@Query("newPwd") String newPwd,
     @Query("oldPwd") String oldPwd);


    /**
     * 获取他图片标签列表
     *
     * @param lat
     * @param log
     * @return
     */
    @POST("label/list")
    Observable<TipResponse> getTips(@Query("lat") String lat,
                                    @Query("log") String log);

    /**
     * 添加图片为喜欢
     *
     * @param favore
     * @param mediaId
     * @return 喜欢的数量
     */
    @POST("label/addLike")
    Observable<LikeResponse> addLabelLike(@Query("favore") String favore,
                                          @Query("mediaId") String mediaId);

    /**
     * 添加标签到图片中
     *
     * @param mediaId
     * @param tips
     * @return
     */
    @POST("label/addLabel")
    Observable<AddTagResponse> addLabel(@Query("mediaId") String mediaId,
                                        @Query("tips") String tips);

    /**
     * 删除标签
     *
     * @param mediaId
     * @param tips
     * @return
     */
    @POST("label/delLabel")
    Observable<BaseResponse> deleteLabel(@Query("mediaId") String mediaId,
                                         @Query("tipId") String tips);

    /**
     * 获取印成长首页数据
     *
     * @return print growth data
     */
    @GET("printGrowth/index")
    Observable<PrintGrowthHomeResponse> printGrowthHome();

    /**
     * 获取书的列表数据
     *
     * @param bookType       书类型
     * @param permissionType 1,表示查询当前给宝宝的所有作品（包括创建者和关注者）2,表示查询当前用户的所有作品（包括其关注和创建的所有宝宝）
     * @return
     */
    @GET("printGrowth/getBookListByBookType")
    Observable<BookListResponse> bookList(@Query("bookType") int bookType, @Query("permissionType") int permissionType);

    /**
     * 获取识图卡片列表
     *
     * @return recognize card list response
     */
    @GET("printGrowth/knowCardList")
    Observable<KnowledgeCardListResponse> recognizeCardList();

    /**
     * 获取日记卡片列表
     *
     * @return diary card list response
     */
    @GET("printGrowth/diaryCardList")
    Observable<DiaryCardListResponse> diaryCardList();

    /**
     * 获取附近热点
     *
     * @return diary card list response
     */
    @GET("map/getPois")
    Observable<NearLocalResponse> queryNearList(@Query("key") String key,
                                                @Query("lat") double lat,
                                                @Query("log") double log);

    /**
     * 创建卡片作品
     *
     * @param babyId
     * @param author
     * @param bookCover
     * @param bookId
     * @param bookName
     * @param bookType
     * @param description
     * @param extra
     * @param openBookId
     * @param openBookType
     * @param pageNum
     * @return
     */
    @FormUrlEncoded
    @POST("openBook/save")
    Observable<EditBookResponse> saveProduction(
            @Field("babyId") int babyId,
            @Field("author") String author,
            @Field("bookCover") String bookCover,
            @Field("bookId") String bookId,
            @Field("bookName") String bookName,
            @Field("bookType") int bookType,
            @Field("description") String description,
            @Field("extra") String extra,
            @Field("openBookId") String openBookId,
            @Field("openBookType") int openBookType,
            @Field("pageNum") int pageNum);

    /**
     * 编辑作品
     *
     * @param bookId      作品id
     * @param babyId      baby id
     * @param bookCover   作品封面
     * @param author      作者
     * @param bookName    书名
     * @param description 作品描述
     * @param pageNum     作品页数
     * @return
     */
    @FormUrlEncoded
    @POST("openBook/save")
    Observable<EditBookResponse> editProduction(
            @Field("bookId") String bookId,
            @Field("babyId") int babyId,
            @Field("bookCover") String bookCover,
            @Field("author") String author,
            @Field("bookName") String bookName,
            @Field("description") String description,
            @Field("pageNum") int pageNum);

    @FormUrlEncoded
    @POST("mediaID/backup")
    Observable<MediaIdResponse> mediaBackup(@Field("identifiers") String identifiers);

    /**
     * 按时间查询所有图片
     *
     * @param babyId
     * @return
     */
    @GET("printGrowth/getMediaInfoByTime")
    Observable<QueryPhotoResponse> queryPhotoByTime(
            @Query("babyId") int babyId);

    @POST("mediaID/localList")
    Observable<MediaIdResponse> localList(@Query("userId") String userId);

    /**
     * 按标签查询所有图片
     *
     * @param babyId
     * @return
     */
    @GET("printGrowth/getMediaInfoByLabel")
    Observable<QueryPhotoResponse> queryPhotoByLabel(
            @Query("babyId") int babyId);

    /**
     * 按地点查询所有图片
     *
     * @param babyId
     * @return
     */
    @GET("printGrowth/queryAllMediaByArea")
    Observable<QueryPhotoByLocationResponse> queryPhotoByLocation(
            @Query("babyId") int babyId);

    /**
     * 按成员查询所有图片
     *
     * @param babyId
     * @return
     */
    @GET("printGrowth/getMediaInfoByUser")
    Observable<QueryPhotoResponse> queryPhotoByUser(
            @Query("babyId") int babyId,
            @Query("userId") String userId);

    /**
     * 按时间查询所有时光
     *
     * @param babyId
     * @param type
     * @return
     */
    @GET("printGrowth/getTimeLineByTime")
    Observable<QueryTimeLineResponse> queryTimeLineByTime(
            @Query("babyId") int babyId,
            @Query("type") String type);

    /**
     * 按发布人查询所有时光
     *
     * @param babyId
     * @param userId
     * @param type
     * @return
     */
    @GET("printGrowth/getTimeLineByTimeByMember")
    Observable<QueryTimeLineResponse> queryTimeLineByMember(
            @Query("babyId") int babyId,
            @Query("memberId") String userId,
            @Query("type") int type);

    /**
     * 查询所有成员信息
     *
     * @return
     */
    @GET("printGrowth/getFamilyMember")
    Observable<UsersInfoResponse> queryUsers();

    /**
     * 查询图片信息
     *
     * @return
     */
    @GET("babyCloudAlbums/imageParam")
    Observable<ImageExInfoResponse> queryImageInfo(@Query("url") String url);

    /**
     * 查询页码
     *
     * @return
     */
    @GET("babyTime/getTimeOfPageNo")
    Observable<TimeOfPageResponse> timeOfpage(@Query("pageSize") int pageSize,
                                              @Query("timeId") int timeId);

    /**
     * 查询经纬度反地理编码
     *
     * @return
     */
    @GET("map/getAddress")
    Observable<QueryLocationInfoResponse> queryLocationInfo(@Query("lat") double lat,
                                                            @Query("log") double log);

    /**
     * 设置短信提醒
     *
     * @return
     */
    @GET("member/msgRemind")
    Observable<BaseResponse> smsRemind(@Query("open") int open);

    @GET("printGrowth/queryGroupMediaByArea")
    Observable<GroupPhotoByLocationResponse> groupPhotoByLocation(@Query("mediaIds") String mediaIds);

    @GET("map/getAddress")
    Observable<LocationInfoResponse> getAddress(@Query("lat") double lat, @Query("log") double log);

    @GET("printGrowth/bookMedias")
    Observable<QuerySelectedPhotoResponse> bookMedias(@Query("bookId") String bookId);


    /**
     * 印品介绍
     */
    @GET("printGrowth/introduce")
    Observable<ProductionIntroListResponse> queryProductionIntro(@Query("bookType") int bookType);


    /**
     * 开放平台里的书保存
     * 此处有一万只草泥马 从我心口奔腾而过，我只想说，我干你娘，接口真烂.
     */
    @POST("openBook/save")
    @FormUrlEncoded
    Observable<cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse> sdkBookSave(
            @Field("babyId") String babyId,
            @Field("author") String author,
            @Field("bookCover") String bookCover,
            @Field("bookId") String bookId, // 表示是  我们自己服务器的ID， 如果没有这个 null ： 表示新建， 有就是更新。
            @Field("bookName") String bookTitle,
            @Field("bookType") String bookType,
            @Field("description") String bookSummary,
            @Field("openBookId") String openBookId, //开放平台的ID
            @Field("openBookType") String openBookType,
            @Field("pageNum") int pageNum,
            @Field("extra") String extra  // 这个应该是个数组
    );

    /**
     * 加了个type
     *
     * @param type 书的大分类，在老檀的极力要求下加的
     * @return
     */
    @POST("openBook/save")
    @FormUrlEncoded
    @Deprecated
    Observable<cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse> sdkBookSave(
            @Field("book_id") String bookId,
            @Field("book_type") String bookType,
            @Field("type") String type,
            @Field("book_cover") String bookCover,
            @Field("book_author") String bookAuthor,
            @Field("author_avatar") String authorAvatar,
            @Field("book_title") String bookTitle,
            @Field("book_summary") String bookSummary,
            @Field("days") String commemorations,
            @Field("extra") String extra
    );

    /**
     * fetch all book in open sdk
     * actually those are indexed in timeFace server what you get.
     *
     * @param types this value is string array wrapper with '[]'
     * @return Observable
     */
    @GET("openBook/list")
    Observable<GeneralBookResponse> sdkBookList(@Query("book_type") String types);


    /**
     * Delete one book tha saved on timeFace server which created on OpenSDK
     * Actually deleteRemoteNotebook the index on timeFace server, not deleteRemoteNotebook this on OpenSDk yet.
     *
     * @param id id$type
     * @return
     * @Notice this id is'nt the id on OpenSDk
     */
    @GET("openBook/delete")
    Observable<cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse> sdkBookDelete(@Query("id") String id);

    /**
     * get one book info from timeFace server by the id  which on timeFace server
     * Just get the info for book id on OpenSDk
     *
     * @param id remote id$type
     * @return Observable
     */
    @GET("openBook/get")
    Observable<GeneralBookItemResponse> sdkBookGet(@Query("id") String id);

    /**
     * 我的首页
     */
    @GET("member/index")
    Observable<MineResponse> queryMine();

    @GET("printGrowth/getBookThemeId")
    Observable<GetThemeResponse> getDefaultTheme(@Query("bookType") int bookType);

    /**
     * 更新输编辑时间
     *
     * @param bookId
     * @return
     */
    @GET("printGrowth/updateBookTime")
    Observable<BaseResponse> updateBookTime(@Query("bookId") String bookId);

    @GET("openBook/getExtra")
    Observable<QueryProductionExtraResponse> getProductionExtra(@Query("bookId") String bookId);

    /**
     * 圈时光评论
     *
     * @param circleTimelineId
     * @param commentContent
     * @param commentDate
     * @param commentId
     * @return
     */
    @GET("dynamic/comment")
    Observable<CircleCommentResponse> circleComment(
            @Query("circleTimelineId") long circleTimelineId,
            @Query("commentContent") String commentContent,
            @Query("commentDate") long commentDate,
            @Query("commentId") long commentId);

    /**
     * 编辑某条时光
     *
     * @param circleTimelineInfo
     * @return
     */
    @GET("dynamic/update")
    Observable<UpdateTimeLineResponse> editCircleTime(
            @Query("circleTimelineInfo") String circleTimelineInfo);

    /**
     * 圈时光点赞、取消点赞
     *
     * @param circleTimelineId
     * @param like
     * @return
     */
    @GET("dynamic/like")
    Observable<BaseResponse> circleLike(
            @Query("circleTimelineId") long circleTimelineId,
            @Query("like") int like);

    /**
     * 删除某条评论
     *
     * @param commentId
     * @return
     */
    @GET("dynamic/comment/delete")
    Observable<BaseResponse> deleteComment(
            @Query("commentId") long commentId);

    /**
     * 圈时光详情
     *
     * @param circleTimelineId
     * @return
     */
    @GET("dynamic/detail")
    Observable<UpdateTimeLineResponse> circleTimeDetail(
            @Query("circleTimelineId") long circleTimelineId);

    /**
     * 删除某条圈时光
     *
     * @param circleTimelineId
     * @return
     */
    @GET("dynamic/delete")
    Observable<BaseResponse> circleTimeDelete(
            @Query("circleTimelineId") long circleTimelineId);

    /**
     * 加圈申请
     *
     * @param babyRealName
     * @param circleId
     * @param leaveWords
     * @return
     */
    @GET("babyCircle/join")
    Observable<BaseResponse> joinCircle(@Query("circleId") long circleId,
                                        @Query("babyRealName") String babyRealName,
                                        @Query("leaveWords") String leaveWords);

    /**
     * 创建圈子
     *
     * @param circleName 圈名称
     * @param openLever  0-私有圈 1-公有圈
     * @return
     */
    @GET("babyCircle/create")
    Observable<CircleCreateResponse> createCircle(@Query("circleName") String circleName,
                                                  @Query("openLever") int openLever);

    /**
     * 修改宝宝大名
     */
    @GET("circle/updateBabyRealName")
    Observable<BaseResponse> updateBabyRealName(@Query("babyId") long babyId,
                                                @Query("realName") String realName);

    /**
     * 圈首页
     *
     * @param circleId 圈ID
     * @return
     */
    @GET("circle/index")
    Observable<CircleIndexResponse> queryCircleIndex(@Query("circleId") long circleId);

    /**
     * 圈首页
     *
     * @param circleId 圈ID
     * @return
     */
    @GET("circle/indexInfo")
    Observable<CircleIndexInfoResponse> queryCircleIndexInfo(@Query("circleId") long circleId);

    /**
     * 圈列表
     *
     * @return
     */
    @GET("babyCircle/list")
    Observable<CircleListResponse> circleList();

    /**
     * 圈资料
     *
     * @param circleId 圈ID
     * @return
     */
    @GET("babyCircle/detail")
    Observable<CircleDetailResponse> circleDetail(@Query("circleId") long circleId);

    /**
     * 查找圈子
     *
     * @param circleName
     * @return
     */
    @GET("babyCircle/queryByCircleNumOrName")
    Observable<CircleSearchListResponse> queryByCircleNumOrName(@Query("circleName") String circleName);

    /**
     * 按照圈中的宝宝查询
     *
     * @param circleId
     * @return
     */
    @GET("circleMedia/byBaby")
    Observable<QueryCirclePhotoResponse<QueryByCircleBabyObj>> queryByCircleBaby(
            @Query("circleId") long circleId);

    /**
     * 查询某个圈成员的发布图片
     *
     * @param circleId
     * @param userId
     * @return
     */
    @GET("circleMedia/getUser")
    Observable<QueryCirclePhotoResponse<GetCirclePhotoByUserObj>> getCirclePhotoByUser(
            @Query("circleId") long circleId,
            @Query("userId") long userId);

    /**
     * 按发布者查询
     *
     * @param circleId
     * @return
     */
    @GET("circleMedia/byUser")
    Observable<QueryCirclePhotoResponse<QueryByCircleUserObj>> queryByCircleUser(
            @Query("circleId") long circleId);

    /**
     * 查询宝宝被圈中的照片
     *
     * @param circleId
     * @param babyId
     * @return
     */
    @GET("circleMedia/getAtBaby")
    Observable<QueryCirclePhotoResponse<GetCirclePhotoByUserObj>> getCirclePhotoByAtBaby(
            @Query("circleId") long circleId,
            @Query("babyId") long babyId);

    /**
     * 按年月查询所有照片
     *
     * @param circleId
     * @param year
     * @param month
     * @return
     */
    @GET("circleMedia/byYearMonth")
    Observable<QueryCirclePhotoResponse<GetCirclePhotoByUserObj>> getCirclePhotoByYearMonth(
            @Query("circleId") long circleId,
            @Query("year") String year,
            @Query("month") String month);

    /**
     * 按活动相册查询
     *
     * @param circleId
     * @return
     */
    @GET("activity/list")
    Observable<QueryCirclePhotoResponse<CircleActivityAlbumObj>> queryByCircleActivity(
            @Query("circleId") long circleId);

    /**
     * 按时间查询
     *
     * @param circleId
     * @return
     */
    @GET("circleMedia/byTime")
    Observable<QueryCircleByTimeResponse> queryByCircleTime(
            @Query("circleId") long circleId);

    /**
     * 活动相册中照片详情列表
     *
     * @param activityAlbumId
     * @return
     */
    @GET("activity/detail")
    Observable<QueryCirclePhotoResponse<GetCirclePhotoByUserObj>> getCirclePhotoByActivity(
            @Query("activityAlbumId") long activityAlbumId);

    /**
     * 搜索活动相册
     *
     * @param circleId
     * @param key
     * @return
     */
    @GET("activity/search")
    Observable<QueryCirclePhotoResponse<CircleActivityAlbumObj>> searchCircleActivity(
            @Query("circleId") long circleId,
            @Query("key") String key);

    /**
     * 新增宝宝
     *
     * @param circleId
     * @param babyName
     * @return
     */
    @GET("circle/addBaby")
    Observable<BaseResponse> addBaby(
            @Query("circleId") long circleId,
            @Query("babyName") String babyName);

    /**
     * 圈中某个宝宝
     *
     * @param circleId
     * @param babyName
     * @param babyId
     * @param mediaId
     * @return
     */
    @GET("circle/atBaby")
    Observable<BaseResponse> atBaby(
            @Query("circleId") long circleId,
            @Query("babyName") String babyName,
            @Query("babyId") long babyId,
            @Query("mediaId") long mediaId);

    /**
     * 获取圈内的所有宝宝
     *
     * @param circleId
     * @param hasAlone
     * @param mediaId
     * @return
     */
    @GET("circle/allBaby")
    Observable<QueryCirclePhotoResponse<GetCircleAllBabyObj>> getCircleAllBaby(
            @Query("circleId") long circleId,
            @Query("hasAlone") int hasAlone,
            @Query("mediaId") long mediaId);

    /**
     * 查询圈时光上次发布的活动相册
     *
     * @param circleId
     * @return
     */
    @GET("activity/lastActivity")
    Observable<QueryCircleLastActivityResponse> queryLastActivity(
            @Query("circleId") long circleId);

    /**
     * 圈首页
     *
     * @param circleId
     * @return
     */
    @GET("circle/index")
    Observable<CircleIndexResponse> circleIndex(
            @Query("circleId") long circleId);

    /**
     * 圈时光发布
     *
     * @param circleId
     * @param circleTimeline
     * @return
     */
    @GET("dynamic/publish")
    Observable<CirclePublishResponse> circlePublish(
            @Query("circleId") long circleId,
            @Query("circleTimeline") String circleTimeline);

    /**
     * 获取圈作业列表
     *
     * @param circleId
     * @return
     */
    @GET("homework/list")
    Observable<HomeWorkListResponse> homeWorkList(
            @Query("circleId") long circleId);

    /**
     * 老师布置作业详情列表
     *
     * @param taskId
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GET("homework/teacherHomeworkDetal")
    Observable<HomeWorkDetailResponse> teacherHomeworkDetal(
            @Query("taskId") long taskId,
            @Query("currentPage") int currentPage,
            @Query("pageSize") int pageSize);

    /**
     * 发布作业
     *
     * @param circleId
     * @param schoolTask
     * @return
     */
    @GET("homework/publish")
    Observable<HomeWorkPublishResponse> homeWorkPublish(
            @Query("circleId") long circleId,
            @Query("schoolTask") String schoolTask);

    /**
     * 家长提交作业的详情
     *
     * @param homeworkId
     * @return
     */
    @GET("homework/submitHomeworkDetal")
    Observable<HomeWorkSubmitResponse> submitHomeworkDetal(
            @Query("homeworkId") long homeworkId);

    /**
     * 提交作业
     *
     * @param taskId
     * @param homework
     * @return
     */
    @GET("homework/submit")
    Observable<HomeWorkSubmitResponse> homeWorkSubmit(
            @Query("taskId") long taskId,
            @Query("homework") String homework);

    /**
     * 同意教师认证
     *
     * @param userId
     * @return
     */
    @GET("teacherAuth/check")
    Observable<BaseResponse> checkTeacher(
            @Query("userId") long userId);

    /**
     * 需认证的教师列表
     *
     * @param circleId
     * @return
     */
    @GET("teacherAuth/list")
    Observable<QueryCirclePhotoResponse<TeacherAuthObj>> checkTeacherList(
            @Query("circleId") long circleId);

    /**
     * 查询是否有新的教师认证消息
     *
     * @param circleId
     * @return
     */
    @GET("teacherAuth/isHas")
    Observable<TeacherAuthIsHasResponse> isHas(
            @Query("circleId") long circleId);

    /**
     * 圈二维码详情
     *
     * @param circleId
     * @return
     */
    @GET("circle/qrcode")
    Observable<QrcodeResponse> qrcode(
            @Query("circleId") long circleId);

    /**
     * 圈资料编辑
     *
     * @param circleDetailInfo
     * @return
     */
    @GET("circle/update")
    Observable<BaseResponse> editCircleInfo(
            @Query("circleDetailInfo") String circleDetailInfo);

    /**
     * 发起/取消 某个成员的教师认证
     *
     * @param certification
     * @param circleId
     * @param userId
     * @return
     */
    @GET("teacherAuth/start")
    Observable<BaseResponse> teacherAuthStart(
            @Query("certification") int certification,
            @Query("circleId") long circleId,
            @Query("userId") long userId);

    /**
     * 圈成员列表
     *
     * @param circleId
     * @return
     */
    @GET("circle/memberList")
    Observable<CircleMemberListResponse> circleMemberList(
            @Query("circleId") long circleId);

    /**
     * 同意/拒绝 某人入圈
     *
     * @param agree
     * @param circleId
     * @param userId
     * @return
     */
    @GET("circle/joinCircleCheck")
    Observable<BaseResponse> joinCircleCheck(
            @Query("agree") int agree,
            @Query("circleId") long circleId,
            @Query("userId") long userId);

    /**
     * 圈头像推荐图片
     *
     * @return
     */
    @GET("circle/getDefaultCover")
    Observable<QueryCirclePhotoResponse<MediaObj>> getDefaultCover();

    /**
     * 修改圈成员昵称
     *
     * @param nickName
     * @param circleId
     * @param userId
     * @return
     */
    @GET("circle/updateNickname")
    Observable<BaseResponse> updateNickname(
            @Query("nickName") String nickName,
            @Query("circleId") long circleId,
            @Query("userId") long userId);

    /**
     * 获取活动相册列表
     *
     * @param circleId
     * @return
     */
    @POST("activity/list")
    Observable<ActiveSelectListResponse> queryActiveList(@Query("circleId") long circleId);

    /**
     * 创建活动相册
     *
     * @param albumName
     * @param circleId
     * @return
     */
    @POST("activity/create")
    Observable<CreateActiveResponse> createActive(@Query("albumName") String albumName,
                                                  @Query("circleId") long circleId);

    /**
     * 查询上一次发布圈动态的相册
     *
     * @param circleId
     * @return
     */
    @POST("activity/lastActivity")
    Observable<CreateActiveResponse> queryLastSelect(@Query("circleId") long circleId);

    /**
     * 发送圈动态
     *
     * @param circleId
     * @param circleTimeline
     * @return
     */
    @FormUrlEncoded
    @POST("dynamic/publish")
    Observable<TimeLineSendResponse> sendCircleTimeLine(@Field("circleId") long circleId,
                                                        @Field("circleTimeline") String circleTimeline);


    /**
     * 圈中/取消宝宝
     *
     * @param babyIds
     * @param cancleCircleId
     * @param circleId
     * @param mediaId
     * @return
     */
    @POST("circle/atBaby")
    Observable<BaseResponse> circleAtBaby(@Query("babyIds") String babyIds,
                                          @Query("cancleCircleId") String cancleCircleId,
                                          @Query("circleId") long circleId,
                                          @Query("mediaId") long mediaId);

    /**
     * 按某种条件查询时光列表
     * @param circleId 圈号
     * @param type 1-全部 2-按我发布的 3-按@我宝宝的
     * @return
     */
    @GET("circleBook/queryDynamicList")
    Observable<CircleTimeLinesResponse> queryCircleTimeLines(
            @Query("circleId") String circleId,
            @Query("type") int type);

    /**
     * 接口详情 (id: 7759) 复制URL
     * 接口名称 圈成员列表
     * 请求类型 get
     * 请求Url  /circle/memberList
     */
    @POST("circle/memberList")
    Observable<MemberListResponse> memberList(@Query("circleId") long circleId);

    /**
     * 接口详情 (id: 7760) 复制URL
     * 接口名称 同意/拒绝 某人入圈
     * 请求类型 get
     * 请求Url  /circle/joinCircleCheck
     */
    @POST("circle/joinCircleCheck")
    Observable<BaseResponse> joinCircleCheck(@Query("agree") long agree,
                                             @Query("circleId") long circleId,
                                             @Query("userId") long userId);

    /**
     * 接口详情 (id: 7758) 复制URL
     * 接口名称 发起/取消 某个成员的教师认证
     * 请求类型 get
     * 请求Url  /teacherAuth/start
     */
    @POST("teacherAuth/start")
    Observable<BaseResponse> start(@Query("certification") long certification,
                                   @Query("circleId") long circleId,
                                   @Query("userId") long userId);

    /**
     * 接口详情 (id: 7757) 复制URL
     * 接口名称 移除某个成员
     * 请求类型 post
     * 请求Url  /circle/removeMember
     */
    @POST("circle/removeMember")
    Observable<BaseResponse> removeMember(@Query("circleId") long circleId,
                                          @Query("userId") long userId);

    /**
     * 接口详情 (id: 7763) 复制URL
     * 接口名称 修改圈成员昵称
     * 请求类型 get
     * 请求Url  /circle/updateNickname
     */
    @POST("circle/updateNickname")
    Observable<BaseResponse> updateNickname(@Query("circleId") long circleId,
                                            @Query("nickName") String nickName,
                                            @Query("userId") long userId);

    /**
     * 删除圈动态
     * @param circleTimelineId
     * @return
     */
    @POST("dynamic/delete")
    Observable<BaseResponse> deleteCircleTimeLine(@Query("circleTimelineId") long circleTimelineId);

    @POST("dynamic/detail")
    Observable<CircleTimeLineDetailResponse> queryCircleTimeLineDetail(@Query("circleTimelineId") long circleTimelineId);

    /**
     * 接口详情 (id: 7762) 复制URL
     接口名称 成员近期发布的照片
     请求类型 get
     请求Url  /circle/getNewestPic
     */
    @POST("circle/getNewestPic")
    Observable<MediasResponse> getNewestPic(@Query("circleId") long circleId,
                                            @Query("userId") long userId);
}
