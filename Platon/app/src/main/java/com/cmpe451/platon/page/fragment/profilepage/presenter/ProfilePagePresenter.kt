package com.cmpe451.platon.page.fragment.profilepage.presenter

import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.networkmodels.*
import com.cmpe451.platon.page.fragment.profilepage.contract.ProfilePageContract
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import com.cmpe451.platon.page.fragment.profilepage.view.*
import com.cmpe451.platon.util.Definitions
import com.google.gson.Gson


class ProfilePagePresenter(private var view: ProfilePageContract.View?, private var repository: ProfilePageRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : ProfilePageContract.Presenter {

    val token = sharedPreferences.getString("token", null)
    val user_id = sharedPreferences.getString("user_id", "4")!!.toInt()
    lateinit var userInfo: UserInfoResponse
    lateinit var researchInformation: ResearchResponse


    override fun onFollowersButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowerFragment())

    }

    override fun onFollowingButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment())
    }

    override fun onEditProfileButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
    }


    override fun getUser(): Definitions.User {
        return  repository.fetchUser((view as Fragment).activity)
    }
    override fun getUser2(): Definitions.User {
        return repository.fetchUser((view as Fragment).activity)
    }

    override fun onEditButtonClicked() {
        Toast.makeText((view as Fragment).activity, "Edit PP not yet implemented", Toast.LENGTH_SHORT).show()
    }

    override fun onFollowButtonClicked() {
//        navController.navigate(ProfilePagePrivateFragmentDirections.actionProfilePagePrivateFragmentToFollowersFollowingFragment())
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

     override fun bringResearches() {
        if (token != null) {
//            val mytok = token.subSequence(1,token.length-1)
            repository.getResearches(user_id, token,object: HttpRequestListener {
                override fun onRequestCompleted(result: String)  {
                    researchInformation = createResearchResponse(result)
                    view?.researchesFetched(researchInformation)
                }

                override fun onFailure(errorMessage: String) {

                }
            })
        }
    }

    override fun bringUser() {
        if(token != null ){
//            val mytok = token.subSequence(1,token.length-1)
            repository.getUser(token,object: HttpRequestListener {
                override fun onRequestCompleted(result: String) {
                    userInfo = createUserResponse(result)
                    view?.fetchUser(userInfo)
                    sharedPreferences.edit().putString("user_id", userInfo.id.toString()).apply()
                }

                override fun onFailure(errorMessage: String) {

                }
            })
        }
    }

    private fun createUserResponse(responseString: String) : UserInfoResponse {
        return Gson().fromJson<UserInfoResponse>(responseString, UserInfoResponse::class.java)
    }

    private fun createResearchResponse(responseString: String) : ResearchResponse {

        return Gson().fromJson<ResearchResponse>(responseString, ResearchResponse::class.java)
    }




}