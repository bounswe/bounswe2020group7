package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import androidx.lifecycle.ViewModel
import com.cmpe451.platon.util.Definitions

class WorkspaceViewModel : ViewModel() {

    private val repository = WorkspaceRepository()
    var getWorkspaceResponse = repository.getWorkspaceResponse

    fun fetchWorkspace(workspace_id:Int, token:String){
        repository.fetchWorkspace(workspace_id, token)
    }


}