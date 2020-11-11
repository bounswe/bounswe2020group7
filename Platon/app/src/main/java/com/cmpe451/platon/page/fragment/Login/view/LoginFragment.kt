package com.cmpe451.platon.page.fragment.Login.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.page.fragment.Login.contract.LoginContract
import com.cmpe451.platon.page.fragment.Login.contract.LoginContract.*

class LoginFragment : Fragment(), LoginContract.View  {

    private var presenter: Presenter? = null
    private lateinit var fragmentChangeListener: FragmentChangeListener

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setFragmentChangeListener()
        setListeners()
        initializePresenter()
    }

    private fun initViews(root: View) {

    }

    private fun setFragmentChangeListener() {
        this.fragmentChangeListener = activity as FragmentChangeListener
    }

    private fun setListeners() {
        //loginButton.setOnClickListener {
        //    showProgressBar()
        //    val username = username.text.toString().trim()
        //    val password = password.text.toString().trim()
        //    presenter?.login(username, password)
        //}

        //password.addTextChangedListener(textWatcher)
    }

    private fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        //val repository = LoginRepository(sharedPreferences)
        //setPresenter(LoginPresenter(this, repository, sharedPreferences))
    }


    override fun setPresenter(presenter: LoginContract.Presenter) {
        TODO("Not yet implemented")
    }
}