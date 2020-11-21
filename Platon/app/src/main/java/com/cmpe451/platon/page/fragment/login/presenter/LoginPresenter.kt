package com.cmpe451.platon.page.fragment.login.presenter

/**
 * @author Burak Ömür
 */

import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Patterns
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import com.cmpe451.platon.page.fragment.login.view.LoginFragment
import com.cmpe451.platon.page.fragment.login.view.LoginFragmentDirections
import com.cmpe451.platon.util.Definitions


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


    override fun onLoginButtonClicked(mail: EditText, pass: EditText, remember: CheckBox) {

        // define flag of problem
        var flag = false

        // check mail bo
        if (mail.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail.text.toString().trim()).matches()){
            mail.error = "Required field / Wrong input"
            flag = true
        }

        // check password box
        if( pass.text.isNullOrEmpty()){
            pass.error = "Required"
            flag = true
        }

        // gather
        val mailStr = mail.text.toString().trim()
        val passStr = pass.text.toString().trim()
        val rememberBool = remember.isChecked


        if (!flag && !mailStr.equals(passStr, true)){
            sharedPreferences.edit().putBoolean("remember_me", rememberBool).apply()

            sharedPreferences.edit().putStringSet("login_values", setOf(mailStr, passStr)).apply()

            (view as LoginFragment).startActivity(Intent((view as LoginFragment).activity, HomeActivity::class.java))
            (view as LoginFragment).activity?.finish()
        }

        Log.println(Log.INFO, "Important:", mailStr + passStr + rememberBool.toString())
    }


    override fun onAlreadyHaveAccountClicked() {
        Definitions().vibrate (50, (view as Fragment).activity as BaseActivity)
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        navController.navigate(action)

    }

    override fun onForgotPasswordClicked(mail: EditText) {
        Definitions().vibrate (50, (view as Fragment).activity as BaseActivity)
        val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
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