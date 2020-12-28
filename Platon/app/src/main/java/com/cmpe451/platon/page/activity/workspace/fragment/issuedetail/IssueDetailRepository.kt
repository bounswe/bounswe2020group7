package com.cmpe451.platon.page.activity.workspace.fragment.issuedetail

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.Header

class IssueDetailRepository {

    val deleteIssueResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val deleteIssueAssigneeResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val addIssueAssigneeResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val getCommentsResponse:MutableLiveData<Resource<IssueAllComments>> = MutableLiveData()
    val deleteIssueCommentResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val addIssueCommentResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val editIssueResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val getIssueAssignee:MutableLiveData<Resource<IssueAssignee>> = MutableLiveData()
    var getWorkspaceResponse: MutableLiveData<Resource<Workspace>> = MutableLiveData()

    fun fetchWorkspace(workspace_id:Int, token:String) {
        val service = RetrofitClient.getService()
        val call = service.getWorkspace(workspace_id,token)
        getWorkspaceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<Workspace?> {
            override fun onResponse(call: Call<Workspace?>, response: Response<Workspace?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        getWorkspaceResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> getWorkspaceResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> getWorkspaceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Workspace?>, t: Throwable) {
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

    fun editIssue(workspaceId:Int, issueId:Int, title:String?, description:String?, deadline:String?, authToken:String?) {
        val service = RetrofitClient.getService()
        val call = service.editIssue(workspaceId, issueId, title, description, deadline, authToken)

        editIssueResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> editIssueResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> editIssueResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> editIssueResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getIssueAssignee(workSpaceId: Int, issueId: Int, page: Int?, paginationSize: Int?, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.getIssueAssignee(workSpaceId, issueId , page, paginationSize, authToken)

        getIssueAssignee.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<IssueAssignee?> {
            override fun onResponse(call: Call<IssueAssignee?>, response: Response<IssueAssignee?>) {
                when {
                    response.isSuccessful -> getIssueAssignee.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> getIssueAssignee.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->getIssueAssignee.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<IssueAssignee?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun addIssueAssignee(workSpaceId: Int, issueId: Int, assigneeId: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.addIssueAssignee(workSpaceId, issueId , assigneeId, authToken)

        addIssueAssigneeResponse.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful -> addIssueAssigneeResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addIssueAssigneeResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->addIssueAssigneeResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun deleteIssueAssignee(workSpaceId: Int, issueId: Int, assigneeId: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.deleteIssueAssignee(workSpaceId, issueId , assigneeId, authToken)

        deleteIssueAssigneeResponse.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful -> deleteIssueAssigneeResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> deleteIssueAssigneeResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->deleteIssueAssigneeResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getIssueComments(workSpaceId: Int, issueId: Int, page: Int?, paginationSize: Int?, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.getIssueComments(workSpaceId, issueId , page, paginationSize, authToken)

        getCommentsResponse.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<IssueAllComments?> {
            override fun onResponse(call: Call<IssueAllComments?>, response: Response<IssueAllComments?>) {
                when {
                    response.isSuccessful -> getCommentsResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> getCommentsResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->getCommentsResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<IssueAllComments?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun deleteIssueComment(workSpaceId: Int, issueId: Int, commentId: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.deleteIssueComment(workSpaceId, issueId , commentId, authToken)

        deleteIssueCommentResponse.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful -> deleteIssueCommentResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> deleteIssueCommentResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->deleteIssueCommentResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun addIssueComments(workSpaceId: Int, issueId: Int, page: Int?, paginationSize: Int?, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.getIssueComments(workSpaceId, issueId , page, paginationSize, authToken)

        getCommentsResponse.value = Resource.Loading()
        //nullable check
        call.enqueue(object : Callback<IssueAllComments?> {
            override fun onResponse(call: Call<IssueAllComments?>, response: Response<IssueAllComments?>) {
                when {
                    response.isSuccessful -> getCommentsResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> getCommentsResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else ->getCommentsResponse.value =  Resource.Error("Unknown error")
                }
            }

            override fun onFailure(call: Call<IssueAllComments?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}