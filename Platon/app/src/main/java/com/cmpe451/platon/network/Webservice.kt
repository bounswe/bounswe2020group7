package com.cmpe451.platon.network

import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Webservice {


    @GET("api/follow/followers")
    fun getFollowers(@Query("following_id") followingId: Int,
                     @Query("page") page: Int,
                     @Query("per_page") per_page: Int,
                     @Header("auth_token") auth_token: String ) : Call<Followers?>

    @GET("api/follow/followings")
    fun getFollowing(@Query("follower_id") followingId: Int,
                     @Query("page") page: Int,
                     @Query("per_page") per_page: Int,
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

    //-
    @FormUrlEncoded
    @PUT("api/auth_system/user")
    fun editUserInfo(@Field("name") name:String?,
                     @Field("surname") surname:String?,
                     @Field("job") job:String?,
                     @Field("institution") institution:String?,
                     @Field("is_private") is_private:Int?,
                     @Field("google_scholar_name") google_scholar_name:String?,
                     @Field("researchgate_name") researchgate_name:String?,
                     @Header("auth_token") auth_token :String) : Call<JsonObject?>

    @PUT("api/auth_system/user")
    fun uploadPhoto(@Body image: RequestBody,
                    @Header("auth_token") auth_token :String): Call<JsonObject?>

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


    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/profile/notifications",hasBody = true)
    fun deleteNotification(@Field("notification_id") state:Int,
                           @Header("auth_token") auth_token :String) : Call<JsonObject?>

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
                   @Query("per_page") perPage:Int?):Call<Search?>


    @GET("api/search_engine/workspace")
    fun searchWorkspace(@Header("auth_token") token: String?,
                   @Query("search_query") query:String,
                   @Query("skill_filter") skill:String?,
                   @Query("event_filter") event:String?,
                   @Query("page") page:Int?,
                   @Query("per_page") perPage:Int?):Call<Search?>



    @GET("api/upcoming_events")
    fun getUpcomingEvents(
            @Query("page")  page: Int?,
            @Query("per_page") pageSize:Int?
    ):Call<UpcomingEvents?>

    @GET("api/workspaces/trending_projects")
    fun getTrendingProjects(
            @Query("number_of_workspaces") number_of_workspaces :Int
    ):Call<TrendingProjects?>

    @GET("api/workspaces/self")
    fun getPersonalWorkspacesList(
        @Header("auth_token") auth_token: String
    ) : Call<WorkspaceListItems?>

    @GET("api/workspaces")
    fun getWorkspace(
        @Query("workspace_id") workspace_id:Int,
        @Header("auth_token") auth_token: String
    ) : Call<Workspace?>
    @FormUrlEncoded
    @POST("api/workspaces")
    fun addWorkspace(
        @Field("title") title:String,
        @Field("description") description:String,
        @Field("is_private") is_private:Int?,
        @Field("max_collaborators") max_collaborators:Int?,
        @Field("deadline") deadline:String?,
        @Field("requirements") requirements:String?,
        @Field("skills") skills:String?,
        @Header("auth_token") auth_token: String
    ) : Call<JsonObject?>
    @FormUrlEncoded
    @PUT("api/workspaces")
    fun updateWorkspace(
        @Field("workspace_id") workspace_id:Int,
        @Field("title") title:String?,
        @Field("description") description:String?,
        @Field("is_private") is_private:Int?,
        @Field("max_collaborators") max_collaborators:Int?,
        @Field("deadline") deadline:String?,
        @Field("requirements") requirements:String?,
        @Field("skills") skills:String?,
        @Field("state") state:Int?,
        @Header("auth_token") auth_token: String
    ) : Call<JsonObject?>
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/workspaces",hasBody = true)
    fun deleteWorkspace(@Field("workspace_id") workspace_id: Int,
                        @Header("auth_token") token: String): Call<JsonObject?>

    @GET("api/file_system/folder")
    fun getFolder(
        @Query("workspace_id") workspace_id:Int,
        @Query("path") path:String,
        @Header("auth_token") auth_token: String
    ) : Call<Folder?>
}
