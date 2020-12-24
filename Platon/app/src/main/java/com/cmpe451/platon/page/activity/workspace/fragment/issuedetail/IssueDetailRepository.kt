package com.cmpe451.platon.page.activity.workspace.fragment.issuedetail

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.Comments
import com.cmpe451.platon.network.models.IssueComment
import com.cmpe451.platon.network.models.Issues
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IssueDetailRepository {

    val deleteIssueResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val deleteIssueAssigneeResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val addIssueAssigneeResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val commentsResponse:MutableLiveData<Resource<IssueComment>> = MutableLiveData()



    fun getIssuesComment(workSpaceId: Int,issueId: Int, page: Int, paginationSize: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.getIssueComments(workSpaceId, issueId , page, paginationSize, authToken)

        commentsResponse.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<IssueComment?> {
            override fun onResponse(call: Call<IssueComment?>, response: Response<IssueComment?>) {
                print("ert")
                when {
                    response.isSuccessful -> commentsResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> commentsResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->commentsResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<IssueComment?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun deleteIssue(workspace_id: Int, issue_id: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.deleteIssue(workspace_id, issue_id, authToken)

        deleteIssueResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> deleteIssueResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> deleteIssueResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> deleteIssueResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

}