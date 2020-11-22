package com.cmpe451.platon.page.fragment.forgotpass.contract

/**
 * @author Burak Ömür
 */

import android.widget.Button
import android.widget.EditText
import com.cmpe451.platon.core.BasePresenter
import com.cmpe451.platon.core.BaseView

class ForgotPasswordContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun onResetPasswordClicked(pass1: EditText, pass2: EditText, token: EditText)
        fun onForgotPassClicked(email: EditText, forgot_btn:Button, pass1: EditText, pass2: EditText, reset_btn:Button, token:EditText)
    }

}