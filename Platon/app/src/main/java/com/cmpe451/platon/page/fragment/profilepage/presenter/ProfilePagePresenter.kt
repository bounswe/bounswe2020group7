package com.cmpe451.platon.page.fragment.profilepage.presenter

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.cmpe451.platon.page.fragment.profilepage.contract.ProfilePageContract
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import com.cmpe451.platon.page.fragment.profilepage.view.ProfilePageFragment
import com.cmpe451.platon.page.fragment.profilepage.view.ProfilePageFragmentDirections


class ProfilePagePresenter(private var view: ProfilePageContract.View?, private var repository: ProfilePageRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : ProfilePageContract.Presenter {
    override fun onFollowersButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowersFollowingFragment(0))
    }

    override fun onFollowingButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowersFollowingFragment(1))
    }

    override fun onEditProfileButtonClicked() {
        Toast.makeText((view as ProfilePageFragment).activity, "Edit PP not yet implemented", Toast.LENGTH_SHORT).show()
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