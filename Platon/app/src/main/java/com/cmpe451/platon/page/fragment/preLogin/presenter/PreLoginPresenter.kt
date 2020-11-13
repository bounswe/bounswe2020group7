package com.cmpe451.platon.page.fragment.preLogin.presenter

import android.content.SharedPreferences
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository

class PreLoginPresenter(view: PreLoginContract.View, private var repository: PreLoginRepository, private var sharedPreferences: SharedPreferences) : PreLoginContract.Presenter {

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