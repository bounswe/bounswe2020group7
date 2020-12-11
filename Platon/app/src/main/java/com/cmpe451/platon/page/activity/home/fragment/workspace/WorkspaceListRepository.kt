package com.cmpe451.platon.page.activity.home.fragment.workspace

import androidx.lifecycle.MutableLiveData
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Workspace

class WorkspaceListRepository() {

    var workspaces : MutableLiveData<Resource<List<Workspace>>> = MutableLiveData()

    fun getWorkspaces(userId: Int, token: String) {
        var res :Resource<List<Workspace>> = Resource.Success(listOf(
                Workspace(1, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
                Workspace(2, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
                Workspace(3, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
                Workspace(4, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
                Workspace(5, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
                Workspace(6, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
                Workspace(7, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
                Workspace(8, "ws1", "djfksljfksjfklsjfksjfklsjdflk", "jskjdksjdks"),
        ))
        workspaces.value = res
    }


}