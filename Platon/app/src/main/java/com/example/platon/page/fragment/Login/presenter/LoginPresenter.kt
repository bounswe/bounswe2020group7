package com.example.platon.page.fragment.Login.presenter

import android.content.SharedPreferences
import com.example.platon.page.fragment.PreLogin.contract.PreLoginContract
import com.example.platon.page.fragment.PreLogin.model.PreLoginRepository

class LoginPresenter(view: PreLoginContract.View, private var repository: PreLoginRepository, private var sharedPreferences: SharedPreferences) : PreLoginContract.Presenter {

    private var view: PreLoginContract.View? = view

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