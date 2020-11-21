package com.cmpe451.platon.page.fragment.forgotpass.presenter

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences
import android.net.sip.SipSession
import android.util.Log
import androidx.navigation.NavController
import com.cmpe451.platon.`interface`.Listener
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.forgotpass.contract.ForgotPasswordContract
import com.cmpe451.platon.page.fragment.forgotpass.model.ForgotPasswordRepository
import com.cmpe451.platon.page.fragment.forgotpass.view.ForgotPasswordFragment
import com.cmpe451.platon.util.HttpRequest
import org.json.JSONObject


class ForgotPasswordPresenter(private var view: ForgotPasswordContract.View?,
                              private var repository: ForgotPasswordRepository,
                              private var sharedPreferences: SharedPreferences,
                              private var navController: NavController) : ForgotPasswordContract.Presenter {
    override fun onForgotPassClicked(mail: String, flag:Boolean) {

        if (!flag){
            ((view as ForgotPasswordFragment).activity as LoginActivity).navController.navigateUp()
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