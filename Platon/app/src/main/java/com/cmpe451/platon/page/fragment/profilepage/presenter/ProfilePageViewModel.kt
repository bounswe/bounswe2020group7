package com.cmpe451.platon.page.fragment.profilepage.presenter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cmpe451.platon.networkmodels.models.Research
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfilePageViewModel(application: Application): AndroidViewModel(application) {

    //val getResearches: LiveData<List<Research>>
    //val getUsers: LiveData<List<User>>
    private var repository: ProfilePageRepository = ProfilePageRepository()

    init {
        //getResearches = repository.getResearches
        //getUsers = repository.getUsers
    }

    fun insertResearch(research: Research){
        viewModelScope.launch(Dispatchers.IO){
            //repository.insert(research)
        }
    }

}