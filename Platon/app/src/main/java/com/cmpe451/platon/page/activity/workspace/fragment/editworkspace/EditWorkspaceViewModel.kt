package com.cmpe451.platon.page.activity.workspace.fragment.editworkspace

import androidx.lifecycle.ViewModel
import com.cmpe451.platon.util.Definitions

class EditWorkspaceViewModel : ViewModel() {

    private val repository = EditWorkspaceRepository()
    var getUpdateResourceResponse = repository.updateResourceResponse
    var getDeleteResourceResponse = repository.deleteResourceResponse

    fun updateWorkspace(workspace_id: Int,title: String?, description: String?, private: Int?, maxCollaborators: Int?, deadline: String?, requirements: String?, skills: String?,state:Int?, authToken: String){
        repository.updateWorkspace(workspace_id, title, description, private, maxCollaborators,
            deadline, requirements, skills, state,authToken)
    }
    fun deleteWorkspace(workspace_id: Int, authToken: String){
        repository.deleteWorkspace(workspace_id, authToken)
    }


}