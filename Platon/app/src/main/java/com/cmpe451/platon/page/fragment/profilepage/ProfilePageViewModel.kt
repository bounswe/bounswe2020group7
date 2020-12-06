package com.cmpe451.platon.page.fragment.profilepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.cmpe451.platon.networkmodels.models.Research
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageRepository

class ProfilePageViewModel: ViewModel() {


    var getUser:LiveData<User>
    val getResearches: LiveData<List<Research>>

    private var repository: ProfilePageRepository = ProfilePageRepository()

    init {
        getUser = repository.getUser
        getResearches = repository.getResearches
    }

    fun fetchUser(token:String?){
        if(token != null){
            repository.getUser(token)
        }

    }

    fun fetchResearch(token: String?, userId:Int?) {
        if(token != null && userId != null){
            repository.getResearches(userId, token)
        }
    }

    fun getPersonalInformation():ArrayList<Map<String, String>> {
        val x = ArrayList<Map<String, String>>()
        x.add(mapOf(Pair("title", "E-mail"), Pair("info", getUser.value!!.e_mail)))
        x.add(mapOf(Pair("title", "Job"), Pair("info", getUser.value!!.job)))
        x.add(mapOf(Pair("title", "Rating"), Pair("info", getUser.value!!.rate.toString())))

        return x
    }


}
