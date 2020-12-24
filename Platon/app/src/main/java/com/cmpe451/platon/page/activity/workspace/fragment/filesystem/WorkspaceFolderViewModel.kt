package com.cmpe451.platon.page.activity.workspace.fragment.filesystem

import androidx.lifecycle.ViewModel

class WorkspaceFolderViewModel:ViewModel() {

    private val repository = WorkspaceFolderRepository()
    var getFolderResourceResponse = repository.foldersResourceResponse

    fun getFolder(workspaceId:Int, path:String, token:String){
        repository.getFolder(workspaceId, path, token)
    }

}