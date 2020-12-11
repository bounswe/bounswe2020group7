package com.cmpe451.platon.page.fragment.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Followers
import com.cmpe451.platon.network.models.Following

class FollowViewModel: ViewModel() {


    var getFollowersResource: MutableLiveData<Resource<Followers>>
    var getFollowingResource : MutableLiveData<Resource<Following>>
    private var repository: FollowRepository =
        FollowRepository()

    init {
        getFollowersResource = repository.followersResource
        getFollowingResource = repository.followingResource
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