package com.cmpe451.platon.page.fragment.forgotpass.presenter

/**
 * @author Burak Ömür
 */

import android.content.SharedPreferences
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.fragment.forgotpass.contract.ForgotPasswordContract
import com.cmpe451.platon.page.fragment.forgotpass.model.ForgotPasswordRepository
import com.cmpe451.platon.page.fragment.register.view.RegisterFragment
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

class ForgotPasswordPresenter(private var view: ForgotPasswordContract.View?,
                              private var repository: ForgotPasswordRepository,
                              private var sharedPreferences: SharedPreferences,
                              private var navController: NavController) : ForgotPasswordContract.Presenter {

    override fun onForgotPassClicked(email: EditText, forgot_btn:Button, pass1: EditText, pass2: EditText, reset_btn:Button, token:EditText) {

        var flag = false
        if (email.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                email.text.toString().trim()
            ).matches()
        ) {
            email.error = "Required / invalid input"
            flag = true
        }

        val mail = email.text.toString().trim()

        val observer = object :Observer<JsonObject>{
            override fun onSubscribe(d: Disposable?) {
                Log.i("Subbed", "Subbed!")
                forgot_btn.isEnabled = false
                email.isEnabled = false
            }

            override fun onNext(t: JsonObject?) {
                val error = t?.has("error")

                if (error != null && !error){
                    email.visibility = View.GONE
                    forgot_btn.visibility = View.GONE
                    token.visibility = View.VISIBLE
                    reset_btn.visibility = View.VISIBLE
                    pass1.visibility = View.VISIBLE
                    pass2.visibility = View.VISIBLE
                }else{
                    Toast.makeText((view as Fragment).activity, "No matching e-mail found!", Toast.LENGTH_LONG).show()
                }

            }

            override fun onError(e: Throwable?) {
                Toast.makeText((view as Fragment).activity, "Request not answered", Toast.LENGTH_LONG).show()
                forgot_btn.isEnabled = true
                email.isEnabled = true
            }

            override fun onComplete() {
                forgot_btn.isEnabled = true
                email.isEnabled = true
                Log.i("Completed", "Completed!")
            }

        }


        if (!flag) {
            repository.postPasswordForgotten(observer, mail)
        }
    }


    override fun onResetPasswordClicked(reset_btn: Button, pass1: EditText, pass2: EditText, token: EditText) {

        var flag = false

        if(token.text.isNullOrEmpty()){
            token.error = "Required"
            flag= true
        }

        if(pass1.text.isNullOrEmpty()){
            pass1.error = "Required"
            flag = true
        }

        if(pass2.text.isNullOrEmpty() ){
            pass2.error = "Required!"
            flag = true
        }

        val pass1Str = pass1.text.toString().trim()
        val pass2Str = pass2.text.toString().trim()
        val tokenStr = token.text.toString().trim()

        if(!pass1Str.equals(pass2Str, false)){
            pass2.error = "Passwords must match!"
            flag = true
        }

        val observer = object :Observer<JsonObject>{
            override fun onSubscribe(d: Disposable?) {
                Log.i("Subbed", "Subbed!")
                reset_btn.isActivated = false
                pass1.isEnabled= false
                pass2.isEnabled = false
                token.isEnabled = false
            }

            override fun onNext(t: JsonObject?) {
                val error = t?.has("error")

                if(error != null && !error){
                    Toast.makeText((view as Fragment).activity, "Successfully changed!", Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                }else{
                    Toast.makeText((view as Fragment).activity, "Failed to change password!", Toast.LENGTH_LONG).show()
                }

            }

            override fun onError(e: Throwable?) {
                Toast.makeText((view as Fragment).activity, "Request not answered", Toast.LENGTH_LONG).show()
                reset_btn.isActivated = true
                pass1.isEnabled= true
                pass2.isEnabled = true
                token.isEnabled = true
            }

            override fun onComplete() {
                reset_btn.isActivated = true
                pass1.isEnabled= true
                pass2.isEnabled = true
                token.isEnabled = true
                Log.i("Completed", "Completed!")

            }

        }


        if (!flag){
            repository.postResetPassword(observer, tokenStr, pass1Str, pass2Str)
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