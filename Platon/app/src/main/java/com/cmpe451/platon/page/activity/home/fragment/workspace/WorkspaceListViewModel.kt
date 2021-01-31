package com.cmpe451.platon.page.activity.home.fragment.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.WorkspaceListItems

/*
 *  It is a bridge between workspace list repository and workspace list fragment.
 */

class WorkspaceListViewModel : ViewModel() {


    private var repository: WorkspaceListRepository = WorkspaceListRepository()
    var workspaces:MutableLiveData<Resource<WorkspaceListItems>>
    var getWorkspaceRecommendationsResponse = repository.workspaceRecommendationsResponse
    init{
        workspaces = repository.workspaces
    }
    fun getWorkspaces(token: String) {
        repository.getWorkspaces(token)
    }

    fun getWorkspaceRecommendations(num_of_rec: Int, currUserToken: String) {
        repository.getWorkspaceRecommendations(num_of_rec, currUserToken)
    }
}