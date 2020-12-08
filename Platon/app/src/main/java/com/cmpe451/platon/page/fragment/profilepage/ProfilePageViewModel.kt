package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*

class ProfilePageViewModel: ViewModel() {


    var currentResearch: MutableLiveData<Research> = MutableLiveData()

    var getUserResourceResponse:LiveData<Resource<User>>
    val getResearchesResourceResponse: LiveData<Resource<Researches>>

    val getUserFollowRequestsResourceResponse:MutableLiveData<Resource<FollowRequests>>
    val getUserNotificationsResourceResponse:MutableLiveData<Resource<List<Notification>>>



    private var repository: ProfilePageRepository = ProfilePageRepository()
    var acceptRequestResourceResponse = repository.acceptRequestResourceResponse
    var positionOfHandlededRequest:Int? = null

    init {
        getUserResourceResponse = repository.userResourceResponse
        getResearchesResourceResponse = repository.researchesResourceResponse
        getUserFollowRequestsResourceResponse = repository.userFollowRequestsResourceResponse
        getUserNotificationsResourceResponse = repository.userNotificationsResourceResponse
    }

    fun getNotifications(token:String){
        repository.getNotifications(token)
    }

    fun getFollowRequests(id:Int, token:String){
        repository.getFollowRequests(id, token)
    }


    fun setCurrentResearch(research:Research){
        this.currentResearch.value = research
    }

    fun fetchUser(token:String?){
        if(token != null){
            repository.getUser(token)
        }

    }

    fun fetchResearch(token: String?, userId:Int?) {
        if(token != null && userId != null){
            repository.getResearches(userId, token)
        }
    }

    fun getPersonalInformation():ArrayList<Map<String, String>> {
        val x = ArrayList<Map<String, String>>()
        x.add(mapOf(Pair("title", "E-mail"), Pair("info", getUserResourceResponse.value!!.data!!.e_mail)))
        x.add(mapOf(Pair("title", "Job"), Pair("info", getUserResourceResponse.value!!.data!!.job)))
        x.add(mapOf(Pair("title", "Rating"), Pair("info", getUserResourceResponse.value!!.data!!.rate.toString())))

        return x
    }

    fun acceptFollowRequest(followerId: Int, followingId:Int, token:String) {
        repository.acceptFollowRequest(followerId, followingId,token)
    }

    fun deleteFollowRequest(followerId: Int, followingId:Int, token:String) {
        repository.deleteFollowRequest(followerId, followingId,token)
    }

    fun setPositionOfHandledRequest(pos:Int){
        positionOfHandlededRequest = pos
    }
    fun removeHandledRequest(){
        if(positionOfHandlededRequest!= null){
            (getUserFollowRequestsResourceResponse.value?.data?.follow_requests as ArrayList).removeAt(positionOfHandlededRequest!!)
        }
    }
}
