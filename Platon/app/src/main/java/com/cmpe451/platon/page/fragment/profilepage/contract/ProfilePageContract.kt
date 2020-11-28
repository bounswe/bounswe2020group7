package com.cmpe451.platon.page.fragment.profilepage.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.cmpe451.platon.networkmodels.Followers
import com.cmpe451.platon.networkmodels.ResearchResponse
import com.cmpe451.platon.networkmodels.UserInfoResponse
import com.cmpe451.platon.util.Definitions

interface ProfilePageContract {

    interface View : BaseView<Presenter> {
        fun researchesFetched(researchInfo : ResearchResponse)
        fun fetchUser(userInfo: UserInfoResponse)
    }

    interface Presenter : BasePresenter {
        fun onFollowersButtonClicked()
        fun onFollowingButtonClicked()
        fun onEditProfileButtonClicked(name:String, surname:String, job:String, isPrivate:Boolean, profilePhoto:String, googleScholar:String, researchGate:String)
        fun onEditButtonClicked()
        fun onFollowButtonClicked()
        fun bringResearches()
        fun bringUser()
    }

}