package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/*
It is a bridge between end point and view model.
 */
class WorkspaceRepository {


    var getWorkspaceResponse: MutableLiveData<Resource<Workspace>> = MutableLiveData()
    var updateResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var deleteResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var getMilestoneResponse: MutableLiveData<Resource<Milestones>> = MutableLiveData()
    var addDeleteUpdateMilestoneResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var applyWorksppaceResourceResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var quitWorkspaceResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val answerWorkspaceApplicationResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val recommendedUsersResourceResponse:MutableLiveData<Resource<RecommendedUserList>> = MutableLiveData()
    val invitationResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
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
    fun updateWorkspace(workspace_id: Int,title: String?, description: String?, private: Int?, maxCollaborators: Int?, deadline: String?, requirements: String?, skills: String?,
                        state:Int?,upcomingEvent:String?, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.updateWorkspace(workspace_id, title, description, private, maxCollaborators,
            deadline, requirements, skills, state,upcomingEvent,authToken)
        updateResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> updateResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> updateResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> updateResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }



    fun deleteWorkspace(workspace_id: Int, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.deleteWorkspace(workspace_id, authToken)
        deleteResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> deleteResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> deleteResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> deleteResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getMilestones(workspace_id: Int, page:Int?, per_page:Int?, token: String){
        val service = RetrofitClient.getService()
        val call = service.getMilestones(workspace_id,page, per_page,token)
        getMilestoneResponse.value = Resource.Loading()
        call.enqueue(object: Callback<Milestones?> {
            override fun onResponse(call: Call<Milestones?>, response: Response<Milestones?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        getMilestoneResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> getMilestoneResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> getMilestoneResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Milestones?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }
    fun addMilestone(workspace_id: Int, title: String, description: String,deadline: String, token: String){
        val service = RetrofitClient.getService()
        val call = service.addMilestone(workspace_id, title, description, deadline,token)
        addDeleteUpdateMilestoneResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> addDeleteUpdateMilestoneResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addDeleteUpdateMilestoneResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addDeleteUpdateMilestoneResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }
    fun updateMilestone( workspace_id: Int,milestone_id:Int, title: String?, description: String?,deadline: String?, token: String){
        val service = RetrofitClient.getService()
        val call = service.updateMilestone(workspace_id, milestone_id,title, description, deadline,token)
        addDeleteUpdateMilestoneResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> addDeleteUpdateMilestoneResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addDeleteUpdateMilestoneResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addDeleteUpdateMilestoneResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }
    fun deleteMilestone(workspace_id: Int, milestone_id: Int, token: String){
        val service = RetrofitClient.getService()
        val call = service.deleteMilestone(workspace_id, milestone_id,token)
        addDeleteUpdateMilestoneResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> addDeleteUpdateMilestoneResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addDeleteUpdateMilestoneResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addDeleteUpdateMilestoneResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun applyToWorkspace(workspaceId: Int,token: String) {
        val service = RetrofitClient.getService()
        val call = service.applyToWorkspace(workspaceId,token)
        applyWorksppaceResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> applyWorksppaceResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> applyWorksppaceResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> applyWorksppaceResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun quitWorkspace(workspaceId: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.quitWorkspace(workspaceId,token)
        quitWorkspaceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> quitWorkspaceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> quitWorkspaceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> quitWorkspaceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }


    val workspaceApplicationsResourceResponse:MutableLiveData<Resource<List<WorkspaceApplication>>> = MutableLiveData()

    fun getWorkspaceApplications(token: String, workspaceId: Int) {
        val service = RetrofitClient.getService()
        val call = service.getWorkspaceApplications(workspaceId,token)
        workspaceApplicationsResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<List<WorkspaceApplication>?> {
            override fun onResponse(call: Call<List<WorkspaceApplication>?>, response: Response<List<WorkspaceApplication>?>) {
                when {
                    response.isSuccessful && response.body() != null -> workspaceApplicationsResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> workspaceApplicationsResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> workspaceApplicationsResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<List<WorkspaceApplication>?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun answerWorkspaceApplication(application_id:Int, is_accepted:Int, token:String){
        val service = RetrofitClient.getService()
        val call = service.answerWorkspaceApplication(application_id, is_accepted,token)
        answerWorkspaceApplicationResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> answerWorkspaceApplicationResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> answerWorkspaceApplicationResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> answerWorkspaceApplicationResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }
    fun getRecommendedCollaborators(workspace_id: Int, number_of_recommendations:Int, token:String){
        val service = RetrofitClient.getService()
        val call = service.getRecommendedCollaborators(workspace_id, number_of_recommendations,token)
        recommendedUsersResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<RecommendedUserList?> {
            override fun onResponse(call: Call<RecommendedUserList?>, response: Response<RecommendedUserList?>) {
                when {
                    response.isSuccessful && response.body() != null -> recommendedUsersResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> recommendedUsersResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> recommendedUsersResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<RecommendedUserList?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }
    fun sendInvitationToWorkspace(wsId: Int, invId: Int, currUserToken: String) {
        val service = RetrofitClient.getService()
        val call = service.inviteToWorkspace(wsId,invId, currUserToken)
        invitationResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        invitationResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> invitationResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> invitationResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    var tagSearchResourceResponse: MutableLiveData<Resource<Search>> = MutableLiveData()


    fun getTagSearch(name: String, page: Int?, perPage: Int?) {
        val service = RetrofitClient.getService()
        val call = service.getTagSearch(1, name, page, perPage)
        tagSearchResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<Search?>{
            override fun onResponse(call: Call<Search?>, response: Response<Search?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        tagSearchResourceResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> tagSearchResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> tagSearchResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Search?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}