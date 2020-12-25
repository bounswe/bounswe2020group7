package com.cmpe451.platon.page.activity.workspace.fragment.filesystem

import androidx.lifecycle.ViewModel
import okhttp3.RequestBody

class WorkspaceFolderViewModel:ViewModel() {

    private val repository = WorkspaceFolderRepository()
    var getFolderResourceResponse = repository.foldersResourceResponse
    var getAddUpdateDeleteFolderResourceResponse = repository.addUpdateDeleteFolderResourceResponse

    var getAddFileToWorkspaceResourceResponse=repository.addFileToWorkspaceResourceResponse

    fun getFolder(workspaceId:Int, path:String, token:String){
        repository.getFolder(workspaceId, path, token)
    }

    fun addFolder(workspaceId: Int, path: String, folderName: String, token: String) {
        repository.addFolder(workspaceId, path, folderName, token)
    }

    fun updateFolderName(workspaceId: Int, cwd: String, folderName: String, token: String) {
        repository.updateFolderName(workspaceId, cwd, folderName, token)
    }

    fun deleteFolder(workspaceId: Int, cwd: String, folderName: String, token: String) {
        repository.deleteFolder(workspaceId, cwd, folderName, token)
    }

    fun uploadFile(workspaceId: Int, path: RequestBody, fileName:RequestBody, file: RequestBody, token: String){
        repository.uploadFile(workspaceId, path,fileName ,file ,token)
    }

}