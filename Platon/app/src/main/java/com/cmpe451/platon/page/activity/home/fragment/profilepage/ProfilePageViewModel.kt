package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import okhttp3.RequestBody

class ProfilePageViewModel: ViewModel() {


    var userSkills: MutableLiveData<Resource<Skills>> = MutableLiveData()
    var currentResearch: MutableLiveData<Research> = MutableLiveData()
    val getResearchesResourceResponse: MutableLiveData<Resource<Researches>>

    val getAddDeleteSkillResourceResponse: MutableLiveData<Resource<JsonObject>>

    var allSkills: MutableLiveData<Resource<List<String>>> = MutableLiveData()

    var getUploadPhotoResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    private var repository: ProfilePageRepository = ProfilePageRepository()

    init {
        getResearchesResourceResponse = repository.researchesResourceResponse
        allSkills = repository.allSkills
        userSkills = repository.userSkills
        getAddDeleteSkillResourceResponse = repository.addDeleteSkillResourceResponse
        getUploadPhotoResourceResponse = repository.uploadPhotoResourceResponse
    }

    fun setCurrentResearch(research:Research){
        this.currentResearch.value = research
    }

    val getEditProfileResourceResponse = repository.editProfileResourceResponse

    fun editProfile(firstnameTv: TextView, lastnameTv: TextView, job:String,
                    institution:String, isPrivateUser:Boolean,
                    googleScholarTv: TextView?,
                    researchGateTv: TextView?, token:String?){

        var name:String? = null
        if(!firstnameTv.text.isNullOrEmpty()){
            name  = firstnameTv.text.toString()
        }
        var surname:String? = null
        if(!lastnameTv.text.isNullOrEmpty()){
            surname  = lastnameTv.text.toString()
        }
        var google_scholar_name:String? = null
        if(!googleScholarTv?.text.isNullOrEmpty()){
            google_scholar_name  = googleScholarTv?.text.toString()
        }
        var researchgate_name:String? = null
        if(!researchGateTv?.text.isNullOrEmpty()){
            researchgate_name  = researchGateTv?.text.toString()
        }


        if(token != null) {
            repository.editUser(name, surname, job, institution, isPrivateUser, google_scholar_name, researchgate_name, token)
        }}

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

    fun uploadPhoto(fBody: RequestBody, token: String) {
        repository.uploadPhoto(fBody, token)
    }
}
