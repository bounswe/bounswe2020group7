package com.cmpe451.platon.page.activity.workspace.fragment.addworkspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddWorkspaceRepository {
    val allSkills: MutableLiveData<Resource<List<String>>> = MutableLiveData()
    fun getAllSkills() {
        val service = RetrofitClient.getService()
        val call = service.getAllSkills()
        allSkills.value = Resource.Loading()
        call.enqueue(object: Callback<List<String>?> {
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
}