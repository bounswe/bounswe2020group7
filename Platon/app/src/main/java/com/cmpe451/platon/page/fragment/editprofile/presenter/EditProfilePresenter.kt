package com.cmpe451.platon.page.fragment.editprofile.presenter

import android.content.SharedPreferences
import android.util.Log
import androidx.navigation.NavController
import com.cmpe451.platon.page.fragment.editprofile.model.EditProfileRepository
import com.google.gson.JsonObject
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

class EditProfilePresenter(private var repository: EditProfileRepository, private var sharedPreferences: SharedPreferences,private var navController: NavController) {
    val token = sharedPreferences.getString("token", null)
    val user_id = sharedPreferences.getString("user_id", "4")!!.toInt()
    fun onEditButtonClicked(name:String?,surname:String?,
                                     job:String?, isPrivate:Boolean?,
                                     profilePhoto:String?,
                                     google_scholar_name:String?,
                                     researchgate_name:String?) {
        editProfile(name,surname,job, isPrivate,profilePhoto, google_scholar_name, researchgate_name)
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
            repository.editUser(name, surname, job, isPrivate, profilePhoto, google_scholar_name, researchgate_name, token)
        }}
}