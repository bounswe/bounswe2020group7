package com.cmpe451.platon.page.fragment.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Workspace

class WorkspaceListViewModel : ViewModel() {
    private var repository: WorkspaceListRepository = WorkspaceListRepository()
    private var workspaces:MutableLiveData<Resource<List<Workspace>>>
    init{
        workspaces = repository.workspaces
    }

}