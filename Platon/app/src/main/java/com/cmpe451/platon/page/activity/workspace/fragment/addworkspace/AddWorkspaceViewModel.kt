package com.cmpe451.platon.page.activity.workspace.fragment.addworkspace

import androidx.lifecycle.ViewModel

class AddWorkspaceViewModel:ViewModel() {
    private val repository:AddWorkspaceRepository = AddWorkspaceRepository()

    var allSkills = repository.allSkills

    fun getAllSkills() {
        repository.getAllSkills()
    }
}