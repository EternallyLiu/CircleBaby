package cn.timeface.open.api.services;

import java.util.List;

import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.response.Authorize;
import cn.timeface.open.api.models.response.EditPod;
import cn.timeface.open.api.models.response.EditText;
import cn.timeface.open.api.models.response.SimpleTemplate;
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

    @POST("api/authorize")
    Observable<BaseResponse<Authorize>> authorize(@Query("app_id") String app_id,
                                                  @Query("app_secret") String app_secret,
                                                  @Query("user_object") String user_object);

    @FormUrlEncoded
    @POST("pod/pod")
    Observable<BaseResponse<TFOBookModel>> getPOD(@Field("book_id") String book_id,
                                                  @Field("book_type") int book_type,
                                                  @Field("rebuild") String rebuild,
                                                  @Field("content_list") String content_list);

    @FormUrlEncoded
    @POST("pod/edittext")
    Observable<BaseResponse<EditText>> editText(@Field("book_id") String book_id,
                                                @Field("content_id") String content_id,
                                                @Field("element_model") String element_model,
                                                @Field("text") String text);

    @FormUrlEncoded
    @POST("pod/editpod")
    Observable<BaseResponse<EditPod>> editPod(@Field("book_id") String book_id,
                                              @Field("content_list") String content_list);

    @GET("api/templatelist")
    Observable<BaseResponse<List<SimpleTemplate>>> getTemplateList(@Query("book_type") int bookType);

    @GET("api/templateinfo")
    Observable<BaseResponse<TFOBookContentModel>> getTemplateInfo(@Query("template_id") String template_id,
                                                                  @Query("book_id") String book_id);

    @FormUrlEncoded
    @POST("api/editbookcover")
    Observable<BaseResponse<TFOBookContentModel>> postEditBookCover(@Field("template_id") String template_id,
                                                                    @Field("book_id") String book_id,
                                                                    @Field("book_title") String book_title,
                                                                    @Field("book_auth") String book_auth,
                                                                    @Field("content_list") String content_list);
}
