package com.cmpe451.platon.page.fragment.workspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Workspace

class WorkspaceListRepository() {
    val workspaces : MutableLiveData<Resource<List<Workspace>>> = MutableLiveData()
}