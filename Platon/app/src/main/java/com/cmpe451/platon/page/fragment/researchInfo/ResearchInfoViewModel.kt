package com.cmpe451.platon.page.fragment.researchInfo

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.networkmodels.models.Research
import com.cmpe451.platon.page.fragment.editprofile.EditProfileRepository

class ResearchInfoViewModel: ViewModel() {

    var currentResearch: MutableLiveData<Research> = MutableLiveData()

    private val repository = ResearchInfoRepository()

    var getResponseCode:MutableLiveData<String>
    var getResponseDeleteResearch: MutableLiveData<Pair<Int, String>>

    init
    {
        getResponseCode = repository.responseCodeAdd
        getResponseDeleteResearch = repository.responseDeleteResearch
    }


    fun addResearchInfo(title:String,description:String?,
                        year:Int,authToken: String){
        repository.addResearch(title, description, year, authToken)
    }
    fun setCurrentResearch(research:Research){
        this.currentResearch.value = research
    }

    fun editResearchInfo(projectId:Int,title: String, description: String?, year: Int, token: String) {
        repository.editResearch(projectId,title, description, year, token)
    }

    fun deleteResearchInfo(projectId:Int, token: String){
        repository.deleteResearch(projectId, token)
    }


}