package com.cmpe451.platon.page.fragment.login.view

/**
 * @author Burak Ömür
 */

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentLoginBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.login.presenter.LoginViewModel
import com.cmpe451.platon.util.Definitions

/**
 * Login Fragment class of application
 * If user tries to login to app, it will be viewing this fragment
 */
class LoginFragment : Fragment() {

    private lateinit var mLoginViewModel: LoginViewModel

    //definitions
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // say it has its own menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mLoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setListeners()
        // check if auto-login possible
    }


    private fun setListeners() {
        // login button listener
        binding.loginBtn.setOnClickListener {
            // call presenter
            mLoginViewModel.tryToLogin(binding.loginBtn, binding.emailEt, binding.passEt, binding.rememberChk)
        }

        binding.dontHaveAccBtn.setOnClickListener {
            onAlreadyHaveAccountClicked()
        }

        binding.forgotPwBtn.setOnClickListener {
            onForgotPasswordClicked()
        }


        mLoginViewModel.getToken.observe(viewLifecycleOwner, { t->
            if(t!= null && t.isNotEmpty()){
                activity?.finish()
                activity?.startActivity(Intent(activity, HomeActivity::class.java))
            }
        })



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
        menu.findItem(R.id.notification_btn)?.isVisible = false
    }

    fun setFields(mail: String, pass: String, b: Boolean) {
        binding.emailEt.setText(mail)
        binding.passEt.setText(pass)
        binding.rememberChk.isChecked = b
    }

    fun clickLogin() {
        binding.loginBtn.performClick()
    }


    private fun onAlreadyHaveAccountClicked() {
        Definitions().vibrate(50, activity as Activity)
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(action)

    }

    fun onForgotPasswordClicked() {
        Definitions().vibrate(50, activity as Activity)
        val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
        findNavController().navigate(action)

    }

}