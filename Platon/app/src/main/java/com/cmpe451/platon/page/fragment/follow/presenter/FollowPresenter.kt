package com.cmpe451.platon.page.fragment.follow.presenter

import android.content.SharedPreferences
import androidx.navigation.NavController
import com.cmpe451.platon.page.fragment.follow.model.FollowRepository

class FollowPresenter( private var repository: FollowRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController){
    val token = sharedPreferences.getString("token", null)
    val user_id = sharedPreferences.getString("user_id", "4")!!.toInt()

    fun bringFollowers() {
        if(token != null){
//            val mytok = token.subSequence(user_id,token.length-1)
            repository.getFollowers(user_id,token)
        }
    }
    fun bringFollowing() {
        if(token != null){
//            val mytok = token.subSequence(user_id,token.length-1)
            repository.getFollowing(user_id,token)
        }
    }

    fun goToProfilePage(id: Int) {

    }

}
