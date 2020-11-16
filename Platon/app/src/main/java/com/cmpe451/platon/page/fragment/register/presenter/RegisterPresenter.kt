package com.cmpe451.platon.page.fragment.register.presenter

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository
import com.cmpe451.platon.page.fragment.register.contract.RegisterContract
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository
import com.cmpe451.platon.page.fragment.register.view.RegisterFragmentDirections

class RegisterPresenter(private var view: RegisterContract.View?, private var repository: RegisterRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : RegisterContract.Presenter {

    override fun onAlreadyHaveAccountClicked() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        navController.navigate(action)
    }

    override fun onRegisterButtonClicked(fullName: String, username: String, mail: String, pass1: String, pass2: String, phone: String, terms: Boolean) {
        Log.println(Log.INFO,"IMPORTANT:",fullName + username + mail + pass1 + pass2 + phone + terms.toString())
    }

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