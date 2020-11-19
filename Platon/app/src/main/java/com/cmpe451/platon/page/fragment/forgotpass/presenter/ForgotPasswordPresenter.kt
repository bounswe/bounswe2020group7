package com.cmpe451.platon.page.fragment.forgotpass.presenter

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

class DefListener:Listener{
    override fun onRequestCompleted(result: JSONObject?) {
        Log.println(Log.INFO, "tİTLE",result.toString())
    }

    override fun onFailure(errorMessage: String) {
        TODO("Not yet implemented")
    }

}


class ForgotPasswordPresenter(private var view: ForgotPasswordContract.View?,
                              private var repository: ForgotPasswordRepository,
                              private var sharedPreferences: SharedPreferences,
                              private var navController: NavController) : ForgotPasswordContract.Presenter {
    override fun onForgotPassClicked(mail: String, flag:Boolean) {

        if (!flag){

            val req = HttpRequest("http://google.com", "GET", null, null, DefListener() )

            req.execute()
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