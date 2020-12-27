package com.cmpe451.platon.page.activity.home.fragment.otherprofile

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.OtherUser
import com.cmpe451.platon.network.models.Researches
import com.cmpe451.platon.network.RetrofitClient
import com.cmpe451.platon.network.models.AllComments
import com.cmpe451.platon.network.models.Skills
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtherProfileRepository() {

    val userResource: MutableLiveData<Resource<OtherUser>> = MutableLiveData()
    val researchesResource:MutableLiveData<Resource<Researches>> = MutableLiveData()
    var followResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    var unFollowResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()
    val userSkills:MutableLiveData<Resource<Skills>> = MutableLiveData()
    val userComments:MutableLiveData<Resource<AllComments>> = MutableLiveData()
    val invitationResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()


    val addDeleteCommentResourceResponse:MutableLiveData<Resource<JsonObject>> = MutableLiveData()

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
                    response.errorBody() != null -> userComments.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userComments.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<AllComments?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getUser(userId:Int, token:String) {
        val service = RetrofitClient.getService()
        val call = service.getOtherUserInfo(userId, token)
        userResource.value = Resource.Loading()
        call.enqueue(object : Callback<OtherUser?> {
            override fun onResponse(call: Call<OtherUser?>, response: Response<OtherUser?>) {
                when{
                    response.isSuccessful && response.body() != null -> userResource.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> userResource.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> userResource.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<OtherUser?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun getResearch(token: String, userId: Int, page:Int?, perPage:Int?) {
        researchesResource.value = Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.getResearches(userId, token, page, perPage)

        call.enqueue(object: Callback<Researches?>{
            override fun onResponse(call: Call<Researches?>, response: Response<Researches?>) {
                when {
                    response.isSuccessful && response.body() != null -> researchesResource.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> researchesResource.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> researchesResource.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<Researches?>, t: Throwable) {
                call.clone().enqueue(this)
            }
        })
    }

    fun follow(followerId: Int, followingId: Int, authToken: String) {
        followResourceResponse.value = Resource.Loading()
        val service = RetrofitClient.getService()
        val call = service.follow(followerId, followingId, authToken)

        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> followResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> followResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> followResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun unfollow(followingId: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.unfollow(followingId, token)
        unFollowResourceResponse.value = Resource.Loading()

        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> unFollowResourceResponse.value = Resource.Success(response.body()!!)
                    response.errorBody() != null -> unFollowResourceResponse.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> unFollowResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
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

    fun addComment(rating: Int, comment: String?, id: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.addComment(id, rating, comment , token)
        addDeleteCommentResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        addDeleteCommentResourceResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> userSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addDeleteCommentResourceResponse.value = Resource.Error("Unknown error!")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }

    fun deleteComment(id: Int, token: String) {
        val service = RetrofitClient.getService()
        val call = service.deleteComment(id, token)
        addDeleteCommentResourceResponse.value = Resource.Loading()
        call.enqueue(object: Callback<JsonObject?>{
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        addDeleteCommentResourceResponse.value = Resource.Success(response.body()!!)
                    }
                    response.errorBody() != null -> userSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> addDeleteCommentResourceResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
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
                    response.errorBody() != null -> userSkills.value = Resource.Error(JSONObject(response.errorBody()!!.string()).get("error").toString())
                    else -> invitationResponse.value = Resource.Error("Unknown error!")
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                call.clone().enqueue(this)
            }

        })
    }


}