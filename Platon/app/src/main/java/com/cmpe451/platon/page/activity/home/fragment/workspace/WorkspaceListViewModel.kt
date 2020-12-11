package com.cmpe451.platon.page.activity.home.fragment.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Workspace

class WorkspaceListViewModel : ViewModel() {


    private var repository: WorkspaceListRepository = WorkspaceListRepository()
    var workspaces:MutableLiveData<Resource<List<Workspace>>>
    init{
        workspaces = repository.workspaces
    }
    fun getWorkspaces(userId: Int, token: String) {
        repository.getWorkspaces(userId, token)
    }
}