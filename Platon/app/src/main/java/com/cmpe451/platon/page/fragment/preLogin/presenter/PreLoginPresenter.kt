package com.cmpe451.platon.page.fragment.preLogin.presenter

import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.cmpe451.platon.R
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragment
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragmentDirections
import com.cmpe451.platon.util.Definitions.TrendingProject
import com.cmpe451.platon.util.Definitions.UpcomingEvent

class PreLoginPresenter(view: PreLoginContract.View, private var repository: PreLoginRepository, private var sharedPreferences: SharedPreferences) : PreLoginContract.Presenter {

    private var view: PreLoginContract.View? = view
    override fun onPreLoginMade() {
        val rememberMe = sharedPreferences.getBoolean("remember_me", false)

        if (rememberMe){
            val action = PreLoginFragmentDirections.actionPreLoginFragmentToLoginFragment()
            ((view as PreLoginFragment).activity as LoginActivity).navController.navigate(action)
        }
    }

    override fun getUpcomingEvents(): ArrayList<UpcomingEvent> {
        return repository.fetchUpcomingEvents((view as PreLoginFragment).activity)
    }

    override fun getTrendingProjects(): ArrayList<TrendingProject> {
        return  repository.fetchTrendingProjects((view as PreLoginFragment).activity)
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        this.view = null
    }
}