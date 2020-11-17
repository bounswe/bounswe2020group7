package com.cmpe451.platon.page.fragment.forgotpass.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

class ForgotPasswordContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onForgotPassClicked(mail: String, flag:Boolean)

    }

}