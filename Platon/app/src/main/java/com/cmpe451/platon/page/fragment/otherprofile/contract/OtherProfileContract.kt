package com.cmpe451.platon.page.fragment.otherprofile.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView
import com.cmpe451.platon.networkmodels.UserInfoResponse

interface OtherProfileContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onFollowersButtonClicked()
        fun onFollowingButtonClicked()
        fun onFollowButtonClicked()
        fun getUser() : UserInfoResponse
    }

}