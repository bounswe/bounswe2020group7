package com.cmpe451.platon.page.fragment.forgotpass

import android.util.Patterns

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel: ViewModel() {



    private val repository = ForgotPasswordRepository()
    val getResetResponse: MutableLiveData<Pair<Int, String>>
    val getSendResetMailResponse: MutableLiveData<Pair<Int, String>>

    init {
        getResetResponse = repository.resetResponse
        getSendResetMailResponse = repository.sendResetMailResponse
    }


    fun sendResetMail(email: String):Boolean {

        val flag = email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (!flag) {
            repository.sendResetKeycodeToMail(email)
        }
        return flag
    }


    fun resetPassword(pass1: String, pass2: String, token: String):Boolean {

        val flag = pass1.isEmpty() || pass2.isEmpty() || token.isEmpty()

        if (!flag){
            repository.resetPassword(token, pass1, pass2)
        }
        return flag
    }


}
