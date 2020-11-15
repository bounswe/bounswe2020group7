package com.cmpe451.platon.page.fragment.login.presenter

import android.content.SharedPreferences
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.compose.navArgument
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import com.cmpe451.platon.page.fragment.login.view.LoginFragmentDirections

class LoginPresenter(private var view: LoginContract.View?, private var repository: LoginRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : LoginContract.Presenter {

    override fun onLoginButtonClicked(mail: String, pass: String, remember: Boolean) {
        Log.println(Log.INFO, "Important:", mail + pass + remember.toString())
    }

    override fun onAlreadyHaveAccountClicked() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        navController.navigate(action)
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