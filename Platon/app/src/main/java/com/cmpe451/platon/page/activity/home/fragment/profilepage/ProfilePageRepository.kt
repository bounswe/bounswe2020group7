package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePageRepository() {

    val researchesResourceResponse: MutableLiveData<Resource<Researches>> = MutableLiveData()
    val allSkills:MutableLiveData<Resource<List<String>>> = MutableLiveData()
    val userSkills:MutableLiveData<Resource<Skills>> = MutableLiveData()
    val addDeleteSkillResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val uploadPhotoResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    var editProfileResourceResponse : MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    var addResearchResourceResponse : MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var editResearchResourceResponse : MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var deleteResearchResourceResponse: MutableLiveData<Resource<JsonObject>> = MutableLiveData()

    val userComments:MutableLiveData<Resource<AllComments>> = MutableLiveData()




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


    fun editUser(name:String?,surname:String?,
                 job:String?, institution:String?, isPrivate:Boolean?,
                 google_scholar_name:String?,
                 researchgate_name:String?,authToken: String){



        editProfileResourceResponse.value = Resource.Loading()
        val service = RetrofitClient.getService()
        var privacy:Int? = null
        if(isPrivate != null){
            privacy = if (isPrivate) 1 else 0
        }

        val call = service.editUserInfo(name, surname, job, institution,  privacy, google_scholar_name, researchgate_name, authToken)

        call.enqueue(object :Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when{
                    response.isSuccessful && response.body() != null -> editProfileResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> editProfileResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> editProfileResourceResponse.value = Resource.Error("Unknown Error")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getResearches( userId: Int, authToken: String, page:Int?, perPage:Int?){
        val service = RetrofitClient.getService()
        val call = service.getResearches(userId, authToken, page, perPage)
        researchesResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<Researches?>{
            override fun onResponse(call: Call<Researches?>, response: Response<Researches?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        researchesResourceResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> researchesResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> researchesResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Researches?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getAllSkills() {
        val service = RetrofitClient.getService()
        val call = service.getAllSkills()
        allSkills.value = Resource.Loading()
        call.enqueue(object: Callback<List<String>?>{
            override fun onResponse(call: Call<List<String>?>, response: Response<List<String>?>) {
                when {
                    response.isSuccessful && response.body() != null -> allSkills.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> allSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> allSkills.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<List<String>?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getUserSkills(userId: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.getUserSkills(userId, token)
        userSkills.value = Resource.Loading()
        call.enqueue(object: Callback<Skills?>{
            override fun onResponse(call: Call<Skills?>, response: Response<Skills?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        userSkills.value = Resource.Success(response.body()!!)
                        var o = 5
                    }
                    response.errorBody() != null -> userSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userSkills.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Skills?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun addSkillToUser(skill: String, token: String) {
        val service = RetrofitClient.getService()
        val call = service.addSkillToUser(skill, token)
        addDeleteSkillResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> addDeleteSkillResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null ->  {
                        if (!JSONObject(response.errorBody()!!.string()).isNull("error")){
                            addDeleteSkillResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).getString("error"))
                        }

                        else addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                    }
                    else -> addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun deleteSkillFromUser(s: String, token: String) {
        val service = RetrofitClient.getService()
        val call = service.deleteSkillFromUser(s, token)
        addDeleteSkillResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> addDeleteSkillResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null ->  {
                        Log.i("ERORR",response.errorBody()!!.string())
                        val obj = JSONObject(response.errorBody()!!.string())
                        if (obj.has("error"))
                            addDeleteSkillResourceResponse.value = Resource.Error(obj.getString("error"))
                        else addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                    }
                    else -> addDeleteSkillResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun uploadPhoto(fBody: RequestBody, token: String) {
        val service = RetrofitClient.getService()
        val call = service.uploadPhoto(fBody, token)
        uploadPhotoResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> uploadPhotoResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null ->  {
                        Log.i("ERORR",response.errorBody()!!.string())
                        val obj = JSONObject(response.errorBody()!!.string())
                        if (obj.has("error"))
                            uploadPhotoResourceResponse.value = Resource.Error(obj.getString("error"))
                        else uploadPhotoResourceResponse.value = Resource.Error("Unknown error!")
                    }
                    else -> uploadPhotoResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getComments(id: Int, token: String, page: Int, pageSize: Int) {
        val service = RetrofitClient.getService()
        val call = service.getComments(id, page, pageSize, token)
        userComments.value = Resource.Loading()
        call.enqueue(object: Callback<AllComments?>{
            override fun onResponse(call: Call<AllComments?>, response: Response<AllComments?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        userComments.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> userSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userComments.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<AllComments?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


    var tagSearchResourceResponse: MutableLiveData<Resource<Search>> = MutableLiveData()


    fun getTagSearchUser(name: String, page: Int?, perPage: Int?) {
        val service = RetrofitClient.getService()
        val call = service.getTagSearch(0, name, page, perPage)
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