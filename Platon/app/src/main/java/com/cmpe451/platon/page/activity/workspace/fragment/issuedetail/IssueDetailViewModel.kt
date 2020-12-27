package com.cmpe451.platon.page.activity.workspace.fragment.issuedetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.IssueAssignee
import retrofit2.http.Field
import retrofit2.http.Header


class IssueDetailViewModel: ViewModel() {

    val repository = IssueDetailRepository()

    var assigneeResponse: MutableLiveData<Resource<IssueAssignee>>
    //var addIssuesResourceResponse = repository.addIssuesResourceResponse
    var deleteIssueResponse = repository.deleteIssueResponse
    var getWorkspaceResponse = repository.getWorkspaceResponse


    init {
        assigneeResponse = repository.getIssueAssignee
        deleteIssueResponse = repository.deleteIssueResponse
        //addIssuesResourceResponse = repository.addIssuesResourceResponse

    }

    fun getIssueAssignee(workSpaceId: Int,issueId: Int, page: Int?, paginationSize: Int?, authToken: String) {
        if(authToken != null){
            repository.getIssueAssignee(workSpaceId, issueId, page, paginationSize, authToken)
        }
    }

    fun fetchWorkspace(workspace_id:Int, token:String){
        repository.fetchWorkspace(workspace_id, token)
    }

    fun deleteIssue(workspaceId:Int, issueId:Int, auth_token :String){
        if(auth_token != null){
            repository.deleteIssue(workspaceId, issueId, auth_token)
        }
    }

}