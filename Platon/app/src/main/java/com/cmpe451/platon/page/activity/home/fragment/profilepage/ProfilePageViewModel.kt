package com.cmpe451.platon.page.activity.home.fragment.profilepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*

class ProfilePageViewModel: ViewModel() {


    var userSkills: MutableLiveData<Resource<Skills>> = MutableLiveData()
    var currentResearch: MutableLiveData<Research> = MutableLiveData()
    val getResearchesResourceResponse: LiveData<Resource<Researches>>

    var allSkills: MutableLiveData<Resource<List<String>>> = MutableLiveData()

    private var repository: ProfilePageRepository = ProfilePageRepository()

    init {
        getResearchesResourceResponse = repository.researchesResourceResponse
        allSkills = repository.allSkills
        userSkills = repository.userSkills
    }

    fun setCurrentResearch(research:Research){
        this.currentResearch.value = research
    }


    fun fetchResearch(token: String?, userId:Int?) {
        if(token != null && userId != null){
            repository.getResearches(userId, token)
        }
    }


    fun getAllSkills() {
        repository.getAllSkills()
    }
    fun getUserSkills(userId:Int, token:String){
        repository.getUserSkills(userId, token)
    }
}
