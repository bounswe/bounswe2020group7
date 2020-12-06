package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.networkmodels.models.Research
import com.cmpe451.platon.networkmodels.models.Researches
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.util.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {

    val getResearches: MutableLiveData<List<Research>> = MutableLiveData<List<Research>>(null)
    val getUser:MutableLiveData<User> = MutableLiveData<User>(null)


    fun getUser(token:String){
        val service = RetrofitClient.getService()
        val call = service.getUserInfo(token)!!
        call.enqueue(object :Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                when{
                    response.isSuccessful -> getUser.value =  response.body()
                    else->  call.clone().enqueue(this)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                    call.clone().enqueue(this)
            }
        })

    }


    fun getResearches( userId: Int, authToken: String){
        val service = RetrofitClient.getService()
        val call = service.getResearches(userId, authToken)!!

        call.enqueue(object: Callback<Researches>{
            override fun onResponse(call: Call<Researches>, response: Response<Researches>) {
                when{
                    response.isSuccessful -> getResearches.value = response.body()?.research_info
                    else -> call.clone().enqueue(this)
                }
            }

            override fun onFailure(call: Call<Researches>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}