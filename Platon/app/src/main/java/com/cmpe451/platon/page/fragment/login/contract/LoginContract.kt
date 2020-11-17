package com.cmpe451.platon.page.fragment.login.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

interface LoginContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onLoginButtonClicked(mail: String, pass: String, remember: Boolean, flag: Boolean)
        fun onAlreadyHaveAccountClicked()
        fun onForgotPasswordClicked(mail: String)

    }

}