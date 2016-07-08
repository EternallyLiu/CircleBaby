package cn.timeface.open.api.services;

import java.util.List;

import cn.timeface.open.api.models.base.BaseResponse;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.api.models.objs.TFOBookType;
import cn.timeface.open.api.models.objs.TFOSimpleTemplate;
import cn.timeface.open.api.models.response.Authorize;
import cn.timeface.open.api.models.response.BookList;
import cn.timeface.open.api.models.response.EditPod;
import cn.timeface.open.api.models.response.EditText;
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
     * 请求开放平台授权token,token是使用开放平台功能的唯一凭证，token存在有效期，过期后需要重新请求接口获取。 token期限需要第三方自行维护。
     *
     * @param app_id
     * @param app_secret
     * @param user_object
     * @return
     */
    @POST("api/authorize")
    Observable<BaseResponse<Authorize>> authorize(@Query("app_id") String app_id,
                                                  @Query("app_secret") String app_secret,
                                                  @Query("user_object") String user_object);

    /**
     * 开放平台一键成书功能,第三方传入指定类型数据，通过pod接口生成可用在线阅读或者二次编辑的排版数据
     *
     * @param book_id
     * @param book_type
     * @param rebuild
     * @param content_list
     * @return
     */
    @FormUrlEncoded
    @POST("pod/pod")
    Observable<BaseResponse<TFOBookModel>> getPOD(@Field("book_id") String book_id,
                                                  @Field("book_type") int book_type,
                                                  @Field("rebuild") String rebuild,
                                                  @Field("content_list") String content_list);

    /**
     * 修改文字接口
     *
     * @param book_id
     * @param content_id
     * @param element_model
     * @param text
     * @return
     */
    @FormUrlEncoded
    @POST("pod/edittext")
    Observable<BaseResponse<EditText>> editText(@Field("book_id") String book_id,
                                                @Field("content_id") String content_id,
                                                @Field("element_model") String element_model,
                                                @Field("text") String text);

    /**
     * 通过修改POD接口返回Content单页内容。修改是一个全量修改的过程，为了保持书内容的完整性，每一次修改都需要提交完成的修改数据。
     *
     * @param book_id
     * @param content_list
     * @return
     */
    @FormUrlEncoded
    @POST("pod/editpod")
    Observable<BaseResponse<EditPod>> editPod(@Field("book_id") String book_id,
                                              @Field("content_list") String content_list);

    /**
     * 开放平台提供自定义封面功能，请求接口获取时光书封面模板列表。
     *
     * @param bookType
     * @return
     */
    @GET("api/templatelist")
    Observable<BaseResponse<List<TFOSimpleTemplate>>> getTemplateList(@Query("book_type") int bookType);

    /**
     * 开放平台提供自定义封面功能，请求接口获取时光书封面模板详情
     *
     * @param template_id
     * @param book_id
     * @return
     */
    @GET("api/templateinfo")
    Observable<BaseResponse<TFOBookContentModel>> templateInfo(@Query("template_id") String template_id,
                                                               @Query("book_id") String book_id);

    /**
     * 时光书创建成功后，获取完整封面数据用于编辑封面
     *
     * @param template_id
     * @param book_id
     * @param book_title
     * @param book_auth
     * @param content_list
     * @return
     */
    @FormUrlEncoded
    @POST("api/editbookcover")
    Observable<BaseResponse<TFOBookContentModel>> editBookCover(@Field("template_id") String template_id,
                                                                @Field("book_id") String book_id,
                                                                @Field("book_title") String book_title,
                                                                @Field("book_auth") String book_auth,
                                                                @Field("content_list") String content_list);

    /**
     * 开放平台提供多种版式模板以供选择，用户可以自主进行选择。列表接口返回当前接入方可以选择的版式列表
     *
     * @return
     */
    @GET("api/booktypelist")
    Observable<BaseResponse<List<TFOBookType>>> bookTypeList();


    /**
     * 获取用户已创建的时光书列表
     *
     * @param page_size
     * @param current_page
     * @return
     */
    @GET("api/booklist")
    Observable<BaseResponse<BookList>> bookList(@Query("page_size") int page_size,
                                                @Query("current_page") int current_page);


    /**
     * 获取用户已创建的时光书列表
     *
     * @return
     */
    @GET("api/booklist")
    Observable<BaseResponse<BookList>> bookList();

    /**
     * 创建一本时光书
     *
     * @return
     */
    @FormUrlEncoded
    @POST("api/createbook")
    Observable<BaseResponse<BookList>> createBook(@Field("book_type") int book_type,
                                                  @Field("book_title") String book_title,
                                                  @Field("template_id") int template_id,
                                                  @Field("book_summary") String book_summary,
                                                  @Field("book_summary_image_url") String book_summary_image_url,
                                                  @Field("book_auth") String book_auth);

    @FormUrlEncoded
    @POST("api/createbook")
    Observable<BaseResponse<BookList>> createBook(@Field("book_type") int book_type,
                                                  @Field("book_title") String book_title);

    /**
     * 删除一本时光书
     *
     * @return
     */
    @FormUrlEncoded
    @POST("api/removebook")
    Observable<BaseResponse> removeBook(@Field("book_id") int book_id);

    /**
     * Page 重新排版
     *
     * @return
     */
    @FormUrlEncoded
    @POST("api/reformat")
    Observable<BaseResponse> reformat(@Field("content_ids") String content_ids);
}
