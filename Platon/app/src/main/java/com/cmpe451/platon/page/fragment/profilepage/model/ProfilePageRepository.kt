package com.cmpe451.platon.page.fragment.profilepage.model

import android.content.Context
import android.content.SharedPreferences
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.util.Definitions.User
import com.cmpe451.platon.util.RetrofitClient
import com.cmpe451.platon.util.Webservice
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field

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

    fun getFollowers(followingId: Int, authToken: String, callback: HttpRequestListener){

        var client: Webservice = RetrofitClient.webservice

        client.getFollowers(followingId, authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                //handle error here
                val er = 0
            }
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                //your raw string response

                val stringResponse = (response.body() as JsonObject).toString()
                callback.onRequestCompleted(stringResponse)
                val ert = 5
            }
        })
    }
//    fun editProfile(name:String?, surname:String?,job:String?, isPrivate:Boolean?,profilePhoto:String?,
//                    google_scholar_name:String?,researchgate_name:String?,auth_token :String){
//        var client: Webservice = RetrofitClient.webservice
//        client.editUserInfo(name,surname,job, isPrivate,profilePhoto,google_scholar_name,researchgate_name,auth_token)?.enqueue(object : Callback<JsonObject> {
//            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                //handle error here
//                val er = 0
//            }
//            override fun onResponse(
//                call: Call<JsonObject>,
//                response: Response<JsonObject>
//            ) {
//                //your raw string response
//
//                val stringResponse = (response.body() as JsonObject).toString()
//                callback.onRequestCompleted(stringResponse)
//                val ert = 5
//            }
//        })
//
//    }
//    fun getFollowing(followingId: Int, authToken: String, callback: HttpRequestListener){
//
//        var client: Webservice = RetrofitClient.webservice
//
//        client.getFollowing(followingId, authToken)?.enqueue(object : Callback<JsonObject> {
//            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                //handle error here
//                val er = 0
//            }
//
//            override fun onResponse(
//                call: Call<JsonObject>,
//                response: Response<JsonObject>
//            ) {
//                //your raw string response
//
//                val stringResponse = (response.body() as JsonObject).toString()
//                callback.onRequestCompleted(stringResponse)
//                val ert = 5
//            }
//
//        })
//    }
    fun getResearches(userId: Int, authToken: String, callback: HttpRequestListener){

        var client: Webservice = RetrofitClient.webservice

        client.getResearches(userId, authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                //handle error here
                val er = 0
            }

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                //your raw string response

                val stringResponse = response.body().toString()
                callback.onRequestCompleted(stringResponse)
                val ert = 5
            }

        })
    }

    fun getUser(authToken: String, callback: HttpRequestListener){

        var client: Webservice = RetrofitClient.webservice

        client.getUserInfo(authToken)?.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                //handle error here
                val er = 0
            }

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                //your raw string response
                val stringResponse = response.body().toString()
                callback.onRequestCompleted(stringResponse)
            }

        })
    }
}