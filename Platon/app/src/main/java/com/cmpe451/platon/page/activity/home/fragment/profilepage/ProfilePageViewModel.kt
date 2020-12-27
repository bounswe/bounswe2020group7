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

    private var repository: ProfilePageRepository = ProfilePageRepository()

    var userSkills= repository.userSkills
    val getResearchesResourceResponse= repository.researchesResourceResponse
    val getAddDeleteSkillResourceResponse= repository.addDeleteSkillResourceResponse
    var allSkills = repository.allSkills
    var getUploadPhotoResourceResponse= repository.uploadPhotoResourceResponse
    val getEditProfileResourceResponse = repository.editProfileResourceResponse

    var getAddResearchResourceResponse= repository.addResearchResourceResponse
    var getDeleteResearchResourceResponse= repository.deleteResearchResourceResponse
    var getEditResearchResourceResponse = repository.editResearchResourceResponse

    var getUserComments = repository.userComments

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

    fun editProfile(firstnameTv: TextView?, lastnameTv: TextView?, job:String?,
                    institution:String?, isPrivateUser:Boolean?,
                    googleScholarTv: TextView?,
                    researchGateTv: TextView?, token:String?){

        var name:String? = null
        if(!firstnameTv?.text.isNullOrEmpty()){
            name  = firstnameTv?.text.toString()
        }
        var surname:String? = null
        if(!lastnameTv?.text.isNullOrEmpty()){
            surname  = lastnameTv?.text.toString()
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

    fun getComments(id: Int, token: String, page: Int, pageSize: Int) {
            repository.getComments(id, token, page, pageSize)
    }
}
