package com.cmpe451.platon.network.models


data class WorkspaceInvitation(
        val invitation_id: Int,
        val workspace_id : Int,
        val invitor_id : Int,
        val invitor_fullname:String,
        val invitee_id:Int,
        val workspace_description:String,
        val workspace_title: String
)

data class WorkspaceApplication(
        val applicant_id: Int,
        val application_id:Int,
        val applicant_fullname: String
)

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
        val upcoming_events:List<UpcomingEvent>
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

data class Milestone(
        val milestone_id:Int,
        val workspace_id:Int,
        val title:String,
        val description: String,
        val deadline: String,
        val creator_id:Int,
        val creator_name:String,
        val creator_surname:String,
        val creator_e_mail:String,
        val creator_rate:Double,
        val creator_job_name:String,
        val creator_institution:String,
        val creator_is_private:Boolean
)
data class Milestones(
        val number_of_pages:Int,
        val result: List<Milestone>
)
