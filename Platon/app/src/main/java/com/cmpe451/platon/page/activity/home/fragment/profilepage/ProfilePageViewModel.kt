package com.cmpe451.platon.page.activity.home.fragment.profilepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject

class ProfilePageViewModel: ViewModel() {


    var userSkills: MutableLiveData<Resource<Skills>> = MutableLiveData()
    var currentResearch: MutableLiveData<Research> = MutableLiveData()
    val getResearchesResourceResponse: LiveData<Resource<Researches>>

    val getAddDeleteSkillResourceResponse: LiveData<Resource<JsonObject>>

    var allSkills: MutableLiveData<Resource<List<String>>> = MutableLiveData()

    private var repository: ProfilePageRepository = ProfilePageRepository()

    init {
        getResearchesResourceResponse = repository.researchesResourceResponse
        allSkills = repository.allSkills
        userSkills = repository.userSkills
        getAddDeleteSkillResourceResponse = repository.addDeleteSkillResourceResponse
    }

    fun setCurrentResearch(research:Research){
        this.currentResearch.value = research
    }


    fun fetchResearch(token: String?, userId:Int?, page:Int?, perPage:Int?) {
        if(token != null && userId != null){
            repository.getResearches(userId, token, page, perPage)
        }
    }

    fun addSkillToUser(skill:String, token:String){
        repository.addSkillToUser(skill, token)
    }

    fun getAllSkills() {
        repository.getAllSkills()
    }
    fun getUserSkills(userId:Int, token:String){
        repository.getUserSkills(userId, token)
    }

    fun deleteSkillFromUser(s: String, token: String) {
        repository.deleteSkillFromUser(s, token)
    }
}
