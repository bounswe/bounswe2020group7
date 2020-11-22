package com.cmpe451.platon.page.fragment.login.view

/**
 * @author Burak Ömür
 */

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentLoginBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import com.cmpe451.platon.page.fragment.login.presenter.LoginPresenter
import com.cmpe451.platon.util.ApiClient
import com.cmpe451.platon.util.ApiInterface

/**
 * Login Fragment class of application
 * If user tries to login to app, it will be viewing this fragment
 */
class LoginFragment : Fragment(), LoginContract.View  {

    //definitions
    private lateinit var presenter: LoginContract.Presenter
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // say it has its own menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializePresenter()
        setListeners()
        // check if auto-login possible
        presenter.onPreLoginAutomated()
    }

    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = LoginRepository(sharedPreferences)
        presenter = LoginPresenter(this, repository, sharedPreferences, (activity as LoginActivity).navController )
    }


    private fun setListeners() {

        // login button listener
        binding.loginBtn.setOnClickListener {
            // call presenter
            presenter.onLoginButtonClicked(binding.emailEt, binding.passEt, binding.rememberChk)
        }

        binding.dontHaveAccBtn.setOnClickListener {
            presenter.onAlreadyHaveAccountClicked()
        }

        binding.forgotPwBtn.setOnClickListener {
            presenter.onForgotPasswordClicked(binding.emailEt)
        }

        //password.addTextChangedListener(textWatcher)
    }




    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // clear search bar, and make it icon
        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
    }

    override fun setFields(mail: String, pass: String, b: Boolean) {
        binding.emailEt.setText(mail)
        binding.passEt.setText(pass)
        binding.rememberChk.isChecked = b
    }

    override fun clickLogin() {
        binding.loginBtn.performClick()
    }


}