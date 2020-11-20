package com.cmpe451.platon.page.fragment.login.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.databinding.FragmentLoginBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import com.cmpe451.platon.page.fragment.login.presenter.LoginPresenter

class LoginFragment : Fragment(), LoginContract.View  {

    private lateinit var presenter: LoginContract.Presenter
    private lateinit var fragmentChangeListener: FragmentChangeListener
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setFragmentChangeListener()
        setListeners()

        presenter.onPreLoginAutomated()
    }

    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = LoginRepository(sharedPreferences)
        presenter = LoginPresenter(this, repository, sharedPreferences, (activity as LoginActivity).navController )
    }

    private fun initViews(root: View) {

    }

    private fun setFragmentChangeListener() {
        this.fragmentChangeListener = activity as FragmentChangeListener
    }

    private fun setListeners() {

        binding.loginBtn.setOnClickListener {
            var flag = false

            if (binding.emailEt.text.isNullOrEmpty()){
                binding.emailEt.error = "Required"
                flag = true
            }
            if( binding.passEt.text.isNullOrEmpty()){
                binding.passEt.error = "Required"
                flag = true
            }


            val mail = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val remember = binding.rememberChk.isChecked

            presenter.onLoginButtonClicked(mail, pass, remember, flag)
        }

        binding.dontHaveAccBtn.setOnClickListener {
            presenter.onAlreadyHaveAccountClicked()
        }

        binding.forgotPwBtn.setOnClickListener {
            val mail = binding.emailEt.text.toString().trim()
            presenter.onForgotPasswordClicked(mail)
        }

        //password.addTextChangedListener(textWatcher)
    }




    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

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