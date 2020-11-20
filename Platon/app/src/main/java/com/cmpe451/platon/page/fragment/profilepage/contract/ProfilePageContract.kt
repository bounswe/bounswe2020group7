package com.cmpe451.platon.page.fragment.profilepage.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

interface ProfilePageContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onFollowersButtonClicked()
        fun onFollowingButtonClicked()
        fun onEditProfileButtonClicked()
        fun getFollowers():ArrayList<String>
        fun getFollowing():ArrayList<String>
        fun getProfilePageDetails():ArrayList<MutableMap<String, String>>
    }

}