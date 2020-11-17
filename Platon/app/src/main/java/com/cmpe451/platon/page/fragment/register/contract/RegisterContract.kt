package com.cmpe451.platon.page.fragment.register.contract

import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

interface RegisterContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onAlreadyHaveAccountClicked()
        fun onRegisterButtonClicked(firstName: String, lastName: String, mail: String, pass1: String, pass2: String,  flag: Boolean)

    }

}