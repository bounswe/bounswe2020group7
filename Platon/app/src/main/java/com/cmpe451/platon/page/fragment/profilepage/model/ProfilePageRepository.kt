package com.cmpe451.platon.page.fragment.profilepage.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.networkmodels.models.OtherUser
import com.cmpe451.platon.networkmodels.models.Research
import com.cmpe451.platon.networkmodels.models.Researches
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {

    val getResearches: MutableLiveData<List<Research>> = MutableLiveData<List<Research>>(null)
    val getUser:MutableLiveData<User> = MutableLiveData<User>(null)


    private fun getUserDetails(user:User) : ArrayList<MutableMap<String,String>>{
        return arrayListOf(mutableMapOf("title" to "Biography", "info" to user.job))
    }


    fun getUser(token:String){
        val service = RetrofitClient.getService()
        val call = service.getUserInfo(token)!!
        call.enqueue(object :Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.i("Home Repo", response.code().toString())
                if(response.code() == 200){
                    getUser.value =  response.body()
                }
                else{
                    call.clone().enqueue(this)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                    call.clone().enqueue(this)
            }

        })

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


}