package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject

class ProfilePageViewModel: ViewModel() {


    var currentResearch: MutableLiveData<Research> = MutableLiveData()
    val getResearchesResourceResponse: LiveData<Resource<Researches>>

    private var repository: ProfilePageRepository = ProfilePageRepository()
    var positionOfHandlededRequest:Int? = null

    init {
        getResearchesResourceResponse = repository.researchesResourceResponse
    }

    fun setCurrentResearch(research:Research){
        this.currentResearch.value = research
    }


    fun fetchResearch(token: String?, userId:Int?) {
        if(token != null && userId != null){
            repository.getResearches(userId, token)
        }
    }




    fun setPositionOfHandledRequest(pos:Int){
        positionOfHandlededRequest = pos
    }
}
