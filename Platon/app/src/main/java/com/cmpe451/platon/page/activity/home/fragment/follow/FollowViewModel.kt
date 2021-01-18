package com.cmpe451.platon.page.activity.home.fragment.follow

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
    var getFollowRecommendationsResponse = repository.followRecommendationsResponse

    fun fetchFollowing(id:Int?, token:String?, page:Int, per_page:Int){
        if(token != null && id != null){
            repository.getFollowing(id, token, page, per_page)
        }
    }
    fun fetchFollowers(id:Int?, token:String?, page:Int, per_page:Int){
        if(token != null && id != null){
            repository.getFollowers(id, token, page, per_page)
        }
    }
    fun getFollowRecommendations(num_of_rec:Int, token:String){
        repository.getFollowRecommendations(num_of_rec, token)
    }

}