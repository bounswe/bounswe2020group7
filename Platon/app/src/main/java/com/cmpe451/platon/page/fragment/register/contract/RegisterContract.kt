package com.cmpe451.platon.page.fragment.register.contract

import android.widget.CheckBox
import android.widget.EditText
import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

interface RegisterContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onAlreadyHaveAccountClicked()
        fun onRegisterButtonClicked(firstName: EditText, lastName: EditText, mail: EditText, pass1: EditText, pass2: EditText, terms: CheckBox)

    }

}