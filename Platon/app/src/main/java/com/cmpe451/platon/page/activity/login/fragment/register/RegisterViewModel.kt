package com.cmpe451.platon.page.activity.login.fragment.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Job
import com.google.gson.JsonObject

class RegisterViewModel: ViewModel() {


    var getRegisterResourceResponse: MutableLiveData<Resource<JsonObject>>
    var getJobListResourceResponse: MutableLiveData<Resource<List<Job>>>


    private var repository: RegisterRepository = RegisterRepository()

    init {
        getRegisterResourceResponse = repository.registerResourceResponse
        getJobListResourceResponse = repository.jobListResourceResponse
    }


    fun getTermsAndConditions(): String {
        return repository.terms
    }


    fun onRegisterButtonClicked(firstName: String, lastName: String, mail: String, job: String, pass1: String, pass2: String, institution:String?) {
        repository.postRegister(firstName, lastName, mail, pass1,  job, institution )
    }

    fun getAllJobs() {
        repository.getAllJobs()
    }
}