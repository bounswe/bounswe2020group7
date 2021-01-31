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

/*
It is a bridge between repository and fragment.
 */
class IssuesViewModel: ViewModel(){

    val repository = IssuesRepository()

    var issuesResponse: MutableLiveData<Resource<Issues>>
    var addIssuesResourceResponse = repository.addIssuesResourceResponse



    init {
        issuesResponse = repository.issuesResponse
        addIssuesResourceResponse = repository.addIssuesResourceResponse

    }

    fun getIssues(workSpaceId: Int, page: Int, paginationSize: Int, authToken: String) {
        repository.getIssues(workSpaceId, page, paginationSize, authToken)
    }

    fun addIssues(workSpaceId: Int, title: String, description: String,deadline: String, authToken: String) {
        repository.addIssues(workSpaceId, title, description, deadline, authToken)
    }




}