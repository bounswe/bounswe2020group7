package com.cmpe451.platon.page.activity.home.fragment.workspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Workspace

class WorkspaceListRepository() {

    var workspaces : MutableLiveData<Resource<List<Workspace>>> = MutableLiveData()

    fun getWorkspaces(userId: Int, token: String) {
        var res :Resource<List<Workspace>> = Resource.Success(listOf(
            Workspace(1, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(2, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(3, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(4, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(5, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(6, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(7, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(8, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(9, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                    "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(10, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                    "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(11, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                    "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(12, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                    "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),
            Workspace(13, "First Ws", 5, "20.10.2020", false, "requiuerireiurie",
                    "descrjhekrjjhklsdjds", 0, listOf("python","matlab","c++")),

        ))
        workspaces.value = res
    }


}