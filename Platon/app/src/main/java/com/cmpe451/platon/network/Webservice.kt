package com.cmpe451.platon.network

import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface Webservice {


    @GET("api/follow/followers")
    fun getFollowers(@Query("following_id") followingId: Int,
                     @Header("auth_token") auth_token: String ) : Call<Followers?>

    @GET("api/follow/followings")
    fun getFollowing(@Query("follower_id") followingId: Int,
                     @Header("auth_token") auth_token: String ) : Call<Following?>

    @FormUrlEncoded
    @POST("api/auth_system/login")
    fun makeLogin(@Field("e_mail") email: String,
                  @Field("password") password: String) : Call<Auth?>


    @FormUrlEncoded
    @POST("api/auth_system/reset_password")
    fun resetPassword(@Field("new_password") pass1: String,
                      @Field("new_password_repeat") pass2: String ,
                      @Header("auth_token") token: String) : Call<JsonObject?>


    @GET("api/auth_system/reset_password")
    fun resetPasswordSendKeycode(@Query("e_mail") mail: String): Call<JsonObject?>

    @FormUrlEncoded
    @POST("api/auth_system/user")
    fun makeRegister(@Field("name") firstName: String,
                     @Field("surname") lastName: String,
                     @Field("e_mail") email: String,
                     @Field("password") password: String ,
                     @Field("job") job: String,
                     @Field("institution") institution:String?) : Call<JsonObject?>

    @GET("api/auth_system/self")
    fun getUserInfo(@Header("auth_token") auth_token: String ) : Call<User?>

    @GET("api/profile/research_information")
    fun getResearches(@Query("user_id") userId: Int,
                      @Header("auth_token") auth_token: String,
                      @Query("page") page:Int?,
                      @Query("per_page") perPage:Int? ) : Call<Researches?>

    //@Multipart
    @FormUrlEncoded
    @PUT("api/auth_system/user")
    fun editUserInfo(@Field("name") name:String?,
                     @Field("surname") surname:String?,
                     @Field("job") job:String?,
                     @Field("institution") institution:String?,
                     @Field("is_private") is_private:Int?,
                     @Field("google_scholar_name") google_scholar_name:String?,
                     @Field("researchgate_name") researchgate_name:String?,
                     //@Part("image") image:MultipartBody.Part?,
                     @Header("auth_token") auth_token :String) : Call<JsonObject?>
    @FormUrlEncoded
    @POST("api/profile/research_information")
    fun addResearchProject(@Field("research_title") research_title:String,
                           @Field("description") description:String?,
                           @Field("year") year:Int,
                           @Header("auth_token") auth_token :String) : Call<JsonObject?>
    @FormUrlEncoded
    @PUT("api/profile/research_information")
    fun editResearchProject(@Field("research_id") research_id:Int,
                            @Field("research_title") research_title:String,
                            @Field("description") description:String?,
                            @Field("year") year:Int,
                            @Header("auth_token") auth_token :String) : Call<JsonObject?>
    @FormUrlEncoded
    @HTTP(method = "DELETE" , path = "api/profile/research_information", hasBody = true)
    fun deleteResearchProject(@Field("research_id") research_id:Int,
                              @Header("auth_token") auth_token :String) : Call<JsonObject?>

    @GET("api/auth_system/user")
    fun getOtherUserInfo(@Query("user_id") user_id:Int,
                         @Header("auth_token") auth_token :String): Call<OtherUser?>

    @FormUrlEncoded
    @POST("api/follow/follow_requests")
    fun follow(@Field("follower_id") follower_id:Int,
               @Field("following_id") following_id:Int,
               @Header("auth_token") auth_token :String) : Call<JsonObject?>


    @GET("api/profile/front_page")
    fun getActivityStream(@Header("auth_token") auth_token :String,
                          @Query("page") page:Int?,
                          @Query("per_page") perPage:Int?) : Call<List<ActivityStreamElement>?>

    @GET("api/profile/notifications")
    fun getNotifications(@Header("auth_token") auth_token :String,
            @Query("page") page:Int?,
            @Query("per_page") perPage:Int?) : Call<Notifications?>

    @GET("api/follow/follow_requests")
    fun getFollowRequests(@Query("following_id") id:Int,
                          @Header("auth_token")auth_token :String,
                            @Query("page") page:Int?,
                          @Query("per_page") perPage:Int?)  : Call<FollowRequests?>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/follow/follow_requests",hasBody = true)
    fun answerFollowRequests(@Field("follower_id") follower_id:Int,
                             @Field("following_id") following_id:Int,
                             @Field("state") state:Int,
                             @Header("auth_token") auth_token :String) : Call<JsonObject?>


    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/follow/followings",hasBody = true)
    fun unfollow(@Field("following_id") follower_id:Int,
                 @Header("auth_token") auth_token :String) : Call<JsonObject?>


    @GET("api/profile/jobs")
    fun getAllJobs(): Call<List<Job>?>


    @GET("api/profile/skills")
    fun getAllSkills(): Call<List<String>?>

    @GET("api/auth_system/skills")
    fun getUserSkills(@Query("user_id") user_id:Int,
                          @Header("auth_token") auth_token :String) : Call<Skills?>


    @FormUrlEncoded
    @POST("api/auth_system/skills")
    fun addSkillToUser(@Field("skill") skill:String, @Header("auth_token") token:String): Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/auth_system/skills",hasBody = true)
    fun deleteSkillFromUser(@Field("skill") s: String,
                            @Header("auth_token") token: String): Call<JsonObject?>


    @GET("api/search_engine/search_history")
    fun getSearchHistory(@Header("auth_token") token: String,
                         @Query("search_type") i: Int): Call<SearchHistory?>

    @GET("api/search_engine/user")
    fun searchUser(@Header("auth_token") token: String?,
                   @Query("search_query") query:String,
                    @Query("job_filter") job:Int?,
                   @Query("page") page:Int?,
                   @Query("per_page") perPage:Int?):Call<UserSearch?>

}
