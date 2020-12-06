package com.cmpe451.platon.page.fragment.otherprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.util.Definitions

class OtherProfileViewModel : ViewModel() {
    private var repository: OtherProfileRepository = OtherProfileRepository()
    var currentUser = repository.currentUser
    var currentResarch = repository.currentResearch
    var isUserPrivate = MutableLiveData<Boolean>()
    var isFollowing = MutableLiveData<Definitions.USERSTATUS>()

    fun getUser(userId:Int, token:String) {
        repository.getUser(userId, token)

    }
    fun setUserInfo(){
        isUserPrivate.value = currentUser.value?.is_private
        isFollowing.value = Definitions.USERSTATUS.NOT_FOLLOWING
    }

    fun fetchResearch(token: String, userId: Int) {
        repository.getResearch(token, userId)
    }
    fun setIsFollowing(status:Definitions.USERSTATUS){
        isFollowing.value = status
    }


}