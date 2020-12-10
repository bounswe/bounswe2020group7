package com.cmpe451.platon.page.fragment.editprofile

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileRepository() {

    var editProfileResourceResponse : MutableLiveData<Resource<JsonObject>> = MutableLiveData(Resource.Loading(JsonObject()))

    fun editUser(name:String?,surname:String?,
                 job:String?, isPrivate:Boolean?,
                 profilePhoto:String?,
                 google_scholar_name:String?,
                 researchgate_name:String?,authToken: String){


        val service = RetrofitClient.getService()
        val call = service.editUserInfo(name, surname, job, true, isPrivate, profilePhoto, google_scholar_name, researchgate_name, authToken)

        call.enqueue(object :Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful && response.body() != null -> editProfileResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> editProfileResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> editProfileResourceResponse.value = Resource.Error("Unknown Error")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

}