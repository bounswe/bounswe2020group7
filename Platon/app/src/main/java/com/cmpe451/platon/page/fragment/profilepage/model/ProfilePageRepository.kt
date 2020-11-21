package com.cmpe451.platon.page.fragment.profilepage.model

import android.content.Context
import android.content.SharedPreferences
import com.cmpe451.platon.util.Definitions.User

class ProfilePageRepository (sharedPreferences: SharedPreferences){
    fun fetchFollowers(context: Context?) : ArrayList<User>{
        return arrayListOf(
                User("Oyku", "Yilmaz", 3.0, "Cmpe"),
                User("Burak", "Omur", 5.0, "Cmpe"),
                User("Ertugrul", "Bulbul", 4.0, "Cmpe")
        )
    }
    fun fetchFollowing(context: Context?) : ArrayList<User>{
        return arrayListOf(
                User("Oyku", "Yilmaz", 3.0, "Cmpe"),
                User("Burak", "Omur", 5.0, "Cmpe"),
                User("Ertugrul", "Bulbul", 4.0, "Cmpe")
        )
    }
    fun fetchProfilePageDetails(context: Context?) : ArrayList<MutableMap<String,String>>{
        return getUserDetails(User("Orkan", "Akisu", 3.0, "Cmpejdfhkjdsfhajkhajk;dafhkj;dahfjdskahfdjskhfjksahfjksdhjdhfjsdhfdkjshfkjsdhfjdhjfhsdjfhsjdfbjsdbfsdkjbfdksdf"))
    }
    private fun getUserDetails(user:User) : ArrayList<MutableMap<String,String>>{
        return arrayListOf(mutableMapOf("title" to "Biography", "info" to user.bio))
    }
    fun fetchUser(context: Context?):User{
        return User("Orkan", "Akisu", 3.0, "Cmpejdfhkjdsfhajkhajk;dafhkj;dahfjdskahfdjskhfjksahfjksdhjdhfjsdhfdkjshfkjsdhfjdhjfhsdjfhsjdfbjsdbfsdkjbfdksdf")
    }
}