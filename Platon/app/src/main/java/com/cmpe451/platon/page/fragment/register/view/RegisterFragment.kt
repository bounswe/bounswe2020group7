package com.cmpe451.platon.page.fragment.register.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentRegisterBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.register.contract.RegisterContract
import com.cmpe451.platon.page.fragment.register.model.RegisterRepository
import com.cmpe451.platon.page.fragment.register.presenter.RegisterPresenter

class RegisterFragment : Fragment(), RegisterContract.View {

    private lateinit var presenter: RegisterContract.Presenter
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializePresenter()
        setListeners()
    }


    override fun initializePresenter() {
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = RegisterRepository(sharedPreferences)
        presenter = RegisterPresenter(this, repository, sharedPreferences, (activity as LoginActivity).navController)
    }

    private fun setListeners() {
        binding.registerBtn.setOnClickListener {
            presenter.onRegisterButtonClicked(binding.registerBtn, binding.firstnameTv, binding.lastnameTv, binding.mailTv, binding.jobTv, binding.pw1Tv, binding.pw2Tv, binding.termsChk)
        }

        binding.alreadHaveBtn.setOnClickListener {
            presenter.onAlreadyHaveAccountClicked()
        }
        binding.termsChk.isClickable = true
        binding.termsChk.setOnClickListener {
            val dial = AlertDialog.Builder(activity as BaseActivity)
            dial.setTitle("Terms and Conditions")
            dial.setMessage(presenter.getTermsAndConds())
            dial.setPositiveButton("Accept") { _, _ ->
                binding.termsChk.isChecked = true
            }
            dial.setNegativeButton("Reject") { _, _ ->
                binding.termsChk.isChecked = false
            }
            dial.show()
        }

        //password.addTextChangedListener(textWatcher)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.notification_btn)?.isVisible = false
        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
    }
}

