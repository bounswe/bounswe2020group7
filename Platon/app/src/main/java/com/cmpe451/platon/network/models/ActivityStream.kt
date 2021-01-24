package com.cmpe451.platon.network.models

data class ActivityStream(val data:List<ActivityStreamElement>?)


data class ActivityStreamElement(
        val image:String?,
        val message:String?
)


data class UpcomingEvents(
        val number_of_pages:Int,
        val upcoming_events:List<UpcomingEvent>?)

data class UpcomingEvent(
        val acronym:String,
        val date:String,
        val deadline:String,
        val id:Int,
        val link:String,
        val location:String,
        val title:String

)

data class TrendingProjects(
        val trending_projects:List<TrendingProject>?)

data class TrendingProject(
        val description:String,
        val state:Int,
        val id:Int,
        val title:String,
        val contributor_list:List<Contributor>

)

data class Contributor(
        val name:String,
        val surname:String,
        val id:Int
)

data class CalendarItem(
        val deadline: String,
        val deadline_str: String,
        val title:String,
        val type:Int,
        val workspace_id:Int,
        val workspace_title:String
)
