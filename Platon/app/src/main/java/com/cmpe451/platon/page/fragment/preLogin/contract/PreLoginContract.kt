package com.cmpe451.platon.page.fragment.preLogin.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

interface PreLoginContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onPreLoginMade()

    }

}