package com.cmpe451.platon.page.activity.home.fragment.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.*
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.ActivityStreamElement
import com.cmpe451.platon.network.models.TrendingProject
import com.cmpe451.platon.network.models.UpcomingEvent
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions

/*
 *  It consists of the UI Code, data bindings and general logic of application
 */

class HomeFragment : Fragment(), TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener,
    ActivityStreamAdapter.ActivityStreamButtonClickListener, CalendarAdapter.CalendarButtonClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val mHomeViewModel: HomeViewModel by activityViewModels()

    private var maxPageNumberUpcoming:Int=0;
    private lateinit var paginationListener:PaginationListener
    private var maxPageNumberActivity:Int=0
    private var pageSize:Int = 20

    private lateinit var dialog:AlertDialog
    private lateinit var calendarDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /*
     *  Creates and returns the view hierarchy associated with the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root

    }

    /*
     *  After view creation listeners and observers implemented
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setListeners()

        setObservers()
        mHomeViewModel.getActivities((activity as HomeActivity).currUserToken, 0, pageSize)
        mHomeViewModel.getTrendingProjects(10)
        mHomeViewModel.getUpcomingEvents(0, 5)


    }

    /*
     *  Click on top menu bar items handled
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.calendar_btn->{
                onCalendarClicked()
            }
        }
        return item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
    }

    /*
     *  Observers of the view model responses handled
     */
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
                    maxPageNumberUpcoming = t.data!!.number_of_pages
                    (binding.homeUpcomingEventsRecyclerView.adapter as UpcomingEventsAdapter).submitList(
                        t.data!!.upcoming_events!! as ArrayList<UpcomingEvent>)

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            }


        })
    }

    /*
     *  View initialization handled
     *  Also layout and adapter of recycler views added
     */
    private fun initViews() {

        val myActivities: ArrayList<ActivityStreamElement> = arrayListOf()

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels

        val layoutManagerTrending = LinearLayoutManager(context)
        val layoutManageUpcoming = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManageActivity = LinearLayoutManager(context)

        paginationListener = object: PaginationListener(layoutManageActivity, pageSize){
            override fun loadMoreItems() {
                if(maxPageNumberActivity-1 > currentPage){
                    isLoading = true
                    currentPage++
                    mHomeViewModel.getActivities((activity as HomeActivity).currUserToken, currentPage, pageSize)

                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        binding.homeTrendingProjectsRecyclerView.layoutManager = layoutManagerTrending
        binding.homeUpcomingEventsRecyclerView.layoutManager = layoutManageUpcoming
        binding.homeUpcomingEventsRecyclerView.adapter = UpcomingEventsAdapter(ArrayList(),requireContext(), this)
        binding.homeUpcomingEventsRecyclerView.addOnScrollListener(object: PaginationListener(layoutManageUpcoming){
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



        binding.homeActivityStreamRecyclerView.layoutManager = layoutManageActivity


        binding.homeActivityStreamRecyclerView.layoutParams = LinearLayout.LayoutParams(width, (height/2))
        binding.homeTrendingProjectsRecyclerView.layoutParams = LinearLayout.LayoutParams(width, (height/2))
        binding.homeActivityStreamRecyclerView.adapter = ActivityStreamAdapter(myActivities, requireContext(), this)

        binding.homeActivityStreamRecyclerView.addOnScrollListener(paginationListener)



    }

    /*
     *  Click on calendar handled
     */
    private fun onCalendarClicked() {
        mHomeViewModel.getCalendar((requireActivity() as HomeActivity).currUserToken)

        mHomeViewModel.getCalendarResourceResponse.observe(viewLifecycleOwner) { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mHomeViewModel.getCalendarResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
                Resource.Success::class.java -> {
                    if (t.data!!.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No personal events found",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val calendarBinding = DialogCalendarBinding.inflate(
                            layoutInflater,
                            requireView().parent as ViewGroup,
                            false
                        )
                        calendarDialog = AlertDialog.Builder(context).setView(calendarBinding.root)
                            .setCancelable(true).create()
                        calendarDialog.show()
                        calendarBinding.calendarRv.adapter =
                            CalendarAdapter(ArrayList(), requireContext(), this)
                        calendarBinding.calendarRv.layoutManager =
                            LinearLayoutManager(requireContext())
                        (calendarBinding.calendarRv.adapter as CalendarAdapter).submitElements(t.data!!)
                        calendarBinding.calendarTitleTv.setOnClickListener {
                            calendarDialog.dismiss()
                        }
                    }
                    mHomeViewModel.getCalendarResourceResponse.value = Resource.Done()
                }
            }
        }
    }

    /*
     *  Observers of the view model responses added
     */
    private fun setListeners() {
        dialog = Definitions().createProgressBar(requireContext())

        mHomeViewModel.getActivityStreamResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java -> {
                    maxPageNumberActivity = t.data!!.totalItems
                    (binding.homeActivityStreamRecyclerView.adapter as ActivityStreamAdapter).submitElements(t.data!!.orderedItems as ArrayList<ActivityStreamElement>)
                }
                Resource.Error::class.java ->Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    /*
     *  Click on trending projects recycler view cell handled
     */
    override fun onTrendingProjectButtonClicked(binding: TrendProjectCellBinding, project:TrendingProject) {
        val collabIds = project.contributor_list!!.map{it.id}
        val bnd = Bundle()
        bnd.putString("token", (activity as HomeActivity).currUserToken)
        bnd.putInt("user_id", (activity as HomeActivity).currUserId)
        bnd.putBoolean("add", false)
        bnd.putInt("workspace_id", project.id)
        bnd.putBoolean("isOwner", true)
        if(collabIds.contains((activity as HomeActivity).currUserId)){
            bnd.putBoolean("isOwner", true)
        }
        else {
            bnd.putBoolean("isOwner", false)
        }
        startActivity(Intent(activity, WorkspaceActivity::class.java).putExtras(bnd))
    }

    /*
     *  Click on upcoming events recycler view cell handled
     */
    override fun onUpcomingButtonClicked(binding: UpcomingEventCellBinding, position: Int) {
        if (binding.expandLl.visibility == View.GONE){
            binding.expandLl.visibility = View.VISIBLE
        }else{
            binding.expandLl.visibility = View.GONE
        }
        binding.expandLl.refreshDrawableState()

        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onActivityStreamButtonClicked(binding: ActivityStreamCellBinding, position: Int) {
        //todo
    }

    /*
     *  Visibilities of top menu bar items handled
     */
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        menu.findItem(R.id.calendar_btn)?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCalendarItemClicked(wsId: Int) {
        val bnd = Bundle()
        bnd.putString("token", (activity as HomeActivity).currUserToken)
        bnd.putInt("user_id", (activity as HomeActivity).currUserId)
        bnd.putBoolean("add", false)
        bnd.putInt("workspace_id", wsId)
        bnd.putBoolean("isOwner", true)
        calendarDialog.dismiss()
        startActivity(Intent(activity, WorkspaceActivity::class.java).putExtras(bnd))
    }
}