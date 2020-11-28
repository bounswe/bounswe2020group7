package com.cmpe451.platon.page.fragment.otherprofile.presenter

import android.content.SharedPreferences
import androidx.navigation.NavController
import com.cmpe451.platon.networkmodels.UserInfoResponse
import com.cmpe451.platon.page.fragment.otherprofile.contract.OtherProfileContract
import com.cmpe451.platon.page.fragment.otherprofile.model.OtherProfileRepository

class OtherProfilePresenter(view: OtherProfileContract.View, private var repository: OtherProfileRepository, private var sharedPreferences: SharedPreferences,private var navController: NavController) : OtherProfileContract.Presenter {
    override fun onFollowersButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun onFollowingButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun onFollowButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun getUser(): UserInfoResponse {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }


}