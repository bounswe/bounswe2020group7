package com.cmpe451.platon.page.fragment.profilepage.presenter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpe451.platon.networkmodels.models.Research
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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


    }
