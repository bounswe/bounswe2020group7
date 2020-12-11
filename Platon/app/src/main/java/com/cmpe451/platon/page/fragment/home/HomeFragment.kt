package com.cmpe451.platon.page.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.ActivityStreamAdapter
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.adapter.UpcomingEventsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.ActivityStreamCellBinding
import com.cmpe451.platon.databinding.FragmentHomeBinding
import com.cmpe451.platon.databinding.TrendProjectCellBinding
import com.cmpe451.platon.databinding.UpcomingEventCellBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.ActivityStreamElement
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.util.Definitions

class HomeFragment : Fragment(), TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener, ActivityStreamAdapter.ActivityStreamButtonClickListener {

    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView
    private lateinit var activityStreamRecyclerView: RecyclerView
    private lateinit var binding: FragmentHomeBinding
    private val mHomeViewModel: HomeViewModel by activityViewModels()

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
        initViews()
        setListeners()

        mHomeViewModel.getActivities((activity as HomeActivity).token!!)
    }



    private fun initViews() {
        val myTrendingProject: ArrayList<Definitions.TrendingProject> = mHomeViewModel.getTrendingProjects()
        val myUpcomingEvents: ArrayList<Definitions.UpcomingEvent> = mHomeViewModel.getUpcomingEvents()
        val myActivities: ArrayList<ActivityStreamElement> = arrayListOf()

        trendingProjectsRecyclerView = binding.homeTrendingProjectsRecyclerView
        trendingProjectsRecyclerView.adapter = TrendingProjectsAdapter(myTrendingProject, requireContext(), this)
        trendingProjectsRecyclerView.layoutManager = LinearLayoutManager(context)

        upcomingEventsRecyclerView = binding.homeUpcomingEventsRecyclerView
        upcomingEventsRecyclerView.adapter = UpcomingEventsAdapter(myUpcomingEvents,requireContext(), this)
        upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL, false)

        activityStreamRecyclerView = binding.homeActivityStreamRecyclerView
        activityStreamRecyclerView.adapter = ActivityStreamAdapter(myActivities, requireContext(), this)
        activityStreamRecyclerView.layoutManager = LinearLayoutManager(context)
    }


    private fun setListeners() {

        mHomeViewModel.getActivityStreamResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java -> {
                    (activityStreamRecyclerView.adapter as ActivityStreamAdapter).submitElements(t.data as ArrayList<ActivityStreamElement>)
                }
                Resource.Error::class.java ->Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.registerFragment).isVisible = false
        menu.findItem(R.id.loginFragment).isVisible = false
    }

    override fun onTrendingProjectButtonClicked(binding: TrendProjectCellBinding, position: Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onUpcomingButtonClicked(binding: UpcomingEventCellBinding, position: Int) {
        if (binding.upcomingEventDesc.visibility == View.GONE){
            binding.upcomingEventDesc.visibility = View.VISIBLE
        }else{
            binding.upcomingEventDesc.visibility = View.GONE
        }

        binding.upcomingEventDesc.refreshDrawableState()

        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onActivityStreamButtonClicked(binding: ActivityStreamCellBinding, position: Int) {
        //todo
    }
}