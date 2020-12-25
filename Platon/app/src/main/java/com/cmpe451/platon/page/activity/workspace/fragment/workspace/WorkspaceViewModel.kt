package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Milestones
import com.cmpe451.platon.util.Definitions
import com.google.gson.JsonObject

class WorkspaceViewModel : ViewModel() {

    private val repository = WorkspaceRepository()
    var getWorkspaceResponse = repository.getWorkspaceResponse
    var getUpdateResourceResponse = repository.updateResourceResponse
    var getDeleteResourceResponse = repository.deleteResourceResponse
    var getMilestoneResponse = repository.getMilestoneResponse
    var getAddDeleteUpdateMilestoneResponse=repository.addDeleteUpdateMilestoneResponse

    fun fetchWorkspace(workspace_id:Int, token:String){
        repository.fetchWorkspace(workspace_id, token)
    }
    fun updateWorkspace(workspace_id: Int,title: String?, description: String?, private: Int?, maxCollaborators: Int?, deadline: String?, requirements: String?, skills: String?,state:Int?, authToken: String){
        repository.updateWorkspace(workspace_id, title, description, private, maxCollaborators,
            deadline, requirements, skills, state,authToken)
    }
    fun deleteWorkspace(workspace_id: Int, authToken: String){
        repository.deleteWorkspace(workspace_id, authToken)
    }
    fun addMilestone(workspace_id: Int, title: String, description: String,deadline: String, token: String){
        repository.addMilestone(workspace_id, title, description, deadline, token)
    }
    fun getMilestones(workspace_id: Int, page:Int?, per_page:Int?, token: String){
        repository.getMilestones(workspace_id, page, per_page, token)
    }
    fun updateMilestone(workspace_id: Int,milestone_id:Int, title: String, description: String,deadline: String, token: String){
        repository.updateMilestone(workspace_id,milestone_id, title, description, deadline, token)
    }
    fun deleteMilestone(workspace_id: Int, milestone_id: Int, token: String){
        repository.deleteMilestone(workspace_id, milestone_id, token)
    }

}