package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.WorkspaceListItem
import java.util.*


class WorkspaceRepository {


    var getWorkspaceResponse: MutableLiveData<Resource<WorkspaceListItem>> = MutableLiveData()

    fun fetchWorkspace() {
//
//        getWorkspaceResponse.value = Resource.Success(WorkspaceListItem(1, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
//        "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")))
    }
}