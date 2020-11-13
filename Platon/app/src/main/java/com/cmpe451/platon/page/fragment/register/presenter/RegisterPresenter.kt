package com.cmpe451.platon.page.fragment.register.presenter

import android.content.SharedPreferences
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository
import com.cmpe451.platon.page.fragment.register.contract.RegisterContract
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository

class RegisterPresenter(view: RegisterContract.View, private var repository: RegisterRepository, private var sharedPreferences: SharedPreferences) : RegisterContract.Presenter {

    private var view: RegisterContract.View? = view

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