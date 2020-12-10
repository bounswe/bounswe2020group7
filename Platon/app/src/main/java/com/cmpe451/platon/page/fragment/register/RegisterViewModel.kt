package com.cmpe451.platon.page.fragment.register

import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.google.gson.JsonObject

class RegisterViewModel: ViewModel() {


    var getRegisterResourceResponse: MutableLiveData<Resource<JsonObject>>
    private var repository: RegisterRepository = RegisterRepository()

    init {
        getRegisterResourceResponse = repository.registerResourceResponse
    }


    fun getTermsAndConditions(): String {
        return repository.terms
    }


    fun onRegisterButtonClicked(firstName: String, lastName: String, mail: String, job: String, pass1: String, pass2: String):Boolean {
        val flag = firstName.isEmpty() || lastName.isEmpty()
                || mail.isEmpty() || job.isEmpty()
                || pass1.isEmpty() || pass2.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()


        if (!flag) repository.postRegister(firstName, lastName, mail, job, pass1)
        return flag
    }
}