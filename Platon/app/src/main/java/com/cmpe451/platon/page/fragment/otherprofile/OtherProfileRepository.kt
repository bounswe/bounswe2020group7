package com.cmpe451.platon.page.fragment.otherprofile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.networkmodels.models.OtherUser
import com.cmpe451.platon.networkmodels.models.Research
import com.cmpe451.platon.networkmodels.models.Researches
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtherProfileRepository() {
    val currentUser: MutableLiveData<OtherUser> = MutableLiveData()
    val currentResearch:MutableLiveData<List<Research>> = MutableLiveData<List<Research>>(null)
    var followResponse:MutableLiveData<Pair<Int, String>> = MutableLiveData()
    fun getUser(userId:Int, token:String) {
        val service = RetrofitClient.getService()
        val call = service.getOtherUserInfo(userId, token)!!
        call.enqueue(object : Callback<OtherUser> {
            override fun onResponse(call: Call<OtherUser>, response: Response<OtherUser>) {
                Log.i("Home Repo", response.code().toString())
                if(response.code() == 200){
                    currentUser.value =  response.body()
                    var o = 5
                }
                else{
                    call.clone().enqueue(this)
                }
            }

            override fun onFailure(call: Call<OtherUser>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getResearch(token: String, userId: Int) {
        val service = RetrofitClient.getService()

        val call = service.getResearches(userId, token)!!

        call.enqueue(object: Callback<Researches>{
            override fun onResponse(
                call: Call<Researches>, response: Response<Researches>
            ) {
                val rs = response.body()
                currentResearch.value = rs?.research_info
            }

            override fun onFailure(call: Call<Researches>, t: Throwable) {
            }

        })
    }

    fun follow(followerId: Int, followingId: Int, authToken: String) {
        val service = RetrofitClient.getService()

        val call = service.follow(followerId, followingId, authToken)!!

        call.enqueue(object: Callback<JsonObject>{
            override fun onResponse(
                call: Call<JsonObject>, response: Response<JsonObject>
            ) {
                when {
                    response.isSuccessful ->  followResponse.value = Pair(response.code(), "Successful")
                    (response.errorBody() != null) -> followResponse.value = Pair(response.code(),
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> followResponse.value = Pair(response.code(),"Unknown error!")

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            }

        })
    }


}