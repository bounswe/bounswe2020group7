package com.cmpe451.platon.page.fragment.login.presenter

/**
 * @author Burak Ömür
 */

import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
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


class LoginPresenter(
    private var view: LoginContract.View?,
    private var repository: LoginRepository,
    private var sharedPreferences: SharedPreferences,
    private var navController: NavController
) : LoginContract.Presenter {


    override fun onPreLoginAutomated(){
        val rememberMe = sharedPreferences.getBoolean("remember_me", false)

        if (rememberMe){

            Toast.makeText((view as LoginFragment).activity, "Logging In", Toast.LENGTH_LONG).show()

            val mail = sharedPreferences.getString("login_mail", null)
            val pass = sharedPreferences.getString("login_pass", null)
            if (mail != null && pass != null){
                (view as LoginFragment).setFields(mail, pass, true)
                (view as LoginFragment).clickLogin()
            }
        }
    }

    override fun triggerLogin(token: String?) {
        ((view as Fragment).activity as BaseActivity).finish()
        ((view as Fragment).activity as BaseActivity).startActivity(
            Intent(
                (view as Fragment).activity,
                HomeActivity::class.java
            )
        )
    }


    override fun onLoginButtonClicked(mail: EditText, pass: EditText, remember: CheckBox) {

        // define flag of problem
        var flag = false

        // check mail bo
        if (mail.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                mail.text.toString().trim()
            ).matches()){
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


        if (!flag){
            sharedPreferences.edit().putBoolean("remember_me", rememberBool).apply()
            sharedPreferences.edit().putString("login_mail", mailStr).apply()
            sharedPreferences.edit().putString("login_pass", passStr).apply()

            if (repository.tryToLogin(mailStr, passStr)) {

                val ht = HandlerThread("MyHandlerThread")
                ht.start()
                val handler = Handler(ht.looper)
                val runnable = Runnable {
                    var token:String? = null


                    while(token == null){
                        token = sharedPreferences.getString("token", null)
                        Thread.sleep(250)
                    }
                    if(token.subSequence(0, 4) != "fail"){
                        triggerLogin(token)
                    }else{
                        sharedPreferences.edit().remove("token").apply()
                        Toast.makeText(
                            (view as Fragment).activity,
                            "Some error occurred",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
                handler.post(runnable)
            }else{
                    Toast.makeText(
                        (view as Fragment).activity,
                        "Some error occurred",
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }


    override fun onAlreadyHaveAccountClicked() {
        Definitions().vibrate(50, (view as Fragment).activity as BaseActivity)
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        navController.navigate(action)

    }

    override fun onForgotPasswordClicked(mail: EditText) {
        Definitions().vibrate(50, (view as Fragment).activity as BaseActivity)
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