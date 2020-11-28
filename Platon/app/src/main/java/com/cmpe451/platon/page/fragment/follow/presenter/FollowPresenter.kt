package com.cmpe451.platon.page.fragment.follow.presenter

import android.content.SharedPreferences
import androidx.navigation.NavController
import com.cmpe451.platon.`interface`.HttpRequestListener
import com.cmpe451.platon.networkmodels.Followers
import com.cmpe451.platon.networkmodels.Followings
import com.cmpe451.platon.networkmodels.ResearchResponse
import com.cmpe451.platon.networkmodels.UserInfoResponse
import com.cmpe451.platon.page.fragment.editprofile.contract.EditProfileContract
import com.cmpe451.platon.page.fragment.editprofile.model.EditProfileRepository
import com.cmpe451.platon.page.fragment.follow.contract.FollowContract
import com.cmpe451.platon.page.fragment.follow.model.FollowRepository
import com.google.gson.Gson

class FollowPresenter(private var view: FollowContract.View, private var repository: FollowRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : FollowContract.Presenter {
    val token = sharedPreferences.getString("token", null)
    val user_id = sharedPreferences.getString("user_id", "4")!!.toInt()
    lateinit var userInfo: UserInfoResponse
    lateinit var researchInformation: ResearchResponse
    override fun goToProfilePage(id: Int) {
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
    fun bringFollowers() {
        if(token != null){
//            val mytok = token.subSequence(user_id,token.length-1)
            repository.getFollowers(user_id,token,object: HttpRequestListener {

                override fun onRequestCompleted(result: String) {
                    val followers = createFollowersResponse(result)
                    view?.fetchFollowers(followers)
                    val ert = 3
                }

                override fun onFailure(errorMessage: String) {

                }
            }
            )
        }
    }
    fun bringFollowing() {
        if(token != null){
//            val mytok = token.subSequence(user_id,token.length-1)
            repository.getFollowing(user_id,token,object: HttpRequestListener {

                override fun onRequestCompleted(result: String) {
                    val following = createFollowingResponse(result)
                    view?.fetchFollowing(following)
                    val ert = 3
                }

                override fun onFailure(errorMessage: String) {

                }
            }
            )
        }
    }
    private fun createFollowersResponse(responseString: String) : Followers {
        return Gson().fromJson<Followers>(responseString, Followers::class.java)
    }
    private fun createFollowingResponse(responseString: String) : Followings {
        return Gson().fromJson<Followings>(responseString, Followings::class.java)
    }

}
