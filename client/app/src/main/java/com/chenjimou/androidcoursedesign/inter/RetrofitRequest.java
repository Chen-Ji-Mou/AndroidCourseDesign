package com.chenjimou.androidcoursedesign.inter;

import com.chenjimou.androidcoursedesign.model.GetCommentsModel;
import com.chenjimou.androidcoursedesign.model.DeleteCommentModel;
import com.chenjimou.androidcoursedesign.model.DeleteSpaceModel;
import com.chenjimou.androidcoursedesign.model.GetNoticesModel;
import com.chenjimou.androidcoursedesign.model.GetSpaceDetailModel;
import com.chenjimou.androidcoursedesign.model.GetStarCountModel;
import com.chenjimou.androidcoursedesign.model.GetAllSpacesModel;
import com.chenjimou.androidcoursedesign.model.LoginModel;
import com.chenjimou.androidcoursedesign.model.AddNewCommentModel;
import com.chenjimou.androidcoursedesign.model.GetPictureModel;
import com.chenjimou.androidcoursedesign.model.PostSpaceModel;
import com.chenjimou.androidcoursedesign.model.PostStarModel;
import com.chenjimou.androidcoursedesign.model.ReadNoticeModel;
import com.chenjimou.androidcoursedesign.model.RegisterModel;
import com.chenjimou.androidcoursedesign.model.GetUserSpaceModel;
import com.chenjimou.androidcoursedesign.model.UpLoadPictureModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitRequest
{
    /* ======================================== user =================================================== */

    /**
     * 注册用户
     * @param name 用户名
     * @param password 密码
     */
    @POST("/api/user/register")
    @FormUrlEncoded
    Observable<RegisterModel> register(@Field("name") String name, @Field("password") String password);

    /**
     * 用户登录
     * @param name 用户名
     * @param password 密码
     */
    @POST("/api/user/login")
    @FormUrlEncoded
    Observable<LoginModel> login(@Field("name") String name, @Field("password") String password);

    /**
     * 发布动态
     * @param token 令牌
     * @param content 动态内容
     * @param pictures 动态中的图片
     */
    @POST("/api/post")
    @FormUrlEncoded
    Observable<PostSpaceModel> postSpace(@Header("Authorization") String token, @Field("content") String content,
            @Field("pictures") List<String> pictures);

    /* ======================================== post =================================================== */

    /**
     * 获取首页动态列表
     * @param token 令牌
     */
    @GET("/api/post/hello")
    Observable<GetAllSpacesModel> getAllSpaces(@Header("Authorization") String token);

    /**
     * 获取某用户的所有动态
     * @param token 令牌
     * @param userId 该用户的id
     */
    @GET("/api/post/profile")
    Observable<GetUserSpaceModel> getUserSpace(@Header("Authorization") String token, @Query("userId") String userId);

    /**
     * 删除自己的动态
     * @param token 令牌
     * @param spaceId 动态id
     */
    @HTTP(method = "DELETE", path = "/api/post", hasBody = true)
    @FormUrlEncoded
    Observable<DeleteSpaceModel> deleteSpace(@Header("Authorization") String token, @Field("id") String spaceId);

    /**
     * 获取某条动态的具体信息
     * @param token 令牌
     * @param spaceId 动态id
     */
    @GET("/api/post/{id}")
    Observable<GetSpaceDetailModel> getSpaceDetail(@Header("Authorization") String token, @Path("id") String spaceId);

    /* ======================================== picture =================================================== */

    /**
     * 上传一张图片
     * @param token 令牌
     * @param base64 图片的base64编码字符串
     */
    @POST("/api/picture")
    @FormUrlEncoded
    Observable<UpLoadPictureModel> upLoadPicture(@Header("Authorization") String token, @Field("base64") String base64);

    /**
     * 获取一张图片
     * @param token 令牌
     * @param pictureId 图片id
     */
    @GET("/api/picture/{id}")
    Observable<GetPictureModel> getPicture(@Header("Authorization") String token, @Path("id") String pictureId);

    /* ======================================== comment =================================================== */

    /**
     * 添加一条评论
     * @param token 令牌
     * @param content 评论内容
     * @param spaceId 动态id
     */
    @POST("/api/comment")
    @FormUrlEncoded
    Observable<AddNewCommentModel> addNewComment(@Header("Authorization") String token, @Field("content") String content,
            @Field("postId") String spaceId);

    /**
     * 获取某条动态下的所有评论
     * @param token 令牌
     * @param spaceId 动态id
     */
    @GET("/api/comment/list")
    Observable<GetCommentsModel> getComments(@Header("Authorization") String token, @Query("postId") String spaceId);

    /**
     * 删除自己动态下的某条评论
     * @param token 令牌
     * @param commentId 评论id
     */
    @HTTP(method = "DELETE", path = "/api/post", hasBody = true)
    @FormUrlEncoded
    Observable<DeleteCommentModel> deleteComment(@Header("Authorization") String token, @Field("id") String commentId);

    /* ======================================== star =================================================== */

    /**
     * 点赞一个动态
     * @param token 令牌
     * @param userId 点赞的用户id
     */
    @POST("/api/star")
    @FormUrlEncoded
    Observable<PostStarModel> postStar(@Header("Authorization") String token, @Field("postId") String userId);

    /**
     * 查看一个动态下的所有点赞数
     * @param token 令牌
     * @param spaceId 动态id
     */
    @GET("/api/star/count")
    Observable<GetStarCountModel> getStarCount(@Header("Authorization") String token, @Query("postId") String spaceId);

    /* ======================================== notice =================================================== */

    /**
     * 获取用户所有的通知
     * @param token 令牌
     */
    @GET("/api/notice/all")
    Observable<GetNoticesModel> getNotices(@Header("Authorization") String token);

    /**
     * 将某个通知变为已读
     * @param token 令牌
     * @param noticeId 通知id
     */
    @POST("/api/notice/read")
    @FormUrlEncoded
    Observable<ReadNoticeModel> readNotice(@Header("Authorization") String token, @Field("id") String noticeId);
}