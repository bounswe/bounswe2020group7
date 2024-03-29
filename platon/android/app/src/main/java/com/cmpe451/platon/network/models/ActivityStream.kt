package com.cmpe451.platon.network.models

import com.google.gson.annotations.SerializedName
import java.security.AccessControlContext

/*
 Data classes objects used by Retrofit in order to parse responses of the request. Namings of the objects are self explanatory.
 */

data class ActivityStream(
        @SerializedName("@context")
        val cont: String,
        val id: Int,
        val orderedItems:ArrayList<ActivityStreamElement>?,
        val totalItems: Int
)

data class ActivityStreamElement(
        val actor:Actor?,
        val summary:String?
)

data class Actor(
       val id: Int,
       val image: ActorImage,
       val name: String,
       val type: String
)

data class ActorImage(
        val type: String,
        val url: String
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
