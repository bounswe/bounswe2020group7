package com.cmpe451.platon.page.activity.home.fragment.otherprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Skills
import com.cmpe451.platon.util.Definitions

/*
 *  It is a bridge between other profile view repository and other profile fragment.
 */

class OtherProfileViewModel : ViewModel() {
    private var repository: OtherProfileRepository = OtherProfileRepository()
    var getUserResource = repository.userResource
    var getResearchesResource = repository.researchesResource
    var getFollowResourceResponse = repository.followResourceResponse
    var getUnfollowResourceResponse = repository.unFollowResourceResponse
    var userSkills: MutableLiveData<Resource<Skills>> =repository.userSkills
    var isUserPrivate = MutableLiveData<Boolean>()
    var isFollowing = MutableLiveData<Definitions.USERSTATUS>()
    var getInvitationResponse = repository.invitationResponse

    var getUserComments = repository.userComments
    var getAddDeleteCommentResourceResponse = repository.addDeleteCommentResourceResponse
    var getTagSearchResourceResponse = repository.tagSearchResourceResponse
    var getReportUserResourceResponse = repository.reportUserResourceResponse
    fun getUser(userId:Int, token:String) {
        repository.getUser(userId, token)
    }

    fun setUserInfo(){
        isUserPrivate.value = getUserResource.value?.data?.is_private
        isFollowing.value = when(getUserResource.value?.data?.following_status){
            1-> Definitions.USERSTATUS.FOLLOWING;
            0-> Definitions.USERSTATUS.REQUESTED;
            else-> Definitions.USERSTATUS.NOT_FOLLOWING
        }
    }

    fun fetchResearch(token: String, userId: Int, page:Int?, perPage:Int?) {
        repository.getResearch(token, userId,page, perPage)
    }
    fun setIsFollowing(status:Definitions.USERSTATUS){
        isFollowing.value = status
    }
    fun follow(follower_id:Int, following_id: Int, auth_token:String){
        repository.follow(follower_id,following_id, auth_token)
    }

    fun unfollow(following_id: Int, token: String) {
        repository.unfollow(following_id, token)
    }
    fun getUserSkills(userId:Int, token:String){
        repository.getUserSkills(userId, token)
    }

    fun getComments(id: Int, token: String, page: Int, pageSize: Int) {
        repository.getComments(id, token, page, pageSize)
    }

    fun addComment(rating: Int, comment: String?, Id: Int, token: String) {
        repository.addComment(rating, comment, Id, token)
    }

    fun deleteComment(commentId: Int, currUserToken: String) {
        repository.deleteComment(commentId, currUserToken)
    }

    fun sendInvitationToWorkspace(wsId: Int, invId: Int, currUserToken: String) {
        repository.sendInvitationToWorkspace(wsId, invId, currUserToken)
    }
    fun getTagSearchUser(name: String, page:Int?, perPage: Int?) {
        repository.getTagSearchUser(name, page, perPage)
    }

    fun reportUser(reported_user_id:Int, text:String?, token:String){
        repository.getReportUser(reported_user_id, text, token)
    }
}