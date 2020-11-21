package com.cmpe451.platon.page.fragment.register.presenter

import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.fragment.register.contract.RegisterContract
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository
import com.cmpe451.platon.page.fragment.register.view.RegisterFragment
import com.cmpe451.platon.page.fragment.register.view.RegisterFragmentDirections
import com.cmpe451.platon.util.Definitions

class RegisterPresenter(private var view: RegisterContract.View?, private var repository: RegisterRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : RegisterContract.Presenter {
    override fun getTermsAndConds(): String {
        return repository.terms
    }

    override fun onAlreadyHaveAccountClicked() {
        Definitions().vibrate(50, (view as Fragment).activity as BaseActivity)
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        navController.navigate(action)
    }

    override fun onRegisterButtonClicked(firstName: EditText, lastName: EditText, mail: EditText,job:EditText, pass1: EditText, pass2: EditText, terms:CheckBox) {

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

        if(pass1Str.equals(pass2Str, false)){
            pass2.error = "Required / Must match"
            flag = true
        }

        if (flag) {
            if(repository.postRegister(firstNameStr, lastNameStr, mailStr, jobStr, pass1Str)){
                Toast.makeText((view as Fragment).activity, "Success", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText((view as Fragment).activity, "Error", Toast.LENGTH_LONG).show()
            }
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