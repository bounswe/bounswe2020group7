package com.cmpe451.platon.page.activity.workspace.fragment.addworkspace

import androidx.lifecycle.ViewModel

class AddWorkspaceViewModel:ViewModel() {
    private val repository:AddWorkspaceRepository = AddWorkspaceRepository()


    var allSkills = repository.allSkills
    var getAddDeleteWorkspaceResourceResponse = repository.addDeleteWorkspaceResourceResponse

    fun getAllSkills() {
        repository.getAllSkills()
    }
    fun addWorkspace(title:String, description:String, isPrivate:Int, max_collaborators:Int?,
    deadline:String?, requirements:String?,skills:String?,auth_token:String){
        repository.addWorkspace(title, description, isPrivate, max_collaborators,
        deadline, requirements, skills,auth_token)
    }
}