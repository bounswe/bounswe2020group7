package com.cmpe451.platon.network.models


data class WorkspaceListItems(
        val workspaces :List<WorkspaceListItem>
)
data class WorkspaceListItem (
        val id: Int,
        val title: String,
        val is_private:Int?,
        val description: String,
        val creation_time:String,
        val deadline:String,
        val state: Int,
        val max_collaborators:Int,
        val contributors: List<Contributor>
)
data class Workspace(
        val id: Int,
        val active_contributors: List<Contributor>,
        val creator_id:Int,
        val deadline:String,
        val description: String,
        val is_private:Boolean,
        val title: String,
        val max_collaborators:Int,
        val requirements:List<String>,
        val timestamp:String,
        val skills:List<String>,
        val state: Int,

)
data class Folder(
        val cwd:String,
        val files :List<String>,
        val folders:List<String>
)


data class Job(
    val id: Int,
    val name:String
)
