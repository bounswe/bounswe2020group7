package com.cmpe451.platon.page.fragment.forgotpass.model

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences

class ForgotPasswordRepository(sharedPreferences: SharedPreferences) {
    fun postPasswordForgotten(mail: String):Boolean {
        return true
    }

    fun postResetPassword(token: String, pass1: String, pass2: String):Boolean {
        return true
    }
}