package com.cmpe451.platon.page.fragment.editprofile.presenter

import android.content.SharedPreferences
import android.util.Log
import androidx.navigation.NavController
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.page.fragment.editprofile.contract.EditProfileContract
import com.cmpe451.platon.page.fragment.editprofile.model.EditProfileRepository

import com.cmpe451.platon.util.Definitions
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

class EditProfilePresenter(view: EditProfileContract.View, private var repository: EditProfileRepository, private var sharedPreferences: SharedPreferences,private var navController: NavController) : EditProfileContract.Presenter {
    val token = sharedPreferences.getString("token", null)
    val user_id = sharedPreferences.getString("user_id", "4")!!.toInt()
    override fun onEditButtonClicked(name:String?,surname:String?,
                                     job:String?, isPrivate:Boolean?,
                                     profilePhoto:String?,
                                     google_scholar_name:String?,
                                     researchgate_name:String?) {
        editProfile(name,surname,job, isPrivate,profilePhoto, google_scholar_name, researchgate_name)
    }


    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }
    private fun editProfile(name:String?, surname:String?,
                            job:String?, isPrivate:Boolean?,
                            profilePhoto:String?,
                            google_scholar_name:String?,
                            researchgate_name:String?){
        if(token != null) {

            val observer = object : Observer<JsonObject> {
                override fun onSubscribe(d: Disposable?) {
                    Log.i("Attached", "Started")
                }

                override fun onNext(t: JsonObject?) {
                    navController.navigateUp()
                }

                override fun onError(e: Throwable?) {
                    Log.i("Error", e?.message.toString())
                }

                override fun onComplete() {
                    Log.i("Completed", "Completed")
                }

            }

//            val mytok = token.subSequence(user_id,token.length-1)
            repository.editUser(observer, name, surname, job, isPrivate, profilePhoto, google_scholar_name, researchgate_name, token)
        }}
}