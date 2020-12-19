package com.cmpe451.platon.page.activity.home.fragment.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
import com.cmpe451.platon.network.models.TrendingProject
import com.cmpe451.platon.network.models.UpcomingEvent
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.util.Definitions
import java.lang.Error

class HomeFragment : Fragment(), TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener, ActivityStreamAdapter.ActivityStreamButtonClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val mHomeViewModel: HomeViewModel by activityViewModels()

    private lateinit var dialog:AlertDialog

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

        setObservers()
        mHomeViewModel.getActivities((activity as HomeActivity).token!!, 0, 5)
        mHomeViewModel.getTrendingProjects(10)
        mHomeViewModel.getUpcomingEvents(0, 5)


    }

    private fun setObservers(){
        mHomeViewModel.getTrendingProjectsResourceResponse.observe(viewLifecycleOwner, {t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    binding.homeTrendingProjectsRecyclerView.adapter =
                            TrendingProjectsAdapter(t.data!!.trending_projects as ArrayList<TrendingProject>, requireContext(), this)

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })


        mHomeViewModel.getUpcomingEventsResourceResponse.observe(viewLifecycleOwner, {t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    binding.homeUpcomingEventsRecyclerView.adapter =
                            UpcomingEventsAdapter(t.data!!.upcoming_events as ArrayList<UpcomingEvent>, requireContext(), this)

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            }


        })
    }

    private fun initViews() {

        val myActivities: ArrayList<ActivityStreamElement> = arrayListOf()

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels

        val layoutManagerTrending = LinearLayoutManager(context)
        val layoutManageUpcoming = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManageActivity = LinearLayoutManager(context)

        binding.homeTrendingProjectsRecyclerView.layoutManager = layoutManagerTrending
        binding.homeUpcomingEventsRecyclerView.layoutManager = layoutManageUpcoming
        binding.homeActivityStreamRecyclerView.layoutManager = layoutManageActivity


        binding.homeActivityStreamRecyclerView.layoutParams = LinearLayout.LayoutParams(width, (height/2))
        //binding.homeUpcomingEventsRecyclerView.layoutParams = LinearLayout.LayoutParams(width/2, height/2)
        binding.homeTrendingProjectsRecyclerView.layoutParams = LinearLayout.LayoutParams(width, (height/2))


        binding.homeActivityStreamRecyclerView.adapter = ActivityStreamAdapter(myActivities, requireContext(), this)

    }


    private fun setListeners() {
        dialog = Definitions().createProgressBar(requireContext())

        mHomeViewModel.getActivityStreamResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java -> {
                    (binding.homeActivityStreamRecyclerView.adapter as ActivityStreamAdapter).submitElements(t.data as ArrayList<ActivityStreamElement>)
                }
                Resource.Error::class.java ->Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })


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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}