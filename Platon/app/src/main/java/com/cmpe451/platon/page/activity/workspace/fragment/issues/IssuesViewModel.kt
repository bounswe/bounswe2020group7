package com.cmpe451.platon.page.activity.workspace.fragment.issues

import com.cmpe451.platon.page.activity.home.HomeActivityRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.google.gson.JsonObject

class IssuesViewModel: ViewModel(){

    var issuesResponse: MutableLiveData<Resource<Issues>>

    val repository = IssuesRepository()

    init {
        issuesResponse = repository.issuesResponse

    }

    fun getIssues(workSpaceId: Int, page: Int, paginationSize: Int, authToken: String) {
        repository.getIssues(workSpaceId, page, paginationSize, authToken)
    }




}