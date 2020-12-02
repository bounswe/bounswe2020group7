package com.cmpe451.platon.page.fragment.login

import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    var getToken:LiveData<String>
    var getResponseCode:LiveData<Int>

    private var repository: LoginRepository = LoginRepository()

    init {
        getToken = repository.getToken
        getResponseCode = repository.getResponseCode
    }

    fun tryToLogin(emailEt: EditText, passEt: EditText) {
        // define flag of problem
        var flag = false

        // check mail bo
        if (emailEt.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                emailEt.text.toString().trim()
            ).matches()){
            emailEt.error = "Required field / Wrong input"
            flag = true
        }

        // check password box
        if( passEt.text.isNullOrEmpty()){
            passEt.error = "Required"
            flag = true
        }

        // gather
        val mailStr = emailEt.text.toString().trim()
        val passStr = passEt.text.toString().trim()

        if (!flag){
            repository.tryToLogin(mailStr, passStr)
        }
    }




}