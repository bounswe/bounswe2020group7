package com.cmpe451.platon.page.fragment.editprofile.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

interface EditProfileContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onEditButtonClicked(name:String?,surname:String?,
                                job:String?, isPrivate:Boolean?,
                                profilePhoto:String?,
                                google_scholar_name:String?,
                                researchgate_name:String?)
    }

}