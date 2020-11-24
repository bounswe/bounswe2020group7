package com.cmpe451.platon.page.fragment.follow.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.cmpe451.platon.networkmodels.Followers
import com.cmpe451.platon.networkmodels.Followings

interface FollowContract {

    interface View : BaseView<Presenter> {
        fun fetchFollowers(followers: Followers)
        fun fetchFollowing(following: Followings)
    }

    interface Presenter : BasePresenter {
        fun goToProfilePage(id:Int)

    }

}