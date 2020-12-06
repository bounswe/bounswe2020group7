package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Research
import com.cmpe451.platon.network.models.Researches
import com.cmpe451.platon.network.models.User
import com.cmpe451.platon.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {

    val researchesResourceResponse: MutableLiveData<Resource<Researches>> = MutableLiveData(Resource.Loading())
    val userResourceResponse:MutableLiveData<Resource<User>> = MutableLiveData(Resource.Loading())


    fun getUser(token:String){
        val service = RetrofitClient.getService()
        val call = service.getUserInfo(token)
        call.enqueue(object :Callback<User?>{
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                when{
                    response.isSuccessful && response.body() != null -> userResourceResponse.value =  Resource.Success(response.body()!!)
                    response.errorBody() != null -> userResourceResponse.value =  Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userResourceResponse.value =  Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                    call.clone().enqueue(this)
            }
        })

    }


    fun getResearches( userId: Int, authToken: String){
        val service = RetrofitClient.getService()
        val call = service.getResearches(userId, authToken)

        call.enqueue(object: Callback<Researches?>{
            override fun onResponse(call: Call<Researches?>, response: Response<Researches?>) {
                when {
                    response.isSuccessful && response.body() != null -> researchesResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> researchesResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> researchesResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Researches?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}