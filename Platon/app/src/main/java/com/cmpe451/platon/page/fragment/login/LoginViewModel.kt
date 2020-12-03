package com.cmpe451.platon.page.fragment.login

import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    var getToken:LiveData<String>
    var getResponseCode:LiveData<String>

    private var repository: LoginRepository = LoginRepository()

    init {
        getToken = repository.token
        getResponseCode = repository.loginResponse
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