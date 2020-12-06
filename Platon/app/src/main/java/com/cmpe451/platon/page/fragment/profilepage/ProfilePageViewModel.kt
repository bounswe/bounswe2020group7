package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Research
import com.cmpe451.platon.network.models.Researches
import com.cmpe451.platon.network.models.User

class ProfilePageViewModel: ViewModel() {


    var currentResearch: MutableLiveData<Research> = MutableLiveData()

    var getUserResourceResponse:LiveData<Resource<User>>
    val getResearchesResourceResponse: LiveData<Resource<Researches>>

    private var repository: ProfilePageRepository = ProfilePageRepository()

    init {
        getUserResourceResponse = repository.userResourceResponse
        getResearchesResourceResponse = repository.researchesResourceResponse
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


}
