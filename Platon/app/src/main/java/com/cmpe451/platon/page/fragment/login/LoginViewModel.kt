package com.cmpe451.platon.page.fragment.login

import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    var getResponseCode:LiveData<Pair<Int, String>>

    private var repository: LoginRepository = LoginRepository()

    init {
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