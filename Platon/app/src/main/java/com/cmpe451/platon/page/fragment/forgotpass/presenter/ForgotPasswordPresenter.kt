package com.cmpe451.platon.page.fragment.forgotpass.presenter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.cmpe451.platon.page.fragment.forgotpass.model.ForgotPasswordRepository
import com.cmpe451.platon.util.Definitions
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

class ForgotPasswordPresenter(private var repository: ForgotPasswordRepository,
                              private var sharedPreferences: SharedPreferences,
                              private var navController: NavController) {

    fun onForgotPassClicked(email: EditText, forgot_btn:Button, pass1: EditText, pass2: EditText, reset_btn:Button, token:EditText) {

        var flag = false
        if (email.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                email.text.toString().trim()
            ).matches()
        ) {
            email.error = "Required / invalid input"
            flag = true
        }

        val mail = email.text.toString().trim()

        /*
        val dialog = Definitions().createProgressBar((view as Fragment).activity as Context)


        val observer = object :Observer<JsonObject>{
            override fun onSubscribe(d: Disposable?) {
                dialog.show()
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
                    Toast.makeText((view as Fragment).activity, "Some unknown error occurred!", Toast.LENGTH_LONG).show()
                }

            }

            override fun onError(e: Throwable?) {
                val msg = e?.message

                if( msg != null && msg.contains("HTTP 400", true)){
                    Toast.makeText((view as Fragment).activity, "Input Format Error", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 401", true)){
                    Toast.makeText((view as Fragment).activity,  "Account Problems", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 404", true)){
                    Toast.makeText((view as Fragment).activity, "E-mail not found", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 500", true)){
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
*/

        if (!flag) {
            repository.postPasswordForgotten(mail)
        }
    }


    fun onResetPasswordClicked(reset_btn: Button, pass1: EditText, pass2: EditText, token: EditText) {

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
        /*
        val dialog = Definitions().createProgressBar((view as Fragment).activity as Context)


        val observer = object :Observer<JsonObject>{
            override fun onSubscribe(d: Disposable?) {
                dialog.show()
            }

            override fun onNext(t: JsonObject?) {
                val error = t?.has("error")

                if(error != null && !error){
                    Toast.makeText((view as Fragment).activity, "Successfully changed!", Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                }else{
                    Toast.makeText((view as Fragment).activity, "Some unknown error occurred!", Toast.LENGTH_LONG).show()
                }

            }

            override fun onError(e: Throwable?) {
                val msg = e?.message

                if( msg != null && msg.contains("HTTP 400", true)){
                    Toast.makeText((view as Fragment).activity, "Passwords are not matched", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 401", true)){
                    Toast.makeText((view as Fragment).activity,  "Authorization Error", Toast.LENGTH_LONG).show()
                }else if( msg != null && msg.contains("HTTP 500", true)){
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

*/
        if (!flag){
            repository.postResetPassword(tokenStr, pass1Str, pass2Str)
        }

    }


}