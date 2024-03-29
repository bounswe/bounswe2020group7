package com.cmpe451.platon.page.activity.login.fragment.forgotpass

import android.util.Patterns

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.google.gson.JsonObject

/*
 *   It is a bridge between forgot password repository and  forgot password fragment.
 */
class ForgotPasswordViewModel: ViewModel() {
    private val repository = ForgotPasswordRepository()
    val getResetResourceResponse: MutableLiveData<Resource<JsonObject>>
    val getSendResetMailResourceResponse: MutableLiveData<Resource<JsonObject>>

    init {
        getResetResourceResponse = repository.resetResourceResponse
        getSendResetMailResourceResponse = repository.sendResetMailResourceResponse
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
