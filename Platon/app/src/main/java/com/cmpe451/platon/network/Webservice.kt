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

    @Multipart
    @PUT("api/auth_system/user")
    fun uploadPhoto(@Part("profile_photo\"; filename=\"profile_photo.png\" ") profile_photo: RequestBody,
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


    @GET("api/activity_stream")
    fun getActivityStream(@Header("auth_token") auth_token :String,
                          @Query("page") page:Int?,
                          @Query("per_page") perPage:Int?) : Call<ActivityStream?>

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
                   @Query("sorting_criteria") sortBy:Int?,
                   @Query("page") page:Int?,
                   @Query("per_page") perPage:Int?):Call<Search?>


    @GET("api/search_engine/upcoming_events")
    fun searchUpcomingEvent(@Header("auth_token") token: String?,
                   @Query("search_query") query:String,
                   @Query("date_filter_start") dateFilterS:String?,
                   @Query("date_filter_end") dateFilterE:String?,
                    @Query("deadline_filter_start") deadlineS:String?,
                    @Query("deadline_filter_end") deadlineE:String?,
                    @Query("sorting_criteria") sortBy:Int?,
                   @Query("page") page:Int?,
                   @Query("per_page") perPage:Int?):Call<Search?>


    @GET("api/search_engine/workspace")
    fun searchWorkspace(@Header("auth_token") token: String?,
                   @Query("search_query") query:String,
                   @Query("skill_filter") skill:String?,
                    @Query("creator_name") creatorName:String?,
                    @Query("creator_surname") creatorSurname:String?,
                    @Query("starting_date_start") startDateS:String?,
                    @Query("starting_date_end") startDateE:String?,
                    @Query("deadline_start") deadlineS:String?,
                    @Query("deadline_end") deadlineE:String?,
                    @Query("sorting_criteria") sortBy:Int?,
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
        @Field("upcoming_events") upcoming_events:String?,
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

    @FormUrlEncoded
    @POST("api/file_system/folder")
    fun addFolder(
        @Field("workspace_id") workspace_id:Int,
        @Field("path") path:String,
        @Field("new_folder_name") new_folder_name:String,
        @Header("auth_token") auth_token: String
    ) : Call<JsonObject?>


    @FormUrlEncoded
    @PUT("api/file_system/folder")
    fun changeFolderName(
        @Field("workspace_id") workspace_id:Int,
        @Field("path") path:String,
        @Field("new_folder_name") new_folder_name:String,
        @Header("auth_token") auth_token: String
    ) : Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/file_system/folder",hasBody = true)
    fun deleteFolder(@Field("workspace_id") workspace_id: Int,
                     @Field("path") path:String,
                     @Field("new_folder_name") new_folder_name:String,
                     @Header("auth_token") token: String): Call<JsonObject?>



    @Multipart
    @POST("api/file_system/file")
    fun uploadFileToWorkspace(
        @Part("workspace_id") workspace_id:Int,
        @Part("path") path:RequestBody,
        @Part("filename") fileName:RequestBody,
        @Part("new_file\"; filename=\"new_file\" ") new_file: RequestBody,
        @Header("auth_token") token: String): Call<JsonObject?>



    @GET("api/workspaces/milestone")
    fun getMilestones(
        @Query("workspace_id") workspace_id:Int,
        @Query("page") page:Int?,
        @Query("per_page") per_page:Int?,
        @Header("auth_token") auth_token: String
    ) : Call<Milestones?>

    @FormUrlEncoded
    @POST("api/workspaces/milestone")
    fun addMilestone(
        @Field("workspace_id") workspace_id:Int,
        @Field("title") title:String,
        @Field("description") description: String,
        @Field("deadline") deadline: String,
        @Header("auth_token") auth_token: String
    ) : Call<JsonObject?>


    @FormUrlEncoded
    @PUT("api/workspaces/milestone")
    fun updateMilestone(
        @Field("workspace_id") workspace_id:Int,
        @Field("milestone_id") milestone_id:Int,
        @Field("title") title:String?,
        @Field("description") description: String?,
        @Field("deadline") deadline: String?,
        @Header("auth_token") auth_token: String
    ) : Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/workspaces/milestone",hasBody = true)
    fun deleteMilestone( @Field("workspace_id") workspace_id:Int,
                      @Field("milestone_id") milestone_id:Int,
                     @Header("auth_token") token: String): Call<JsonObject?>

    @FormUrlEncoded
    @POST("api/workspaces/applications")
    fun applyToWorkspace(
        @Field("workspace_id") workspace_id:Int,
        @Header("auth_token") auth_token: String
    ) : Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/workspaces/quit",hasBody = true)
    fun quitWorkspace(@Field("workspace_id") workspace_id: Int,
                     @Header("auth_token") token: String): Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/file_system/file",hasBody = true)
    fun deleteFile(
        @Field("workspace_id") workspaceId: Int,
        @Field("path") cwd: String,
        @Field("filename") file: String,
        @Header("auth_token") token: String):Call<JsonObject?>


    @GET("api/workspaces/invitations")
    fun getInvitationsFromWs(@Header("auth_token") currUserToken: String): Call<List<WorkspaceInvitation>?>

    @GET("api/workspaces/applications")
    fun getWorkspaceApplications(@Query("workspace_id") workspace_id: Int,
                                 @Header("auth_token") currUserToken: String): Call<List<WorkspaceApplication>?>
  
    @GET("api/workspaces/issue")
    fun getIssues(@Query("workspace_id") workspaceId: Int,
                  @Query("page") page: Int,
                  @Query("per_page") perPage: Int,
                  @Header("auth_token") authToken: String ): Call<Issues?>

    @FormUrlEncoded
    @POST("api/workspaces/issue")
    fun addIssue(@Field("workspace_id") workspaceId: Int,
                 @Field("title") title: String,
                 @Field("description") description: String,
                 @Field("deadline") deadline: String ,
                 @Header("auth_token") authToken: String): Call<JsonObject?>

    @FormUrlEncoded
    @PUT("api/workspaces/issue")
    fun editIssue(@Field("workspace_id") workspaceId:Int,
                  @Field("issue_id") issueId:Int,
                  @Field("title") title:String?,
                  @Field("description") description:String?,
                  @Field("deadline") deadline:String?,
                  @Header("auth_token") authToken:String?): Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE" , path = "api/workspaces/issue", hasBody = true)
    fun deleteIssue(@Field("workspace_id") workspaceId:Int,
                    @Field("issue_id") issueId:Int,
                    @Header("auth_token") auth_token :String) : Call<JsonObject?>



    @GET("api/workspaces/issue/comment")
    fun getIssueComments(@Query("workspace_id") workspaceId: Int,
                         @Query("issue_id") issueId: Int,
                         @Query("page") page: Int,
                         @Query("per_page") perPage: Int,
                         @Header("auth_token") authToken: String ): Call<IssueComment?>

    @GET("api/workspaces/issue/assignee")
    fun getIssueAssignee(@Query("workspace_id") workspaceId: Int,
                         @Query("issue_id") issueId: Int,
                         @Query("page") page: Int?,
                         @Query("per_page") perPage: Int?,
                         @Header("auth_token") authToken: String ): Call<IssueAssignee?>

    @FormUrlEncoded
    @POST("api/workspaces/issue/assignee")
    fun addIssueAssignee(@Field("workspace_id") workspaceId:Int,
                         @Field("issue_id") issueId:Int,
                         @Field("assignee_id") assigneeId:Int,
                         @Header("auth_token") authToken:String?): Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE" , path = "api/workspaces/issue/assignee", hasBody = true)
    fun deleteIssueAssignee(@Field("workspace_id") workspaceId:Int,
                         @Field("issue_id") issueId:Int,
                         @Field("assignee_id") assigneeId:Int,
                         @Header("auth_token") authToken:String?): Call<JsonObject?>

    @FormUrlEncoded
    @HTTP(method = "DELETE" , path = "api/workspaces/applications", hasBody = true)
    fun answerWorkspaceApplication(@Field("application_id") application_id:Int,
                    @Field("is_accepted") is_accepted:Int,
                    @Header("auth_token") auth_token :String) : Call<JsonObject?>



    @GET("api/follow/comment")
    fun getComments(
        @Query("commented_user_id") id:Int,
        @Query("page") page:Int?,
        @Query("per_page") perPage:Int?,
        @Header("auth_token") auth_token :String):Call<AllComments?>

    @HTTP(method = "DELETE" , path = "api/follow/comment", hasBody = true)
    fun deleteComment(@Query("comment_id") id:Int,
                                   @Header("auth_token") auth_token :String) : Call<JsonObject?>

    @FormUrlEncoded
    @POST("api/follow/comment")
    fun addComment(@Field("commented_user_id") userId: Int,
                 @Field("rate") rate: Int,
                 @Field("text") text: String?,
                 @Header("auth_token") authToken: String): Call<JsonObject?>
    @FormUrlEncoded
    @POST("api/workspaces/invitations")
    fun inviteToWorkspace(@Field("workspace_id") workspace_id: Int,
                   @Field("invitee_id") invitee_id: Int,
                   @Header("auth_token") authToken: String): Call<JsonObject?>



    @FormUrlEncoded
    @HTTP(method = "DELETE" , path = "api/workspaces/invitations", hasBody = true)
    fun answerWorkspaceInvitation(@Field("invitation_id") application_id:Int,
                                   @Field("is_accepted") is_accepted:Int,
                                   @Header("auth_token") auth_token :String) : Call<JsonObject?>


    @GET("api/workspaces/issue/comment")
    fun getIssueComments(
        @Query("workspace_id") workspaceId:Int,
        @Query("issue_id") issueId:Int,
        @Query("page") page:Int?,
        @Query("per_page") perPage:Int?,
        @Header("auth_token") authToken :String):Call<IssueAllComments?>

    @FormUrlEncoded
    @HTTP(method = "DELETE" , path = "api/workspaces/issue/comment", hasBody = true)
    fun deleteIssueComment(
        @Query("workspace_id") workspaceId:Int,
        @Query("issue_id") issueId:Int,
        @Query("commentId") commentId:Int?,
        @Header("auth_token") authToken :String):Call<JsonObject?>


    @FormUrlEncoded
    @POST("api/workspaces/issue/comment")
    fun addIssueComment(@Field("workspace_id") workspaceId: Int,
                        @Field("issue_id") issueId: Int,
                        @Field("comment") comment: String,
                        @Header("auth_token") authToken: String): Call<JsonObject?>


    @GET("api/search_engine/tag_search")
    fun getTagSearch(@Query("search_type") searchType:Int,
                     @Query("skills") name: String,
                     @Query("page") page: Int?,
                     @Query("per_page") pageSize: Int?): Call<Search?>

    @GET("api/recommendation_system/collaboration")
    fun getRecommendedCollaborators(
        @Query("workspace_id") workspace_id:Int,
        @Query("number_of_recommendations") number_of_recommendations:Int,
        @Header("auth_token") authToken :String):Call<RecommendedUserList?>
    @GET("api/recommendation_system/follow")
    fun getRecommendedFollows(
        @Query("number_of_recommendations") number_of_recommendations:Int,
        @Header("auth_token") authToken :String):Call<RecommendedUserList?>
    @GET("api/recommendation_system/workspace")
    fun getRecommendedWorkspaces(
        @Query("number_of_recommendations") number_of_recommendations:Int,
        @Header("auth_token") authToken :String):Call<RecommendedWorkspaceList?>


    @FormUrlEncoded
    @POST("api/follow/report")
    fun reportUser(@Field("reported_user_id") reported_user_id: Int,
                        @Field("text") text: String?,
                        @Header("auth_token") authToken: String): Call<JsonObject?>


}
