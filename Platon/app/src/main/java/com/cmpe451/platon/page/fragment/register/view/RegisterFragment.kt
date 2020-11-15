package com.cmpe451.platon.page.fragment.register.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.databinding.FragmentRegisterBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.register.contract.RegisterContract
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository
import com.cmpe451.platon.page.fragment.register.presenter.RegisterPresenter

class RegisterFragment : Fragment(), RegisterContract.View  {

    private lateinit var presenter: RegisterContract.Presenter
    private lateinit var fragmentChangeListener: FragmentChangeListener

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initViews(view)
        initializePresenter()
        setFragmentChangeListener()
        setListeners()

    }


    private fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = RegisterRepository(sharedPreferences)
        //setPresenter(LoginPresenter(this, repository, sharedPreferences))
        presenter = RegisterPresenter(this, repository, sharedPreferences, (activity as LoginActivity).navController)
    }

    private fun initViews(root: View) {

    }

    private fun setFragmentChangeListener() {
        this.fragmentChangeListener = activity as FragmentChangeListener
    }

    private fun setListeners() {
        binding.registerBtn.setOnClickListener {
            val username = binding.usernameTv.text.toString().trim()
            val mail = binding.mailTv.text.toString().trim()
            val pass1 = binding.pw1Tv.text.toString().trim()
            val pass2 = binding.pw2Tv.text.toString().trim()
            val phone = binding.phoneTv.text.toString().trim()
            val fullName = binding.fullnameTv.text.toString().trim()
            val terms = binding.termsChk.isChecked

            presenter.onRegisterButtonClicked(fullName, username, mail, pass1, pass2, phone, terms)
        }

        binding.alreadHaveBtn.setOnClickListener {
            presenter.onAlreadyHaveAccountClicked()
        }

        //loginButton.setOnClickListener {
        //    showProgressBar()
        //    val username = username.text.toString().trim()
        //    val password = password.text.toString().trim()
        //    presenter?.login(username, password)
        //}

        //password.addTextChangedListener(textWatcher)
    }




    override fun setPresenter(presenter: RegisterContract.Presenter) {
        TODO("Not yet implemented")
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.registerFragment).isVisible = false
        menu.findItem(R.id.loginFragment).isVisible = false
    }
}