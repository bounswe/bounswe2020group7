package com.cmpe451.platon.page.activity.workspace.fragment.filesystem

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.Folder
import com.google.gson.JsonObject
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
It is a bridge between end point and view model.
 */
class WorkspaceFolderRepository {


    val foldersResourceResponse:MutableLiveData<Resource<Folder>> = MutableLiveData()
    val addUpdateDeleteFolderResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    val addFileToWorkspaceResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val addUpdateDeleteFileResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()



    fun uploadFile(workspaceId: Int, path: RequestBody, fileName:RequestBody, file:RequestBody, token: String){
        addFileToWorkspaceResourceResponse.value =Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.uploadFileToWorkspace(workspaceId, path, fileName, file, token)

        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> addFileToWorkspaceResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null ->{
                        val js = JSONObject(response.errorBody()!!.string())
                        addFileToWorkspaceResourceResponse.value = Resource.Error(if(js.has("err"))  js.getString("err") else "Unknown error!")
                    }
                    else -> addFileToWorkspaceResourceResponse.value = Resource.Error("Unknown error!")
                }
                response.errorBody()?.close()
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })


    }


    fun getFolder(workspaceId: Int, path: String, token: String) {
        foldersResourceResponse.value =Resource.Loading()

        val service = RetrofitClient.getService()
        val call = service.getFolder(workspaceId, path, token)

        call.enqueue(object : Callback<Folder?> {
            override fun onResponse(call: Call<Folder?>, response: Response<Folder?>) {
                when{
                    response.isSuccessful -> foldersResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> foldersResourceResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> foldersResourceResponse.value = Resource.Error("Unknown error!")
                }
                response.errorBody()?.close()
            }
            override fun onFailure(call: Call<Folder?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun addFolder(workspaceId: Int, path: String, folderName: String, token: String) {
        addUpdateDeleteFolderResourceResponse.value =Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.addFolder(workspaceId, path,folderName, token)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> addUpdateDeleteFolderResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addUpdateDeleteFolderResourceResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("err").toString())
                    else -> addUpdateDeleteFolderResourceResponse.value = Resource.Error("Unknown error!")
                }
                response.errorBody()?.close()
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun updateFolderName(workspaceId: Int, path: String, folderName: String, token: String) {
        addUpdateDeleteFolderResourceResponse.value =Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.changeFolderName(workspaceId, path,folderName, token)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> addUpdateDeleteFolderResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addUpdateDeleteFolderResourceResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addUpdateDeleteFolderResourceResponse.value = Resource.Error("Unknown error!")
                }
                response.errorBody()?.close()
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun deleteFolder(workspaceId: Int, path: String, folderName: String, token: String) {
        addUpdateDeleteFolderResourceResponse.value =Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.deleteFolder(workspaceId, path,folderName, token)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> addUpdateDeleteFolderResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addUpdateDeleteFolderResourceResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addUpdateDeleteFolderResourceResponse.value = Resource.Error("Unknown error!")
                }
                response.errorBody()?.close()
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }


    fun deleteFile(workspaceId: Int, cwd: String, file: String, token: String) {
        addUpdateDeleteFileResourceResponse.value =Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.deleteFile(workspaceId, cwd, file, token)
        call.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful -> addUpdateDeleteFileResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> addUpdateDeleteFileResourceResponse.value = Resource.Error(
                        JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addUpdateDeleteFileResourceResponse.value = Resource.Error("Unknown error!")
                }
                response.errorBody()?.close()
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }
}