package com.cmpe451.platon.page.fragment.profilepage.model

import android.content.Context
import android.content.SharedPreferences
import com.cmpe451.platon.util.Definitions.User

class ProfilePageRepository (sharedPreferences: SharedPreferences){
    fun fetchFollowers(context: Context?) : ArrayList<User>{
        return arrayListOf(
                User(1,"Oyku", "Yilmaz", 3.0, "Cmpe"),
                User(2,"Burak", "Omur", 5.0, "Cmpe"),
                User(3,"Ertugrul", "Bulbul", 4.0, "Cmpe")
        )
    }
    fun fetchFollowing(context: Context?) : ArrayList<User>{
        return arrayListOf(
                User(1,"Oyku", "Yilmaz", 3.0, "Cmpe"),
                User(2,"Burak", "Omur", 5.0, "Cmpe"),
                User(3,"Ertugrul", "Bulbul", 4.0, "Cmpe")
        )
    }
    fun fetchProfilePageDetails(context: Context?) : ArrayList<MutableMap<String,String>>{
        return getUserDetails(User(4,"Orkan", "Akisu", 3.0, "Cmpejdfhkjdsfhajkhajk;dafhkj;dahfjdskahfdjskhfjksahfjksdhjdhfjsdhfdkjshfkjsdhfjdhjfhsdjfhsjdfbjsdbfsdkjbfdksdf"))
    }
    private fun getUserDetails(user:User) : ArrayList<MutableMap<String,String>>{
        return arrayListOf(mutableMapOf("title" to "Biography", "info" to user.bio))
    }
    fun fetchUser(context: Context?):User{
        return User(4,"Orkan", "Akisu", 4.0, "Cmpejdfhkjdsfhajkhajk;dafhkj;dahfjdskahfdjskhfjksahfjksdhjdhfjsdhfdkjshfkjsdhfjdhjfhsdjfhsjdfbjsdbfsdkjbfdksdf")
    }
}