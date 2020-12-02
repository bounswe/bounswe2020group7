package com.cmpe451.platon.page.fragment.profilepage.model

import android.content.Context
import com.cmpe451.platon.networkmodels.models.OtherUser
import com.cmpe451.platon.networkmodels.models.Researches
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {


    //val getUsers = userDao.getUsers()
    //val getResearches = userResearchesDao.getResearchesOfUser()

    fun fetchFollowers(context: Context?) : ArrayList<OtherUser>{
        return arrayListOf(
                OtherUser(1 ,"email1","Oyku", "Yilmaz", "CMPE",null, null, 3.0, null),
            OtherUser(2, "email21","Burak", "Omur","CMPE" ,null, null,2.0,null),
            OtherUser(3,"email3","Ertugrul", "Bulbul","CMPE", null, null,1.0,null)
        )
    }
    fun fetchProfilePageDetails(context: Context?) : ArrayList<MutableMap<String,String>>{
        return getUserDetails(User(4,"gmailorkan","Orkan", "Akisu", "cmpe", "Cmpejdfhkjdsfdkjhjfhsdjfhsjdfbjsdbfsdkjbfdksdf", null, 2.0, null))
    }
    private fun getUserDetails(user:User) : ArrayList<MutableMap<String,String>>{
        return arrayListOf(mutableMapOf("title" to "Biography", "info" to user.job))
    }
    fun fetchUser(context: Context?):User{
        return User(4,"gmailorkan","Orkan", "Akisu", "cmpe", "Cmpejdfhkjdsfdkjhjfhsdjfhsjdfbjsdbfsdkjbfdksdf", null, 2.0, null)
    }

    fun getFollowers(followingId: Int, authToken: String){}

    fun getResearches( userId: Int, authToken: String){

        val service = RetrofitClient.getService()

        val call = service.getResearches(userId, authToken)!!

        call.enqueue(object: Callback<JsonObject>{
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }

        })
    }

    fun getUser(authToken: String){

        val service = RetrofitClient.getService()

        val call = service.getUserInfo(authToken)!!

        call.enqueue(object: Callback<JsonObject>{
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }

        })

    }
}