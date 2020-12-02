package com.cmpe451.platon.page.fragment.home.presenter

import android.content.SharedPreferences
import androidx.navigation.NavController
import com.cmpe451.platon.page.fragment.home.model.HomeRepository

class HomePresenter(private var repository: HomeRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) {

/*
    fun getUpcomingEvents(): ArrayList<Definitions.UpcomingEvent> {
        //return repository.fetchUpcomingEvents((view as HomeFragment).activity)
    }

    fun getTrendingProjects(): ArrayList<Definitions.TrendingProject> {
        //return  repository.fetchTrendingProjects((view as HomeFragment).activity)
    }

    fun getActivities(): ArrayList<Definitions.ActivityStream> {
        //return repository.fetchActivityStream((view as HomeFragment).activity)
    }*/
}