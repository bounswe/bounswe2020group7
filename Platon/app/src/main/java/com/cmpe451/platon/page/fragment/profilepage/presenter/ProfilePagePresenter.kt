package com.cmpe451.platon.page.fragment.profilepage.presenter

import android.content.SharedPreferences
import android.util.Log
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
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable


class ProfilePagePresenter(private var view: ProfilePageContract.View?, private var repository: ProfilePageRepository, private var sharedPreferences: SharedPreferences, private var navController: NavController) : ProfilePageContract.Presenter {

    val token = sharedPreferences.getString("token", null)
    var user_id = 0
    lateinit var userInfo: UserInfoResponse
    lateinit var researchInformation: ResearchResponse


    override fun onFollowersButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowerFragment())

    }

    override fun onFollowingButtonClicked() {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment())
    }

    override fun onEditProfileButtonClicked(name: String, surname: String, job: String, isPrivate: Boolean, profilePhoto: String, googleScholar: String, researchGate: String) {
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment(name, surname,job,isPrivate,profilePhoto,googleScholar,researchGate))
    }


//    override fun getUser(): Definitions.User {
//        return  repository.fetchUser((view as Fragment).activity)
//    }


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
        if (token != null && user_id != 0) {

            val observer=object :Observer<ResearchResponse>{
                override fun onSubscribe(d: Disposable?) {
                    Log.i("Fettch Research...", "Fetching!!")
                }

                override fun onNext(t: ResearchResponse?) {
                    if(t != null){
                        view?.researchesFetched(t)
                        researchInformation = t
                    }
                }

                override fun onError(e: Throwable?) {
                    Log.i("Fettch Ress errr...", "Error!!")
                }

                override fun onComplete() {
                    Log.i("Completed Ress...", "Compp!!")
                }

            }

            repository.getResearches(observer, user_id, token)
        }
    }

    override fun bringUser() {
        if(token != null ){
            val observer=object :Observer<UserInfoResponse>{
                override fun onSubscribe(d: Disposable?) {
                    Log.i("Fettch User...", "Fetching!!")
                }

                override fun onNext(t: UserInfoResponse?) {
                    if(t != null){
                        view?.fetchUser(t)
                        userInfo = t
                        user_id = userInfo.id
                        bringResearches()
                    }
                }

                override fun onError(e: Throwable?) {
                    Log.i("Fettch User errr...", "Error!!")
                }

                override fun onComplete() {
                    Log.i("Completed User...", "Compp!!")
                }

            }


            repository.getUser(observer, token)

        }
    }
}