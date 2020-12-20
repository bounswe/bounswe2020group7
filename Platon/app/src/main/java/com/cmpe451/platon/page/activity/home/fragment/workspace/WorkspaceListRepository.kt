package com.cmpe451.platon.page.activity.home.fragment.workspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.WorkspaceListItems
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkspaceListRepository() {

    var workspaces : MutableLiveData<Resource<WorkspaceListItems>> = MutableLiveData()

    fun getWorkspaces(token: String) {
        val service = RetrofitClient.getService()
        val call = service.getPersonalWorkspacesList(token)
        workspaces.value = Resource.Loading()
        call.enqueue(object: Callback<WorkspaceListItems?> {
            override fun onResponse(call: Call<WorkspaceListItems?>, response: Response<WorkspaceListItems?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        workspaces.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> workspaces.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> workspaces.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<WorkspaceListItems?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}