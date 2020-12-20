package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import androidx.lifecycle.ViewModel
import com.cmpe451.platon.util.Definitions

class WorkspaceViewModel : ViewModel() {

    private val repository = WorkspaceRepository()
    var getWorkspaceResponse = repository.getWorkspaceResponse
    lateinit var isPrivateString:String
    lateinit var workspaceStateString:String

    fun fetchWorkspace(){
        repository.fetchWorkspace()
        setFields()
    }
    private fun setFields(){
        if(getWorkspaceResponse.value?.data!=null){
            isPrivateString = if(getWorkspaceResponse.value!!.data!!.is_private!! ==1 ) "Private Workspace" else "Public Workspace"
            workspaceStateString = when(getWorkspaceResponse.value!!.data!!.state){
                0-> "Search for Collaborators State"
                1-> "Ongoing State"
                else -> "Finished"
            }
        }
    }

}