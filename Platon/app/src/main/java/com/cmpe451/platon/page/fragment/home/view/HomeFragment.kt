package com.cmpe451.platon.page.fragment.home.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.databinding.FragmentHomeBinding
import com.cmpe451.platon.databinding.FragmentLoginBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.home.contract.HomeContract
import com.cmpe451.platon.page.fragment.home.model.HomeRepository
import com.cmpe451.platon.page.fragment.home.presenter.HomePresenter

class HomeFragment : Fragment(), HomeContract.View  {

    private lateinit var presenter: HomeContract.Presenter
    private lateinit var fragmentChangeListener: FragmentChangeListener

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePresenter()
        setFragmentChangeListener()
        setListeners()

    }

    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = HomeRepository(sharedPreferences)
        presenter = HomePresenter(this, repository, sharedPreferences, (activity as HomeActivity).navController )
    }

    private fun initViews(root: View) {

    }

    private fun setFragmentChangeListener() {
        this.fragmentChangeListener = activity as FragmentChangeListener
    }

    private fun setListeners() {
        //password.addTextChangedListener(textWatcher)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.registerFragment).isVisible = false
        menu.findItem(R.id.loginFragment).isVisible = false
        menu.findItem(R.id.logout_menu_btn).isVisible = true
    }
}