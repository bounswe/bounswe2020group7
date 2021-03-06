package com.cmpe451.platon.page.activity.login.fragment.landing

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.ActivityStreamAdapter
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.adapter.UpcomingEventsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentLandingBinding
import com.cmpe451.platon.databinding.TrendProjectCellBinding
import com.cmpe451.platon.databinding.UpcomingEventCellBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.ActivityStreamElement
import com.cmpe451.platon.network.models.TrendingProject
import com.cmpe451.platon.network.models.UpcomingEvent
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.fragment.home.HomeViewModel
import com.cmpe451.platon.util.Definitions

/*
 *  It consists of the UI Code, data bindings and general logic of application
 */
class LandingFragment : Fragment(),TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener  {

    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView

    private lateinit var dialog: AlertDialog
    private val mHomeViewModel:HomeViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var binding: FragmentLandingBinding

    private var maxPageNumberUpcoming:Int=0

    /*
     *  Creates and returns the view hierarchy associated with the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLandingBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        sharedPreferences = activity?.getSharedPreferences("token_file", Context.MODE_PRIVATE)!!
        return binding.root
    }

    /*
     *  After view creation listeners and observers implemented
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!doAutoLogin()){
            initViews()
            setListeners()
            setObservers()
            mHomeViewModel.getTrendingProjects(10)
            mHomeViewModel.getUpcomingEvents(0, 5)
        }
    }

    private fun doAutoLogin():Boolean{
        val mail = sharedPreferences.getString("mail", null)
        val pass = sharedPreferences.getString("pass", null)
        if(mail != null && pass != null){
            findNavController().navigate(LandingFragmentDirections.actionLandingFragmentToLoginFragment())
            return true
        }
        return false
    }

    /*
     *  Observers of the view model responses handled
     */
    private fun setObservers(){
        mHomeViewModel.getTrendingProjectsResourceResponse.observe(viewLifecycleOwner, {t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    binding.preLoginTrendingProjectsRecyclerView.adapter =
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
                    maxPageNumberUpcoming = t.data!!.number_of_pages
                    (binding.preLoginUpcomingEventsRecyclerView.adapter as UpcomingEventsAdapter).submitList(
                        t.data!!.upcoming_events!! as ArrayList<UpcomingEvent>)                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /*
     *  Layout managers and adapters of recycler view added
     */
    private fun initViews() {

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels

        val layoutManagerTrending = LinearLayoutManager(context)
        val layoutManageUpcoming = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.preLoginTrendingProjectsRecyclerView.layoutManager = layoutManagerTrending
        binding.preLoginUpcomingEventsRecyclerView.layoutManager = layoutManageUpcoming
        binding.preLoginUpcomingEventsRecyclerView.adapter = UpcomingEventsAdapter(ArrayList(),requireContext(), this)
        binding.preLoginUpcomingEventsRecyclerView.addOnScrollListener(object: PaginationListener(layoutManageUpcoming){
            override fun loadMoreItems() {
                if(maxPageNumberUpcoming-1 > currentPage){
                    currentPage++
                    mHomeViewModel.getUpcomingEvents(currentPage, 5)
                    Toast.makeText(requireContext(), "Next page", Toast.LENGTH_LONG).show()
                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        })

        binding.preLoginUpcomingEventsRecyclerView .layoutParams = LinearLayout.LayoutParams(width, (height/2))
        //binding.homeUpcomingEventsRecyclerView.layoutParams = LinearLayout.LayoutParams(width/2, height/2)
        binding.preLoginTrendingProjectsRecyclerView.layoutParams = LinearLayout.LayoutParams(width, (height/2))

    }


    private fun setListeners() {
        dialog = Definitions().createProgressBar(requireContext())
        //password.addTextChangedListener(textWatcher)
    }

    /*
     *  Click on upcoming event handled
     */
    override fun onUpcomingButtonClicked(binding: UpcomingEventCellBinding, position:Int) {
        if (binding.expandLl.visibility == View.GONE){
            binding.expandLl.visibility = View.VISIBLE
        }else{
            binding.expandLl.visibility = View.GONE
        }

        binding.expandLl.refreshDrawableState()

        Definitions().vibrate(50, activity as BaseActivity)
    }


    /*
     *  Click on trending project handled
     */
    override fun onTrendingProjectButtonClicked(binding: TrendProjectCellBinding, project:TrendingProject) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }
}