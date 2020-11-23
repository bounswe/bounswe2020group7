package com.cmpe451.platon.page.fragment.register.contract

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

interface RegisterContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun getTermsAndConds():String
        fun onAlreadyHaveAccountClicked()
        fun onRegisterButtonClicked(register_btn: Button, firstName: EditText, lastName: EditText, mail: EditText, job:EditText, pass1: EditText, pass2: EditText, terms: CheckBox)

    }

}