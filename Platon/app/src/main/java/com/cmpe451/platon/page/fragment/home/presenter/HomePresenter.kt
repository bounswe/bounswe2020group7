package com.cmpe451.platon.page.fragment.home.presenter

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.home.contract.HomeContract
import com.cmpe451.platon.page.fragment.home.model.HomeRepository
import com.cmpe451.platon.page.fragment.home.view.HomeFragment
import com.cmpe451.platon.util.Definitions

class HomePresenter(private var view:HomeContract.View?, private var repository: HomeRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : HomeContract.Presenter {

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        this.view = null
    }

    override fun getUpcomingEvents(): ArrayList<Definitions.UpcomingEvent> {
        return repository.fetchUpcomingEvents((view as HomeFragment).activity)
    }

    override fun getTrendingProjects(): ArrayList<Definitions.TrendingProject> {
        return  repository.fetchTrendingProjects((view as HomeFragment).activity)
    }

    override fun getActivities(): ArrayList<Definitions.ActivityStream> {
        return repository.fetchActivityStream((view as HomeFragment).activity)
    }
}