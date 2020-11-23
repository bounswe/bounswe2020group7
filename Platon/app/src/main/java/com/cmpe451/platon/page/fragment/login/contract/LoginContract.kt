package com.cmpe451.platon.page.fragment.login.contract

/**
 * @author Burak Ömür
 */

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observer

interface LoginContract {

    interface View : BaseView<Presenter> {
        fun setFields(mail: String, pass: String, b: Boolean)
        fun clickLogin()

    }

    interface Presenter : BasePresenter {
        fun onLoginButtonClicked(login_btn: Button, mail: EditText, pass: EditText, remember: CheckBox)
        fun onAlreadyHaveAccountClicked()
        fun onForgotPasswordClicked()
        fun onPreLoginAutomated()
        fun triggerLogin(token: String?, rememberBool:Boolean, mailStr:String, passStr:String)

    }

}