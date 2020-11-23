package com.cmpe451.platon.page.fragment.profilepage.presenter

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.networkmodels.Follower
import com.cmpe451.platon.networkmodels.Research
import com.cmpe451.platon.networkmodels.ResearchResponse
import com.cmpe451.platon.networkmodels.UserInfoResponse
import com.cmpe451.platon.page.fragment.profilepage.contract.ProfilePageContract
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import com.cmpe451.platon.page.fragment.profilepage.view.*
import com.cmpe451.platon.util.Definitions
import com.google.gson.Gson


class ProfilePagePresenter(private var view: ProfilePageContract.View?, private var repository: ProfilePageRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : ProfilePageContract.Presenter {

    val token = sharedPreferences.getString("token", null)
    val user_id = sharedPreferences.getString("user_id", "1")!!.toInt()
    var userInfo: UserInfoResponse? = null
    var researchInformation: ResearchResponse? = null


    override fun onFollowersButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowersFollowingFragment(0))
    }

    override fun onFollowingButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowersFollowingFragment(1))
    }

    override fun onEditProfileButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
    }


    override fun getFollowers(): ArrayList<Definitions.User> {
        bringUser()
        return repository.fetchFollowers((view as FollowerListFragment).activity)

    }

    override fun getFollowing(): ArrayList<Definitions.User> {
        return repository.fetchFollowing((view as FollowingListFragment).activity)
    }

    override fun getProfilePageDetails(): ArrayList<MutableMap<String, String>> {
        return repository.fetchProfilePageDetails((view as ProfilePageFragment).activity)
    }

    override fun getUser(): Definitions.User {
        return  repository.fetchUser((view as Fragment).activity)
//        sharedPreferences.edit().putString("name", user.name)
//        sharedPreferences.edit().putString("surname", user.surname)
//        sharedPreferences.edit().putFloat("rating", user.rating as Float)
//        sharedPreferences.edit().putString("bio", user.bio)
//        return user

        
    }
    override fun getUser2(): Definitions.User {
        return repository.fetchUser((view as Fragment).activity)
    }

    override fun onEditButtonClicked() {
        Toast.makeText((view as EditProfileFragment).activity, "Edit PP not yet implemented", Toast.LENGTH_SHORT).show()
    }

    override fun onFollowButtonClicked() {
//        navController.navigate(ProfilePagePrivateFragmentDirections.actionProfilePagePrivateFragmentToFollowersFollowingFragment())
    }

    override fun onFollowersButtonPrivateClicked() {
        navController.navigate(ProfilePagePrivateFragmentDirections.actionProfilePagePrivateFragmentToFollowersFollowingFragment())
    }

    override fun onFollowingButtonPrivateClicked() {
        navController.navigate(ProfilePagePrivateFragmentDirections.actionProfilePagePrivateFragmentToFollowersFollowingFragment())
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

    override fun goToProfilePage(id: Int) {
        navController.navigate(FollowersFollowingFragmentDirections.actionFollowersFollowingFragmentToProfilePagePrivateFragment(id))
    }

    override fun getProjects(): ArrayList<Definitions.TrendingProject> {
        bringResearches()
        if(researchInformation == null){
            return ArrayList()
        }
        return researchToTrendingProject(researchInformation!!.research_info)
    }

    private fun bringFollowers() {
        repository.getFollowers(1,"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MywiZXhwaXJlX3RpbWUiOiIyMDIxLTExLTIzVDE0OjQxOjQxLjQ2MzIyMSJ9.KhsIsUuPUUu38AEZ9GL5IL9TarnUVIQ1isPM9sYA7j8",object: HttpRequestListener {

            override fun onRequestCompleted(result: String) {
                val followers = createFollowersResponse(result!!)
                val ert = 3
            }

            override fun onFailure(errorMessage: String) {

            }
        }

        )
    }

    private  fun bringResearches() {
        if (token != null) {
            repository.getResearches(user_id, token,object: HttpRequestListener {
                override fun onRequestCompleted(result: String) {
                    researchInformation = createResearchResponse(result)

                }

                override fun onFailure(errorMessage: String) {

                }
            })
        }
    }

    private fun bringUser() {
        if(token != null ){
            val mytok = token.subSequence(1,token.length-1)
            repository.getUser(mytok as String,object: HttpRequestListener {
                override fun onRequestCompleted(result: String) {
                    userInfo = createUserResponse(result)
                    val ert = 3
                }

                override fun onFailure(errorMessage: String) {

                }
            })
        }
    }

    private fun createUserResponse(responseString: String) : UserInfoResponse {
        return Gson().fromJson<UserInfoResponse>(responseString, UserInfoResponse::class.java)
    }

    private fun createResearchResponse(responseString: String) : ResearchResponse? {
        if(responseString == null){
            return null
        }
        return Gson().fromJson<ResearchResponse>(responseString, ResearchResponse::class.java)
    }


    private fun createFollowersResponse(responseString: String) : List<Follower> {
        return Gson().fromJson<List<Follower>>(responseString, Follower::class.java)
    }
    private fun researchToTrendingProject(research:List<Research>) : ArrayList<Definitions.TrendingProject>{
        var list = ArrayList<Definitions.TrendingProject>()
        for(item in research){
            var pro = Definitions.TrendingProject(item.title, null, item.description, Definitions.TrendingProject.TREND.PROJECT)
            list.add(pro)
        }
        return list
    }
}