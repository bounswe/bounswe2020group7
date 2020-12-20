package com.cmpe451.platon.network.models


data class WorkspaceListItems(
        val workspaces :List<WorkspaceListItem>
)
data class WorkspaceListItem (
        val id: Int,
        val title: String,
        val is_private:Boolean?,
        val description: String,
        val creation_time:String,
        val deadline:String,
        val state: Int,
        val max_collaborators:Int,
        val contributors: List<Contributor>
)
//data class Workspace(
//        val id: Int,
//        val title: String,
//        val is_private:Boolean?,
//        val description: String,
//        val creation_time:String,
//        val deadline:String,
//        val state: Int,
//        val max_collaborators:Int,
//        val contributors: List<Contributor>
//)




data class Job(
    val id: Int,
    val name:String
)
