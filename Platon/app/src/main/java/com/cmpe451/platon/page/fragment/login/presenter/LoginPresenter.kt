package com.cmpe451.platon.page.fragment.login.presenter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import android.widget.Button
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
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable


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

    override fun triggerLogin(token: String?, rememberBool:Boolean, mailStr:String, passStr:String) {
        ((view as Fragment).activity as BaseActivity).finish()
        ((view as Fragment).activity as BaseActivity).startActivity(
            Intent(
                (view as Fragment).activity,
                HomeActivity::class.java
            )
        )

        sharedPreferences.edit().putBoolean("remember_me", rememberBool).apply()
        if(rememberBool){
            sharedPreferences.edit().putString("login_mail", mailStr).apply()
            sharedPreferences.edit().putString("login_pass", passStr).apply()
        }

    }


    override fun onLoginButtonClicked(login_btn: Button, mail: EditText, pass: EditText, remember: CheckBox) {

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


        val dialog = Definitions().createProgressBar((view as Fragment).activity as Context)

        val observer = object :Observer<JsonObject>{
            override fun onSubscribe(d: Disposable?) {
                dialog.show()
            }

            override fun onNext(t: JsonObject?) {
                val token = t?.get("token")?.asString
                if (token != "null"){
                    sharedPreferences.edit().putString("token", token).apply()
                    Toast.makeText((view as Fragment).activity, "Login successful!", Toast.LENGTH_LONG).show()

                    triggerLogin(token, rememberBool, mailStr, passStr)
                }else{
                    Toast.makeText((view as Fragment).activity, "Token is null!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Throwable?) {
                Log.e("ERROR", e?.message + "" )

                val msg = e?.message

                if(msg != null && msg.contains("HTTP 400", true)){
                    Toast.makeText((view as Fragment).activity, "Input Format Error", Toast.LENGTH_LONG).show()
                }else if(msg != null && msg.contains("HTTP 401", true)){
                    Toast.makeText((view as Fragment).activity, "Account Problems", Toast.LENGTH_LONG).show()
                }else if(msg != null && msg.contains("HTTP 404", true)){
                    Toast.makeText((view as Fragment).activity, "E-mail not found", Toast.LENGTH_LONG).show()
                }else if(msg != null && msg.contains("HTTP 500", true)){
                    Toast.makeText((view as Fragment).activity, "Database Connection/E-mail Server Error", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText((view as Fragment).activity, "Server not responding!", Toast.LENGTH_LONG).show()
                }

                dialog.dismiss()
            }
            override fun onComplete() {
                dialog.dismiss()
            }
        }

        if (!flag){
            repository.tryToLogin(observer, mailStr, passStr)
        }
    }


    override fun onAlreadyHaveAccountClicked() {
        Definitions().vibrate(50, (view as Fragment).activity as BaseActivity)
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        navController.navigate(action)
    }

    override fun onForgotPasswordClicked() {
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