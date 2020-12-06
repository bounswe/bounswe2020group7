package com.cmpe451.platon.page.fragment.login

import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.google.gson.JsonObject

class LoginViewModel: ViewModel() {

    var getLoginResourceResponse: MutableLiveData<Resource<JsonObject>>

    private var repository: LoginRepository = LoginRepository()

    init {
        getLoginResourceResponse = repository.loginResourceResponse
    }

    fun tryToLogin(email: String, pass: String):Boolean {
        // define flag of problem
        val flag = email.isEmpty() || pass.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (!flag){
            repository.tryToLogin(email, pass)
        }
        return flag
    }




}