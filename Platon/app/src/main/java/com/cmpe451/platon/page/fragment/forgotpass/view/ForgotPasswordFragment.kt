package com.cmpe451.platon.page.fragment.forgotpass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.databinding.FragmentForgotPasswordBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.forgotpass.presenter.ForgotPasswordPresenter
import com.cmpe451.platon.page.fragment.forgotpass.model.ForgotPasswordRepository
import com.cmpe451.platon.page.fragment.forgotpass.contract.ForgotPasswordContract

class ForgotPasswordFragment : Fragment(), ForgotPasswordContract.View {

    private lateinit var presenter: ForgotPasswordContract.Presenter
    private lateinit var fragmentChangeListener: FragmentChangeListener

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

        //initViews(view)
        initializePresenter()
        setFragmentChangeListener()
        setListeners()

    }


    override fun initializePresenter() {
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = ForgotPasswordRepository(sharedPreferences)
        //setPresenter(LoginPresenter(this, repository, sharedPreferences))
        presenter = ForgotPasswordPresenter(this, repository, sharedPreferences, (activity as LoginActivity).navController)
    }

    private fun initViews(root: View) {

    }

    private fun setFragmentChangeListener() {
        this.fragmentChangeListener = activity as FragmentChangeListener
    }

    private fun setListeners() {


        binding.forgotPassBtn.setOnClickListener {
            var flag = false

            if (binding.emailEt.text.isNullOrEmpty()){
                binding.emailEt.error = "Required"
                flag = true
            }

            val mail =  binding.emailEt.text.toString().trim()

            presenter.onForgotPassClicked(mail, flag)
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
}