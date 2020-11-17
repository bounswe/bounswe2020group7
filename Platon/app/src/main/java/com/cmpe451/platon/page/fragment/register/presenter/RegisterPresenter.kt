package com.cmpe451.platon.page.fragment.register.presenter

import android.content.Context
import android.content.SharedPreferences
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.view.LoginFragment
import com.cmpe451.platon.page.fragment.login.view.LoginFragmentDirections
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository
import com.cmpe451.platon.page.fragment.register.contract.RegisterContract
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository
import com.cmpe451.platon.page.fragment.register.view.RegisterFragment
import com.cmpe451.platon.page.fragment.register.view.RegisterFragmentDirections

class RegisterPresenter(private var view: RegisterContract.View?, private var repository: RegisterRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : RegisterContract.Presenter {

    override fun onAlreadyHaveAccountClicked() {
        val vib = ((view as RegisterFragment).activity as LoginActivity).getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
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