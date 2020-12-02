package com.cmpe451.platon.page.fragment.login.presenter

import android.app.Application
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel (application: Application): AndroidViewModel(application) {

    var getUser: LiveData<User>
    var getToken:LiveData<String>
    private var repository: LoginRepository = LoginRepository()

    init {
        getUser = repository.getUser
        getToken = repository.getToken
    }

    fun insertUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            //repository.insert(user)
        }
    }

    fun tryToLogin(loginBtn: Button, emailEt: EditText, passEt: EditText, rememberChk: CheckBox) {
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
        val rememberBool = rememberChk.isChecked

        if (!flag){
            repository.tryToLogin(mailStr, passStr)
        }
    }




}