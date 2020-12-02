package com.cmpe451.platon.page.fragment.register.presenter

import android.content.SharedPreferences
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.navigation.NavController
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository
import com.cmpe451.platon.page.fragment.register.view.RegisterFragmentDirections

class RegisterPresenter(private var repository: RegisterRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) {
    fun getTermsAndConds(): String {
        return repository.terms
    }

    fun onAlreadyHaveAccountClicked() {
        //Definitions().vibrate(50, (view as Fragment).activity as BaseActivity)
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        navController.navigate(action)
    }

    fun onRegisterButtonClicked(register_btn: Button, firstName: EditText, lastName: EditText, mail: EditText, job:EditText, pass1: EditText, pass2: EditText, terms:CheckBox) {
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


        if (!flag) {
            repository.postRegister(firstNameStr, lastNameStr, mailStr, jobStr, pass1Str)
        }
    }
}