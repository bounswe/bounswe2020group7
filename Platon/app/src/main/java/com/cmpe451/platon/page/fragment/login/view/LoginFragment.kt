package com.cmpe451.platon.page.fragment.login.view

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
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
    }

    private fun initializePresenter(){
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
            val mail = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val remember = binding.rememberChk.isChecked

            presenter.onLoginButtonClicked(mail, pass, remember)
        }

        binding.dontHaveAccBtn.setOnClickListener {
            presenter.onAlreadyHaveAccountClicked()
        }

        //password.addTextChangedListener(textWatcher)
    }




    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.registerFragment).isVisible = false
        menu.findItem(R.id.loginFragment).isVisible = false
    }

    override fun setPresenter(presenter: LoginContract.Presenter) {
        TODO("Not yet implemented")
    }



}