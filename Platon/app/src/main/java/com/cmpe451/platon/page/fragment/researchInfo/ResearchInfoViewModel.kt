package com.cmpe451.platon.page.fragment.researchInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Research
import com.google.gson.JsonObject

class ResearchInfoViewModel: ViewModel() {



    private val repository = ResearchInfoRepository()

    var getAddResearchResourceResponse:MutableLiveData<Resource<JsonObject>>
    var getDeleteResearchResourceResponse: MutableLiveData<Resource<JsonObject>>
    var getEditResearchResourceResponse:MutableLiveData<Resource<JsonObject>>
    init
    {
        getEditResearchResourceResponse = repository.editResearchResourceResponse
        getAddResearchResourceResponse = repository.addResearchResourceResponse
        getDeleteResearchResourceResponse = repository.deleteResearchResourceResponse
    }


    fun addResearchInfo(title:String,description:String?,
                        year:Int,authToken: String){
        repository.addResearch(title, description, year, authToken)
    }


    fun editResearchInfo(projectId:Int,title: String, description: String?, year: Int, token: String) {
        repository.editResearch(projectId,title, description, year, token)
    }

    fun deleteResearchInfo(projectId:Int, token: String){
        repository.deleteResearch(projectId, token)
    }


}