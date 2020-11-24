package com.cmpe451.platon.page.fragment.home.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.home.contract.HomeContract
import com.cmpe451.platon.page.fragment.home.model.HomeRepository
import com.cmpe451.platon.page.fragment.home.presenter.HomePresenter
import com.cmpe451.platon.util.Definitions

class HomeFragment : Fragment(), HomeContract.View , TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener, ActivityStreamAdapter.ActivityStreamButtonClickListener {

    private lateinit var presenter: HomeContract.Presenter
    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView
    private lateinit var activityStreamRecyclerView: RecyclerView
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
        initViews(view)
        setListeners()
    }

    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = HomeRepository(sharedPreferences)
        presenter = HomePresenter(this, repository, sharedPreferences, (activity as HomeActivity).navController )
    }

    private fun initViews(root: View) {
        val myTrendingProject = presenter?.getTrendingProjects()

        val myUpcomingEvents = presenter?.getUpcomingEvents()

        val myActivities = presenter?.getActivities()

        if (myTrendingProject != null){
            trendingProjectsRecyclerView = binding.homeTrendingProjectsRecyclerView
            trendingProjectsRecyclerView.adapter = TrendingProjectsAdapter(myTrendingProject, requireContext(), this)
            trendingProjectsRecyclerView.layoutManager = LinearLayoutManager(context)
        }
        if (myUpcomingEvents != null){
            upcomingEventsRecyclerView = binding.homeUpcomingEventsRecyclerView
            upcomingEventsRecyclerView.adapter = UpcomingEventsAdapter(myUpcomingEvents,requireContext(), this)
            upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)
        }
        if (myActivities != null){
            activityStreamRecyclerView = binding.homeActivityStreamRecyclerView
            activityStreamRecyclerView.adapter = ActivityStreamAdapter(myActivities, requireContext(), this)
            activityStreamRecyclerView.layoutManager = LinearLayoutManager(context)
        }

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