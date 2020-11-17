package com.cmpe451.platon.page.fragment.login.presenter

import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.navigation.NavController
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import com.cmpe451.platon.page.fragment.login.view.LoginFragment
import com.cmpe451.platon.page.fragment.login.view.LoginFragmentDirections


class LoginPresenter(private var view: LoginContract.View?, private var repository: LoginRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : LoginContract.Presenter {

    override fun onLoginButtonClicked(mail: String, pass: String, remember: Boolean) {

        (view as LoginFragment).startActivity(Intent((view as LoginFragment).activity, HomeActivity::class.java))
        (view as LoginFragment).activity?.finish()
        Log.println(Log.INFO, "Important:", mail + pass + remember.toString())
    }

    override fun onAlreadyHaveAccountClicked() {
        val vib = ((view as LoginFragment).activity as LoginActivity).getSystemService(VIBRATOR_SERVICE) as Vibrator
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
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