package com.cmpe451.platon.page.fragment.login.presenter

import android.content.SharedPreferences
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository

class LoginPresenter(view: LoginContract.View, private var repository: LoginRepository, private var sharedPreferences: SharedPreferences) : LoginContract.Presenter {

    private var view: LoginContract.View? = view

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        this.view = null
    }
}