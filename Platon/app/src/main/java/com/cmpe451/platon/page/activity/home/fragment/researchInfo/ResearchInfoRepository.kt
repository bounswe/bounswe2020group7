package com.cmpe451.platon.page.activity.home.fragment.researchInfo

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResearchInfoRepository() {

    var addResearchResourceResponse : MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var editResearchResourceResponse : MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var deleteResearchResourceResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()


    fun addResearch(title:String,description:String?,
                 year:Int,authToken: String){

        addResearchResourceResponse.value =Resource.Loading()

        val service = RetrofitClient.getService()
        val call = service.addResearchProject(title, description, year, authToken)

        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> addResearchResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addResearchResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addResearchResourceResponse.value = Resource.Error("Unknown error!")
                }

                response.errorBody()?.close()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun editResearch(projectId:Int, title: String, description: String?, year: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.editResearchProject(projectId, title, description, year, authToken)
        editResearchResourceResponse.value =Resource.Loading()
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> editResearchResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> editResearchResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> editResearchResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


    fun deleteResearch(researchId:Int, authToken:String){
        val service = RetrofitClient.getService()

        val call = service.deleteResearchProject(researchId, authToken)
        deleteResearchResourceResponse.value =Resource.Loading()
        call.enqueue(object : Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> deleteResearchResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> deleteResearchResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> deleteResearchResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })



    }


}