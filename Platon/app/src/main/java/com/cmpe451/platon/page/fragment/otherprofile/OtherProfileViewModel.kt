package com.cmpe451.platon.page.fragment.otherprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.util.Definitions

class OtherProfileViewModel : ViewModel() {
    private var repository: OtherProfileRepository = OtherProfileRepository()
    var getUserResource = repository.userResource
    var getResearchesResource = repository.researchesResource
    var getFollowResourceResponse = repository.followResourceResponse

    var isUserPrivate = MutableLiveData<Boolean>()
    var isFollowing = MutableLiveData<Definitions.USERSTATUS>()

    fun getUser(userId:Int, token:String) {
        repository.getUser(userId, token)
    }

    fun setUserInfo(){
        isUserPrivate.value = getUserResource.value?.data?.is_private
        isFollowing.value = Definitions.USERSTATUS.FOLLOWING
    }

    fun fetchResearch(token: String, userId: Int) {
        repository.getResearch(token, userId)
    }
    fun setIsFollowing(status:Definitions.USERSTATUS){
        isFollowing.value = status
    }
    fun follow(follower_id:Int, following_id: Int, auth_token:String){
        repository.follow(follower_id,following_id, auth_token)
    }


}