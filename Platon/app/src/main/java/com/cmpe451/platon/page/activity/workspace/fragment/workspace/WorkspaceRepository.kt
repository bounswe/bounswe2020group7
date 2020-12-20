package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.Workspace
import com.cmpe451.platon.network.models.WorkspaceListItem
import com.cmpe451.platon.network.models.WorkspaceListItems
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class WorkspaceRepository {


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
}