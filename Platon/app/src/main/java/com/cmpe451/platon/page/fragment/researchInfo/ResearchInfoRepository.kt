package com.cmpe451.platon.page.fragment.researchInfo

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.util.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResearchInfoRepository() {

    var responseCodeAdd : MutableLiveData<String> = MutableLiveData()
    var responseCodeEdit : MutableLiveData<String> = MutableLiveData()

    fun addResearch(title:String,description:String?,
                 year:Int,authToken: String){


        val service = RetrofitClient.getService()
        val call = service.addResearchProject(title, description, year, authToken)!!

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                responseCodeAdd.value = response.message()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })
    }

    fun editResearch(projectId:Int, title: String, description: String?, year: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.editResearchProject(projectId, title, description, year, authToken)!!

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                responseCodeEdit.value = response.message()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

            }

        })
    }

}