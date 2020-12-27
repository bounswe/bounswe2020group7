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
    var addIssueAssigneeResponse = repository.addIssueAssigneeResponse
    var deleteIssueAssigneeResponse = repository.addIssueAssigneeResponse
    var deleteIssueResponse = repository.deleteIssueResponse
    var getWorkspaceResponse = repository.getWorkspaceResponse
    var editIssueResponse = repository.editIssueResponse


    init {
        assigneeResponse = repository.getIssueAssignee
        deleteIssueResponse = repository.deleteIssueResponse
        editIssueResponse = repository.editIssueResponse
        addIssueAssigneeResponse = repository.addIssueAssigneeResponse
        deleteIssueAssigneeResponse = repository.deleteIssueAssigneeResponse

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

    fun editIssue(workspaceId:Int, issueId:Int, title:String?, description:String?, deadline:String?, authToken:String?) {
        if(authToken != null){
            repository.editIssue(workspaceId, issueId, title, description, deadline, authToken)
        }
    }

    fun addIssueAssignee(workspaceId:Int, issueId:Int, assigneeId: Int, authToken:String?) {
        if(authToken != null){
            repository.addIssueAssignee(workspaceId, issueId, assigneeId, authToken)
        }
    }

    fun deleteIssueAssignee(workspaceId:Int, issueId:Int, assigneeId: Int, authToken:String?) {
        if(authToken != null){
            repository.deleteIssueAssignee(workspaceId, issueId, assigneeId, authToken)
        }
    }

}