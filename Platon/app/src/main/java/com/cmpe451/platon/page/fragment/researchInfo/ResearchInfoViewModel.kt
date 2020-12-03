package com.cmpe451.platon.page.fragment.researchInfo

import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.page.fragment.editprofile.EditProfileRepository

class ResearchInfoViewModel: ViewModel() {

    private val repository = ResearchInfoRepository()
    val getResponseCode = repository.responseCode
    fun addResearchInfo(title:String,description:String?,
                        year:Int,authToken: String){
        repository.addResearch(title, description, year, authToken)
    }


}