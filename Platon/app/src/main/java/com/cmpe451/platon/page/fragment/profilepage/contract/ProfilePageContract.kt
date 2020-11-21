package com.cmpe451.platon.page.fragment.profilepage.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.cmpe451.platon.util.Definitions

interface ProfilePageContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onFollowersButtonClicked()
        fun onFollowingButtonClicked()
        fun onEditProfileButtonClicked()
        fun getFollowers(): ArrayList<Definitions.User>
        fun getFollowing(): ArrayList<Definitions.User>
        fun getProfilePageDetails():ArrayList<MutableMap<String, String>>
        fun getUser(): Definitions.User
    }

}