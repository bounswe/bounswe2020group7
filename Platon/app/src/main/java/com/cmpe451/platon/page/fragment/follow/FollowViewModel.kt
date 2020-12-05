package com.cmpe451.platon.page.fragment.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.networkmodels.models.Followers
import com.cmpe451.platon.networkmodels.models.Following

class FollowViewModel: ViewModel() {


    var followers: MutableLiveData<Followers>
    var following : MutableLiveData<Following>
    private var repository: FollowRepository =
        FollowRepository()

    init {
        followers = repository.followers
        following = repository.following
    }

    fun fetchFollowing(id:Int?, token:String?){
        if(token != null && id != null){
            repository.getFollowing(id, token)
        }
    }
    fun fetchFollowers(id:Int?, token:String?){
        if(token != null && id != null){
            repository.getFollowers(id, token)
        }
    }


}