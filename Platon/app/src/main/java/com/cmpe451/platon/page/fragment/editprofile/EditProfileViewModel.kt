package com.cmpe451.platon.page.fragment.editprofile

import android.widget.TextView
import androidx.lifecycle.ViewModel

class EditProfileViewModel:ViewModel() {

    private val repository = EditProfileRepository()

    val getEditProfileResourceResponse = repository.editProfileResourceResponse

    fun editProfile(firstnameTv:TextView, lastnameTv:TextView,
                            jobTv:TextView, isPrivateUser:Boolean,
                            profilePhotoTv:TextView,
                            googleScholarTv:TextView,
                            researchGateTv:TextView, token:String?){

        var name:String? = null
        if(!firstnameTv.text.isNullOrEmpty()){
            name  = firstnameTv.text.toString()
        }
        var surname:String? = null
        if(!lastnameTv.text.isNullOrEmpty()){
            surname  = lastnameTv.text.toString()
        }
        var job:String? = null
        if(!jobTv.text.isNullOrEmpty()){
            job  = jobTv.text.toString()
        }
        var google_scholar_name:String? = null
        if(!googleScholarTv.text.isNullOrEmpty()){
            google_scholar_name  = googleScholarTv.text.toString()
        }
        var researchgate_name:String? = null
        if(!researchGateTv.text.isNullOrEmpty()){
            researchgate_name  = researchGateTv.text.toString()
        }

        var profilePhoto:String? = null
        if(!profilePhotoTv.text.isNullOrEmpty()){
            profilePhoto  = profilePhotoTv.text.toString()
        }


        if(token != null) {
            repository.editUser(name, surname, job, isPrivateUser, profilePhoto, google_scholar_name, researchgate_name, token)
        }}

}