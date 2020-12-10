package com.cmpe451.platon.page.fragment.register

import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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


    fun onRegisterButtonClicked(firstName: String, lastName: String, mail: String, job: String, pass1: String, pass2: String, institution:String?):Boolean {
        val flag = firstName.isEmpty() || lastName.isEmpty()
                || mail.isEmpty() || job.isEmpty()
                || pass1.isEmpty() || pass2.isEmpty() || pass1 != pass2
                || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()

        if (!flag) repository.postRegister(firstName, lastName, mail, pass1,  job, institution )
        return flag
    }

    fun getAllJobs() {
        repository.getAllJobs()
    }
}