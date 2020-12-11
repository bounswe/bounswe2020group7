package com.cmpe451.platon.page.fragment.login

import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Auth
import com.google.gson.JsonObject

class LoginViewModel: ViewModel() {

    var getLoginResourceResponse: MutableLiveData<Resource<Auth>>

    private var repository: LoginRepository = LoginRepository()

    init {
        getLoginResourceResponse = repository.loginResourceResponse
    }

    fun tryToLogin(email: String, pass: String) {
        // define flag of problem
        val flag = email.isEmpty() || pass.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (!flag){
            repository.tryToLogin(email, pass)
        }
    }




}