package com.cmpe451.platon.page.fragment.preLogin.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.adapter.UpcomingEventsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentPreLoginBinding
import com.cmpe451.platon.databinding.TrendProjectCellBinding
import com.cmpe451.platon.databinding.UpcomingEventCellBinding
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository
import com.cmpe451.platon.page.fragment.preLogin.presenter.PreLoginPresenter
import com.cmpe451.platon.util.Definitions
import com.cmpe451.platon.R

class PreLoginFragment : Fragment(), PreLoginContract.View, TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener  {

    private var presenter: PreLoginContract.Presenter? = null
    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView

    private lateinit var textHello: TextView

    lateinit var binding: FragmentPreLoginBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePresenter()

        presenter?.onPreLoginMade()
        initViews(view)
        setListeners()
    }

    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = PreLoginRepository(sharedPreferences)

        presenter = PreLoginPresenter(this,repository, sharedPreferences )
    }


    private fun initViews(root: View) {
        val myTrendingProject = presenter?.getTrendingProjects()

        val myUpcomingEvents = presenter?.getUpcomingEvents()

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