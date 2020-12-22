package com.cmpe451.platon.page.activity.workspace.fragment.editworkspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.Workspace
import com.cmpe451.platon.network.models.WorkspaceListItem
import com.cmpe451.platon.network.models.WorkspaceListItems
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class EditWorkspaceRepository {

    var updateResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var deleteResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    fun updateWorkspace(workspace_id: Int,title: String?, description: String?, private: Int?, maxCollaborators: Int?, deadline: String?, requirements: String?, skills: String?,state:Int?, authToken: String) {
        val service = RetrofitClient.getService()
        val call = service.updateWorkspace(workspace_id, title, description, private, maxCollaborators,
            deadline, requirements, skills, state,authToken)
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

}