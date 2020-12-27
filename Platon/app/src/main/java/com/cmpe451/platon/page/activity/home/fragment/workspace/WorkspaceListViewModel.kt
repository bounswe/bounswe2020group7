package com.cmpe451.platon.page.activity.home.fragment.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.WorkspaceListItems

class WorkspaceListViewModel : ViewModel() {


    private var repository: WorkspaceListRepository = WorkspaceListRepository()
    var workspaces:MutableLiveData<Resource<WorkspaceListItems>>
    init{
        workspaces = repository.workspaces
    }
    fun getWorkspaces(token: String) {
        repository.getWorkspaces(token)
    }
}