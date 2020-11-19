package com.cmpe451.platon.page.fragment.login.presenter

import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import com.cmpe451.platon.page.fragment.login.view.LoginFragment
import com.cmpe451.platon.page.fragment.login.view.LoginFragmentDirections
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragment
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragmentDirections


class LoginPresenter(private var view: LoginContract.View?, private var repository: LoginRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : LoginContract.Presenter {


    override fun onPreLoginAutomated(){
        val rememberMe = sharedPreferences.getBoolean("remember_me", false)

        if (rememberMe){

            Toast.makeText((view as LoginFragment).activity, "Autologin..2", Toast.LENGTH_LONG).show()

            val mailPass = sharedPreferences.getStringSet("login_values", setOf()) as Set<String>

            if (mailPass.size == 2){
                val it = mailPass.iterator()
                val mail = it.next()
                val pass = it.next()

                (view as LoginFragment).setFields(mail, pass, true)

                (view as LoginFragment).clickLogin()
            }
        }
    }


    override fun onLoginButtonClicked(mail: String, pass: String, remember: Boolean, flag: Boolean) {
        if (!flag){
            sharedPreferences.edit().putBoolean("remember_me", remember).apply()

            sharedPreferences.edit().putStringSet("login_values", setOf(mail, pass)).apply()

            (view as LoginFragment).startActivity(Intent((view as LoginFragment).activity, HomeActivity::class.java))
            (view as LoginFragment).activity?.finish()
        }

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

    override fun onForgotPasswordClicked(mail: String) {
        val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
        val vib = ((view as LoginFragment).activity as LoginActivity).getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }

        ((view as LoginFragment).activity as LoginActivity).navController.navigate(action)

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