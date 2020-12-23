package com.cmpe451.platon.page.activity.workspace.fragment.issues

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Query
import java.sql.SQLTransactionRollbackException

class IssuesRepository {

    val issuesResponse:MutableLiveData<Resource<Issues>> = MutableLiveData()

    fun getIssues(workSpaceId: Int, page: Int, paginationSize: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.getIssues(workSpaceId, page, paginationSize, authToken)

        issuesResponse.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<Issues?> {
            override fun onResponse(call: Call<Issues?>, response: Response<Issues?>) {
                print("ert")
                when {
                    response.isSuccessful -> issuesResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> issuesResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->issuesResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<Issues?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}