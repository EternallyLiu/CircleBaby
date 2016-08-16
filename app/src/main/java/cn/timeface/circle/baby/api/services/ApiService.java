package cn.timeface.circle.baby.api.services;

import java.util.Map;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.responses.ADResponse;
import cn.timeface.circle.baby.api.models.responses.AddAddressResponse;
import cn.timeface.circle.baby.api.models.responses.AddCartItemResponse;
import cn.timeface.circle.baby.api.models.responses.AddressListResponse;
import cn.timeface.circle.baby.api.models.responses.AlbumDetailResponse;
import cn.timeface.circle.baby.api.models.responses.BabyInfoListResponse;
import cn.timeface.circle.baby.api.models.responses.BabyInfoResponse;
import cn.timeface.circle.baby.api.models.responses.BookTypeListResponse;
import cn.timeface.circle.baby.api.models.responses.CardBookSizeResponse;
import cn.timeface.circle.baby.api.models.responses.CardListResponse;
import cn.timeface.circle.baby.api.models.responses.CloudAlbumListResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryComposedResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.api.models.responses.DiaryTextResponse;
import cn.timeface.circle.baby.api.models.responses.DistrictListResponse;
import cn.timeface.circle.baby.api.models.responses.FamilyListResponse;
import cn.timeface.circle.baby.api.models.responses.ImageInfoListResponse;
import cn.timeface.circle.baby.api.models.responses.InviteResponse;
import cn.timeface.circle.baby.api.models.responses.LessResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneInfoResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneListResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneResponse;
import cn.timeface.circle.baby.api.models.responses.MilestoneTimeResponse;
import cn.timeface.circle.baby.api.models.responses.MineBookListResponse;
import cn.timeface.circle.baby.api.models.responses.MsgListResponse;
import cn.timeface.circle.baby.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.circle.baby.api.models.responses.MyOrderListResponse;
import cn.timeface.circle.baby.api.models.responses.ParamListResponse;
import cn.timeface.circle.baby.api.models.responses.PrintCartListResponse;
import cn.timeface.circle.baby.api.models.responses.PrintDispatchListResponse;
import cn.timeface.circle.baby.api.models.responses.PrintStatusResponse;
import cn.timeface.circle.baby.api.models.responses.RegisterResponse;
import cn.timeface.circle.baby.api.models.responses.RelationIdResponse;
import cn.timeface.circle.baby.api.models.responses.RelationshipResponse;
import cn.timeface.circle.baby.api.models.responses.SystemMsgListResponse;
import cn.timeface.circle.baby.api.models.responses.TimeDetailResponse;
import cn.timeface.circle.baby.api.models.responses.TimelineResponse;
import cn.timeface.circle.baby.api.models.responses.UnReadMsgResponse;
import cn.timeface.circle.baby.api.models.responses.UpdateResponse;
import cn.timeface.circle.baby.api.models.responses.UserLoginResponse;
import cn.timeface.circle.baby.payment.timeface.WxPrepayResponse;
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
    Observable<BaseResponse> publish(@Field("dataList") String dataList,
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

    //编辑时光
    @GET("babyTime/editTime")
    Observable<BaseResponse> editTime(@Query("content") String content,
                                      @Query("mediaList") String mediaList,
                                      @Query("milestone") int milestone,
                                      @Query("time") long time,
                                      @Query("timeId") int timeId);

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
    @POST("babyOrder/addOrder")
    Observable<LessResponse> addOrder(@Query("orderId") String orderId,
                                         @Query("dataList") String dataList,
                                      @Query("appId") String appId);

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
    Observable<BaseResponse> createBook(@Field("author") String author,
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
}
