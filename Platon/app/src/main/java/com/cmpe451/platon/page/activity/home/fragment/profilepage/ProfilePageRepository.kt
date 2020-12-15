package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {

    val researchesResourceResponse: MutableLiveData<Resource<Researches>> = MutableLiveData()
    val allSkills:MutableLiveData<Resource<List<String>>> = MutableLiveData()
    val userSkills:MutableLiveData<Resource<Skills>> = MutableLiveData()
    val addDeleteSkillResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()


    fun getResearches( userId: Int, authToken: String){
        val service = RetrofitClient.getService()
        val call = service.getResearches(userId, authToken)
        researchesResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<Researches?>{
            override fun onResponse(call: Call<Researches?>, response: Response<Researches?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        researchesResourceResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> researchesResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> researchesResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Researches?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getAllSkills() {
        val service = RetrofitClient.getService()
        val call = service.getAllSkills()
        allSkills.value = Resource.Loading()
        call.enqueue(object: Callback<List<String>?>{
            override fun onResponse(call: Call<List<String>?>, response: Response<List<String>?>) {
                when {
                    response.isSuccessful && response.body() != null -> allSkills.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> allSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> allSkills.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<List<String>?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getUserSkills(userId: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.getUserSkills(userId, token)
        userSkills.value = Resource.Loading()
        call.enqueue(object: Callback<Skills?>{
            override fun onResponse(call: Call<Skills?>, response: Response<Skills?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        userSkills.value = Resource.Success(response.body()!!)
                        var o = 5
                    }
                    response.errorBody() != null -> userSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userSkills.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Skills?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun addSkillToUser(skill: String, token: String) {
        val service = RetrofitClient.getService()
        val call = service.addSkillToUser(skill, token)
        addDeleteSkillResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> addDeleteSkillResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null ->  {
                        if (!JSONObject(response.errorBody()!!.string()).isNull("error")){
                            addDeleteSkillResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).getString("error"))
                        }

                        else addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                    }
                    else -> addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun deleteSkillFromUser(s: String, token: String) {
        val service = RetrofitClient.getService()
        val call = service.deleteSkillFromUser(s, token)
        addDeleteSkillResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> addDeleteSkillResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null ->  {
                        Log.i("ERORR",response.errorBody()!!.string().toString())
                        val obj = JSONObject(response.errorBody()!!.string().toString())
                        if (obj.has("error"))
                            addDeleteSkillResourceResponse.value = Resource.Error(obj.getString("error"))
                        else addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                    }
                    else -> addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}