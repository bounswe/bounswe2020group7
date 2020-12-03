package com.cmpe451.platon.page.fragment.preLogin.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.adapter.UpcomingEventsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentPreLoginBinding
import com.cmpe451.platon.databinding.TrendProjectCellBinding
import com.cmpe451.platon.databinding.UpcomingEventCellBinding
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository
import com.cmpe451.platon.util.Definitions

class PreLoginFragment : Fragment(),TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener  {

    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView

    private lateinit var sharedPreferences: SharedPreferences

    lateinit var binding: FragmentPreLoginBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreLoginBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        sharedPreferences = activity?.getSharedPreferences("token_file", Context.MODE_PRIVATE)!!
        doAutoLogin()
        return binding.root
    }

    private fun doAutoLogin(){
        val mail = sharedPreferences.getString("mail", null)
        val pass = sharedPreferences.getString("pass", null)
        if(mail != null && pass != null){
            findNavController().navigate(PreLoginFragmentDirections.actionPreLoginFragmentToLoginFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setListeners()
    }


    private fun initViews(root: View) {
        val myTrendingProject :ArrayList<Definitions.TrendingProject>? = PreLoginRepository().fetchTrendingProjects()

        val myUpcomingEvents :ArrayList<Definitions.UpcomingEvent>? = PreLoginRepository().fetchUpcomingEvents()

        if (myTrendingProject != null){
            trendingProjectsRecyclerView = binding.preLoginTrendingProjectsRecyclerView
            trendingProjectsRecyclerView.adapter = TrendingProjectsAdapter(myTrendingProject, requireContext(), this)
            trendingProjectsRecyclerView.layoutManager = LinearLayoutManager(context)
        }
        if (myUpcomingEvents != null){
            upcomingEventsRecyclerView = binding.preLoginUpcomingEventsRecyclerView
            upcomingEventsRecyclerView.adapter = UpcomingEventsAdapter(myUpcomingEvents,requireContext(), this)
            upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        }
    }


    private fun setListeners() {
        //password.addTextChangedListener(textWatcher)
    }

    override fun onUpcomingButtonClicked(binding: UpcomingEventCellBinding, position:Int) {
        if (binding.upcomingEventDesc.visibility == View.GONE){
            binding.upcomingEventDesc.visibility = View.VISIBLE
        }else{
            binding.upcomingEventDesc.visibility = View.GONE
        }

        binding.upcomingEventDesc.refreshDrawableState()

        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.notification_btn).isVisible = false
        menu.findItem(R.id.registerFragment).isVisible = true
        menu.findItem(R.id.loginFragment).isVisible = true
        menu.findItem(R.id.logout_menu_btn).isVisible = false
    }


    override fun onTrendingProjectButtonClicked(binding: TrendProjectCellBinding, position:Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }
}