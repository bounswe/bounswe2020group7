package com.cmpe451.platon.page.fragment.forgotpass.view

/**
 * @author Burak Ömür
 */

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentForgotPasswordBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.forgotpass.presenter.ForgotPasswordPresenter
import com.cmpe451.platon.page.fragment.forgotpass.model.ForgotPasswordRepository
import com.cmpe451.platon.page.fragment.forgotpass.contract.ForgotPasswordContract

/**
 * Forgot Password's fragment class
 * If user forgets his/her password, s/he will be viewing this fragment class.
 */
class ForgotPasswordFragment : Fragment(), ForgotPasswordContract.View {

    // define
    private lateinit var presenter: ForgotPasswordContract.Presenter
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializePresenter()
        setListeners()
    }


    override fun initializePresenter() {
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = ForgotPasswordRepository(sharedPreferences)
        presenter = ForgotPasswordPresenter(this, repository, sharedPreferences, (activity as LoginActivity).navController)
    }

    private fun initViews(root: View) {

    }

    private fun setListeners() {
        binding.forgotPassBtn.setOnClickListener {
            presenter.onForgotPassClicked(binding.emailEt, binding.forgotPassBtn, binding.newPass1Et, binding.newPass2Et, binding.resetPassBtn, binding.tokenEt)
        }

        binding.resetPassBtn.setOnClickListener {
            presenter.onResetPasswordClicked(binding.newPass1Et, binding.newPass2Et, binding.tokenEt)
        }

        //password.addTextChangedListener(textWatcher)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // clear search bar, and make it iconified
        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        // hide all elements in the menu
        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false

    }
}