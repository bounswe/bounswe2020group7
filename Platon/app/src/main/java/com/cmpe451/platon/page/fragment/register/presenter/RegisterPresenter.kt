package com.cmpe451.platon.page.fragment.register.presenter

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragmentDirections
import com.cmpe451.platon.page.fragment.register.contract.RegisterContract
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository
import com.cmpe451.platon.page.fragment.register.view.RegisterFragment
import com.cmpe451.platon.page.fragment.register.view.RegisterFragmentDirections
import com.cmpe451.platon.util.Definitions
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*
import com.cmpe451.platon.R

class RegisterPresenter(private var view: RegisterContract.View?, private var repository: RegisterRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : RegisterContract.Presenter {
    override fun getTermsAndConds(): String {
        return repository.terms
    }

    override fun onAlreadyHaveAccountClicked() {
        Definitions().vibrate(50, (view as Fragment).activity as BaseActivity)
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        navController.navigate(action)
    }

    override fun onRegisterButtonClicked(register_btn: Button, firstName: EditText, lastName: EditText, mail: EditText, job:EditText, pass1: EditText, pass2: EditText, terms:CheckBox) {

        var flag = false

        if (firstName.text.isNullOrEmpty()){
            firstName.error = "Required"
            flag = true
        }
        if( lastName.text.isNullOrEmpty()){
            lastName.error = "Required"
            flag = true
        }
        if( mail.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail.text.toString().trim()).matches() ){
            mail.error = "Required / Wrong input"
            flag = true
        }
        if( pass1.text.isNullOrEmpty()){
            pass1.error = "Required"
            flag = true
        }
        if( pass2.text.isNullOrEmpty()){
            pass2.error = "Required"
            flag = true
        }
        if( job.text.isNullOrEmpty()){
            job.error = "Required"
            flag = true
        }
        if (!terms.isChecked){
            flag = true
        }


        val firstNameStr = firstName.text.toString().trim()
        val lastNameStr = lastName.text.toString().trim()
        val mailStr = mail.text.toString().trim()
        val pass1Str = pass1.text.toString().trim()
        val pass2Str = pass2.text.toString().trim()
        val jobStr = job.text.toString().trim()

        if(!pass1Str.equals(pass2Str, false)){
            pass2.error = "Required / Must match"
            flag = true
        }

        val dialog = Definitions().createProgressBar((view as Fragment).activity as Context)

        val observer = object :Observer<JsonObject>{
            override fun onSubscribe(d: Disposable?) {
                dialog.show()
            }

            override fun onNext(t: JsonObject?) {
                val error = t?.has("error")

                if(error != null && !error){
                    navController.navigateUp()
                    Toast.makeText((view as Fragment).activity, "Successfully registered!", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText((view as Fragment).activity, "Unknown error occurred!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Throwable?) {
                val msg = e?.message
                if( msg != null && msg.contains("HTTP 400", true)){
                    Toast.makeText((view as Fragment).activity, "Missing data fields or invalid data", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 409", true)){
                    Toast.makeText((view as Fragment).activity, "User with the given e-mail address already exists", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 500", true)){
                    Toast.makeText((view as Fragment).activity, "The server is not connected to the database", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 503", true)){
                    Toast.makeText((view as Fragment).activity, "The server could not send the account activation e-mail", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText((view as Fragment).activity, "Server not responding!", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }

            override fun onComplete() {
                dialog.dismiss()
            }

        }


        if (!flag) {
            repository.postRegister(observer, firstNameStr, lastNameStr, mailStr, jobStr, pass1Str)
        }
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