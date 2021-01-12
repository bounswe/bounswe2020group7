package com.cmpe451.platon.page.activity.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.*
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.ActivityHomeBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.cmpe451.platon.page.activity.home.fragment.home.HomeFragmentDirections
import com.cmpe451.platon.page.activity.home.fragment.profilepage.ProfilePageFragment
import com.cmpe451.platon.page.activity.home.fragment.profilepage.ProfilePageFragmentDirections
import com.cmpe451.platon.page.activity.home.fragment.workspace.WorkspaceListFragmentDirections
import com.cmpe451.platon.page.activity.login.LoginActivity
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.page.activity.workspace.fragment.issues.IssuesFragmentDirections
import com.cmpe451.platon.util.Definitions
import java.util.*


class HomeActivity : BaseActivity(),
        SearchElementsAdapter.SearchButtonClickListener,
        FollowRequestElementsAdapter.FollowRequestButtonClickListener,
        NotificationElementsAdapter.NotificationButtonClickListener,
        WorkspaceInvitationsAdapter.InvitationButtonClickListener{

    private lateinit var navController: NavController
    lateinit var binding : ActivityHomeBinding
    lateinit var search:SearchView
    private lateinit var dialog: AlertDialog
    private val mActivityViewModel: HomeActivityViewModel by viewModels()

    // when notification handled, will be stored here
    private var handledFollowRequestPosition = -1

    var currUserToken:String = ""
    var currUserId:Int = -1

    private var maxPageNumberToolbarElements=0
    private val toolbarPageSize = 10

    private var jobIdList:ArrayList<Int>  = arrayListOf(-1)
    private var searchHistory:List<SearchHistoryElement> = emptyList()

    private lateinit var toolbarLayoutManager:LinearLayoutManager
    private lateinit var paginationListener:PaginationListener

    /**
     * Clears the toolbar/action bar's state.
     * Notification/search and others
     */
    private fun destroyToolbar(flag: Boolean = true) {
        if(flag){
            // collapse the search action view
            search.onActionViewCollapsed()
        }

        //make GONE the views
        binding.layWsAndUeFilters.visibility = View.GONE
        binding.laySearchUser.visibility=View.GONE
        binding.toolbarRecyclerview.visibility = View.GONE
        binding.rgSearchAmong.visibility = View.GONE
        binding.notificationRg.visibility = View.GONE


        binding.rgSearchAmong.setOnCheckedChangeListener(null)
        binding.notificationRg.setOnCheckedChangeListener(null)

        binding.rgSearchAmong.clearCheck()
        binding.notificationRg.clearCheck()
        // if adapter not null, clear it
        if (binding.toolbarRecyclerview.adapter != null){
            (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
        }

        paginationListener.currentPage = 0
        maxPageNumberToolbarElements = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)

        // if null, close the app
        if (intent.extras?.getString("token") == null || intent.extras?.getInt("user_id") == null){
            finish()
        }else{
            currUserToken = intent.extras?.getString("token")!!
            currUserId= intent.extras?.getInt("user_id")!!
        }
        //inflate
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup home navigation host view fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // prepare action bar
        setSupportActionBar(findViewById(R.id.toolbar))
        //actionbar_login will be able to navigate
        NavigationUI.setupActionBarWithNavController(this, navController)

        initViews()
    }

    private fun initViews() {
        toolbarLayoutManager = LinearLayoutManager(this)
        binding.toolbarRecyclerview.layoutManager = toolbarLayoutManager

        binding.bottomNavBar.setOnNavigationItemSelectedListener {
            // destroy the toolbar when going to another fragment using bottom navbar
            destroyToolbar()
            when(it.itemId){
                R.id.profilePageFragment -> {
                    // get current user's information
                    mActivityViewModel.fetchUser(currUserToken)
                }
            }
            it.onNavDestinationSelected(navController)
        }

        initListeners()
        setObservers()
    }


    private fun setObservers(){
        setObserversForSearch()
        setObserversForNotifications()
    }

    private fun setObserversForNotifications() {
        mActivityViewModel.getInvitationsFromWsResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    //maxPageNumberToolbarElements = t.data!!.number_of_pages
                    if (binding.toolbarRecyclerview.adapter?.javaClass == WorkspaceInvitationsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as WorkspaceInvitationsAdapter).submitElements(
                            t.data!!
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = WorkspaceInvitationsAdapter(
                            t.data!! as ArrayList<WorkspaceInvitation>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getInvitationsFromWsResourceResponse.value = Resource.Done()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    mActivityViewModel.getInvitationsFromWsResourceResponse.value = Resource.Done()
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()

                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })


        mActivityViewModel.getUserNotificationsResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    if (binding.toolbarRecyclerview.adapter?.javaClass == NotificationElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as NotificationElementsAdapter).submitElements(
                            t.data!!.notification_list
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = NotificationElementsAdapter(
                            t.data!!.notification_list as ArrayList<Notification>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getUserNotificationsResourceResponse.value = Resource.Done()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    mActivityViewModel.getUserNotificationsResourceResponse.value = Resource.Done()
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()

                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })

        mActivityViewModel.getUserFollowRequestsResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    if (binding.toolbarRecyclerview.javaClass == FollowRequestElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as FollowRequestElementsAdapter).submitElements(
                            t.data!!.follow_requests
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = FollowRequestElementsAdapter(
                            t.data!!.follow_requests as ArrayList<FollowRequest>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getUserFollowRequestsResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    mActivityViewModel.getUserFollowRequestsResourceResponse.value = Resource.Done()
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })

        mActivityViewModel.acceptRequestResourceResponse.observe(this, { i ->
            when (i.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    if (handledFollowRequestPosition != -1) {
                        (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter)
                            .removeElement(this.handledFollowRequestPosition)
                        this.handledFollowRequestPosition = -1
                    }
                    mActivityViewModel.acceptRequestResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, i.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.acceptRequestResourceResponse.value = Resource.Done()
                }

                Resource.Done::class.java -> dialog.dismiss()
            }
        })
        mActivityViewModel.getInvitationAnswerResourceResponse.observe(this,{i->
            when (i.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    if (handledFollowRequestPosition != -1) {
                        (binding.toolbarRecyclerview.adapter as WorkspaceInvitationsAdapter)
                            .removeElement(this.handledFollowRequestPosition)
                        this.handledFollowRequestPosition = -1
                    }
                    mActivityViewModel.acceptRequestResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, i.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.acceptRequestResourceResponse.value = Resource.Done()
                }

                Resource.Done::class.java -> dialog.dismiss()
            }
        })

    }


    private fun setObserversForSearch() {
        //observer for search history
        mActivityViewModel.getSearchHistoryResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    searchHistory = t.data!!.search_history
                    val historyCursor = MatrixCursor(arrayOf("_id", "query"))
                    searchHistory.forEachIndexed { i, e ->
                        historyCursor.addRow(arrayOf(i, e.query))
                    }
                    // set adapter's cursor with retrieved suggestion items
                    search.suggestionsAdapter.changeCursor(historyCursor)
                    mActivityViewModel.getSearchHistoryResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchHistoryResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })

        // listener for search user results
        mActivityViewModel.getSearchUserResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    // set max number of pages for pagination
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    // already exists, append on it, else create new
                    if (binding.toolbarRecyclerview.adapter?.javaClass == SearchElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as SearchElementsAdapter).submitElements(
                            t.data!!.result_list
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = SearchElementsAdapter(
                            t.data!!.result_list as ArrayList<SearchElement>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })

        mActivityViewModel.getSearchWorkspaceResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    // set max number of pages for pagination
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    // already exists, append on it, else create new
                    if (binding.toolbarRecyclerview.adapter?.javaClass == SearchElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as SearchElementsAdapter).submitElements(
                            t.data!!.result_list
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = SearchElementsAdapter(
                            t.data!!.result_list as ArrayList<SearchElement>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })



        mActivityViewModel.getSearchUpcomingEventResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    // set max number of pages for pagination
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    // already exists, append on it, else create new
                    if (binding.toolbarRecyclerview.adapter?.javaClass == SearchElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as SearchElementsAdapter).submitElements(
                            t.data!!.result_list
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = SearchElementsAdapter(
                            t.data!!.result_list as ArrayList<SearchElement>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getSearchUpcomingEventResourceResponse.value =
                        Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchUpcomingEventResourceResponse.value =
                        Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })


        // listener for all job list
        mActivityViewModel.getJobListResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val aList = arrayListOf("Any")
                    t.data!!.forEach {
                        aList.add(it.name)
                        // fill jobList Id array defined above
                        jobIdList.add(it.id)
                    }
                    binding.spJobQuery.adapter = ArrayAdapter(this, R.layout.spinner_item, aList)
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getJobListResourceResponse.value = Resource.Done()
                }
            }
        })


        mActivityViewModel.getUserDeleteNotificationResourceResponse.observe(this, { i ->
            when (i.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    if (handledFollowRequestPosition != -1) {
                        (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter)
                            .removeElement(this.handledFollowRequestPosition)
                        this.handledFollowRequestPosition = -1
                    }
                    mActivityViewModel.acceptRequestResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, i.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.acceptRequestResourceResponse.value = Resource.Done()
                }

                Resource.Done::class.java -> dialog.dismiss()
            }
        })

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        // pagination listener define
        paginationListener = object: PaginationListener(toolbarLayoutManager, toolbarPageSize){
            override fun loadMoreItems() {
                if(maxPageNumberToolbarElements-1 > currentPage){
                    isLoading = true
                    currentPage++
                    when(binding.rgSearchAmong.visibility){
                        View.GONE -> {
                            when (binding.notificationRg.checkedRadioButtonId) {
                                R.id.personal_ntf_rb -> {
                                    mActivityViewModel.getFollowRequests(
                                        currUserId,
                                        currUserToken, currentPage, PAGE_SIZE
                                    )
                                }
                                R.id.general_ntf_rb -> {
                                    mActivityViewModel.getNotifications(
                                        currUserToken,
                                        currentPage,
                                        PAGE_SIZE
                                    )
                                }
                                R.id.workspace_inv_ntf_rb -> {
                                    mActivityViewModel.getInvitationsFromWs(
                                        currUserToken,
                                        currentPage,
                                        PAGE_SIZE
                                    )
                                }
                            }
                        }
                        View.VISIBLE -> {
                            when (binding.rgSearchAmong.checkedRadioButtonId) {
                                R.id.rb_searchUser -> {
                                    val sortBy = binding.spSortByUser.selectedItemPosition
                                    mActivityViewModel.searchUser(
                                        currUserToken,
                                        search.query.toString().trim(),
                                        if (jobIdList[binding.spJobQuery.selectedItemPosition] == -1) null else jobIdList[binding.spJobQuery.selectedItemPosition],
                                        if (sortBy != 0) sortBy - 1 else null, currentPage,
                                        PAGE_SIZE
                                    )
                                }
                                R.id.rb_searchUpcoming -> {
                                    val sortBy = binding.spSortByUe.selectedItemPosition
                                    val startDateE = binding.etStartDateE.text.toString().trim()
                                    val startDateS = binding.etDeadlineS.text.toString().trim()
                                    val deadlineS = binding.etStartDateS.text.toString().trim()
                                    val deadlineE = binding.etDeadlineE.text.toString().trim()

                                    mActivityViewModel.searchUpcomingEvent(
                                        currUserToken,
                                        search.query.toString().trim(),
                                        if (startDateS.isNotEmpty()) startDateS else null,
                                        if (startDateE.isNotEmpty()) startDateE else null,
                                        if (deadlineS.isNotEmpty()) deadlineS else null,
                                        if (deadlineE.isNotEmpty()) deadlineE else null,
                                        if (sortBy != 0) sortBy - 1 else null,
                                        currentPage,
                                        PAGE_SIZE
                                    )

                                }
                                R.id.rb_searchWorkspace -> {
                                    val sortBy = binding.spSortByWs.selectedItemPosition
                                    val name = binding.etFilterName.text.toString().trim()
                                    val surname = binding.etFilterSurname.text.toString().trim()
                                    val startDateE = binding.etStartDateE.text.toString().trim()
                                    val startDateS = binding.etDeadlineS.text.toString().trim()
                                    val deadlineS = binding.etStartDateS.text.toString().trim()
                                    val deadlineE = binding.etDeadlineE.text.toString().trim()
                                    val event = binding.etFilterEvent.text.toString().trim()

                                    mActivityViewModel.searchWorkspace(
                                        currUserToken,
                                        search.query.toString().trim(),
                                        binding.etFilterSkill.text.toString().trim(),
                                        if (name.isNotEmpty()) name else null,
                                        if (surname.isNotEmpty()) surname else null,
                                        if (startDateS.isNotEmpty()) startDateS else null,
                                        if (startDateE.isNotEmpty()) startDateE else null,
                                        if (deadlineS.isNotEmpty()) deadlineS else null,
                                        if (deadlineE.isNotEmpty()) deadlineE else null,
                                        if (sortBy != 0) sortBy - 1 else null,
                                        if (event.isNotEmpty()) event else null,
                                        currentPage,
                                        PAGE_SIZE
                                    )
                                }
                            }
                        }
                        }
                    }
                }
            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        binding.toolbarRecyclerview.addOnScrollListener(paginationListener)


        binding.spSortByUser.adapter = ArrayAdapter(
            this, R.layout.spinner_item, arrayOf(
                "Semantic Rating",
                "Alphabetical Order(A=>Z)",
                "Alphabetical Order (Z=>A)"
            )
        )
        binding.spSortByWs.adapter = ArrayAdapter(
            this, R.layout.spinner_item,
            arrayOf(
                "Semantic Rating",
                "Ascending Date",
                "Descending Date",
                "Ascending Number of Collaborators Needed",
                "Descending Number of Collaborators Needed",
                "Ascending Alphabetical Order",
                "Descending Alphabetical Order"
            )
        )
        binding.spSortByUe.adapter = ArrayAdapter(
            this, R.layout.spinner_item,
            arrayOf("Semantic Rating", "Alphabetical Order(A=>Z)", "Date Order")
        )

        binding.etStartDateS.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etStartDateS.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etStartDateS.setText("")
                }
                datePickerDialog.show()
            }
            true
        }

        binding.etStartDateE.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etStartDateE.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etStartDateE.setText("")
                }
                datePickerDialog.show()
            }
            true
        }

        binding.etDeadlineE.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etDeadlineE.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etDeadlineE.setText("")
                }
                datePickerDialog.show()
            }
            true
        }

        binding.etDeadlineS.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etDeadlineS.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etDeadlineS.setText("")
                }
                datePickerDialog.show()
            }
            true
        }


        dialog = Definitions().createProgressBar(this as BaseActivity)
    }

    /**
     * Triggered when search button clicked
     */
    private fun onSearchBarClicked() {
        // when clicked on search button
        when(binding.rgSearchAmong.visibility){
            // if visible destroy the toolbar, and remove observers
            View.VISIBLE -> {
                destroyToolbar()
            }
            // if gone, create view
            View.GONE -> {
                // destroy toolbar, but do not collapse search view
                destroyToolbar(false)

                // make radio group visible
                binding.rgSearchAmong.visibility = View.VISIBLE
                binding.toolbarRecyclerview.visibility = View.VISIBLE

                //listener for search radio group
                binding.rgSearchAmong.setOnCheckedChangeListener { _, id ->
                    paginationListener.currentPage = 0
                    if (binding.toolbarRecyclerview.adapter != null) {
                        (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
                    }
                    when (id) {
                        R.id.rb_searchUser -> {
                            binding.layWsAndUeFilters.visibility = View.GONE
                            binding.laySearchUser.visibility = View.VISIBLE
                            mActivityViewModel.fetchSearchHistory(currUserToken, 0)
                            mActivityViewModel.getAllJobs()
                        }
                        R.id.rb_searchWorkspace -> {
                            binding.spSortByUe.visibility = View.GONE
                            binding.spSortByWs.visibility = View.VISIBLE
                            binding.layWsAndUeFilters.visibility = View.VISIBLE
                            binding.layWorkspaceFilter.visibility = View.VISIBLE
                            binding.laySearchUser.visibility = View.GONE
                            mActivityViewModel.fetchSearchHistory(currUserToken, 1)
                        }
                        R.id.rb_searchUpcoming -> {
                            binding.spSortByUe.visibility = View.VISIBLE
                            binding.spSortByWs.visibility = View.GONE

                            binding.layWsAndUeFilters.visibility = View.VISIBLE
                            binding.layWorkspaceFilter.visibility = View.GONE
                            binding.laySearchUser.visibility = View.GONE
                            mActivityViewModel.fetchSearchHistory(currUserToken, 2)
                        }
                    }
                }
            }
        }

        search.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                search.setQuery(searchHistory[position].query, false)
                return true
            }

        })

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                search.clearFocus()
                paginationListener.currentPage = 0
                if (binding.toolbarRecyclerview.adapter != null) {
                    (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
                }

                when (binding.rgSearchAmong.checkedRadioButtonId) {
                    R.id.rb_searchUser -> {
                        val sortBy = binding.spSortByUser.selectedItemPosition
                        mActivityViewModel.searchUser(
                            currUserToken, query,
                            if (jobIdList[binding.spJobQuery.selectedItemPosition] == -1) null else jobIdList[binding.spJobQuery.selectedItemPosition],
                            if (sortBy != 0) sortBy - 1 else null, 0, toolbarPageSize
                        )
                    }
                    R.id.rb_searchWorkspace -> {
                        val sortBy = binding.spSortByWs.selectedItemPosition
                        val name = binding.etFilterName.text.toString().trim()
                        val surname = binding.etFilterSurname.text.toString().trim()
                        val startDateE = binding.etStartDateE.text.toString().trim()
                        val startDateS = binding.etDeadlineS.text.toString().trim()
                        val deadlineS = binding.etStartDateS.text.toString().trim()
                        val deadlineE = binding.etDeadlineE.text.toString().trim()
                        val event = binding.etFilterEvent.text.toString().trim()

                        mActivityViewModel.searchWorkspace(
                            currUserToken,
                            search.query.toString().trim(),
                            binding.etFilterSkill.text.toString().trim(),
                            if (name.isNotEmpty()) name else null,
                            if (surname.isNotEmpty()) surname else null,
                            if (startDateS.isNotEmpty()) startDateS else null,
                            if (startDateE.isNotEmpty()) startDateE else null,
                            if (deadlineS.isNotEmpty()) deadlineS else null,
                            if (deadlineE.isNotEmpty()) deadlineE else null,
                            if (sortBy != 0) sortBy - 1 else null,
                            if (event.isNotEmpty()) event else null,
                            0,
                            toolbarPageSize
                        )
                    }
                    R.id.rb_searchUpcoming -> {
                        val sortBy = binding.spSortByUe.selectedItemPosition
                        val startDateE = binding.etStartDateE.text.toString().trim()
                        val startDateS = binding.etDeadlineS.text.toString().trim()
                        val deadlineS = binding.etStartDateS.text.toString().trim()
                        val deadlineE = binding.etDeadlineE.text.toString().trim()
                        mActivityViewModel.searchUpcomingEvent(
                            currUserToken,
                            search.query.toString().trim(),
                            if (startDateS.isNotEmpty()) startDateS else null,
                            if (startDateE.isNotEmpty()) startDateE else null,
                            if (deadlineS.isNotEmpty()) deadlineS else null,
                            if (deadlineE.isNotEmpty()) deadlineE else null,
                            if (sortBy != 0) sortBy - 1 else null,
                            0,
                            toolbarPageSize
                        )

                    }
                }
                return true
            }
        })
    }


    private fun onSeeNotificationsClicked() {
        // arrange view of notification radio group
        when(binding.notificationRg.visibility){
            View.VISIBLE -> {
                destroyToolbar()
            }
            View.GONE -> {
                destroyToolbar()

                //listener for notification radio group
                binding.notificationRg.setOnCheckedChangeListener { _, id ->
                    paginationListener.currentPage = 0
                    if (binding.toolbarRecyclerview.adapter != null) {
                        (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
                    }
                    when (id) {
                        R.id.general_ntf_rb -> {
                            mActivityViewModel.getNotifications(currUserToken, 0, toolbarPageSize)
                        }
                        R.id.personal_ntf_rb -> {
                            mActivityViewModel.getFollowRequests(
                                currUserId,
                                currUserToken,
                                0,
                                toolbarPageSize
                            )
                        }
                        R.id.workspace_inv_ntf_rb -> {
                            mActivityViewModel.getInvitationsFromWs(
                                currUserToken,
                                0,
                                toolbarPageSize
                            )
                        }
                    }
                }

                binding.notificationRg.visibility = View.VISIBLE
                binding.toolbarRecyclerview.visibility = View.VISIBLE
            }
        }
    }

    private fun onLogOutButtonClicked(){
        val sharedPrefs = getSharedPreferences("token_file", 0)
        sharedPrefs.edit().remove("mail").apply()
        sharedPrefs.edit().remove("pass").apply()
        navController.navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToLottieFragment())
        binding.bottomNavBar.visibility = View.INVISIBLE

    }

    fun lottieEndded() {
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_menu_btn -> {
                destroyToolbar()
                onLogOutButtonClicked()
            }
            R.id.notification_btn -> onSeeNotificationsClicked()
            R.id.add_workspace_btn -> onAddWorkspaceClicked()
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun onAddWorkspaceClicked(){
        val bnd = Bundle()
        bnd.putString("token", currUserToken)
        bnd.putInt("user_id", currUserId)
        bnd.putBoolean("add", true)
        bnd.putInt("workspace_id", -1)
        startActivity(Intent(this, WorkspaceActivity::class.java).putExtras(bnd))
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        destroyToolbar()
        when(navController.currentDestination?.id){
            R.id.followFragment -> binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
            R.id.homeFragment -> {
                AlertDialog.Builder(this)
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("EXIT") { _, _ ->
                        finish()
                    }
                    .setNegativeButton("No", null)
                    .create().show()
            }
            R.id.workspaceListFragment -> binding.bottomNavBar.selectedItemId = R.id.homeFragment
            R.id.profilePageFragment -> binding.bottomNavBar.selectedItemId = R.id.homeFragment
            else -> {
                navController.navigateUp()
            }
        }
        return super.onSupportNavigateUp()
    }

    override fun onSearchButtonClicked(element: SearchElement, position: Int) {
        when(binding.bottomNavBar.selectedItemId) {
            R.id.workspaceListFragment -> {
                if (element.name != null) {
                    if (element.id != currUserId) {
                        navController.navigate(
                            WorkspaceListFragmentDirections.actionWorkspaceListFragmentToOtherProfileFragment(
                                element.id
                            )
                        )
                    } else {
                        binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                    }
                } else {
                    val collabIds = element.contributor_list!!.map { it.id }
                    val bnd = Bundle()
                    bnd.putString("token", currUserToken)
                    bnd.putInt("user_id", currUserId)
                    bnd.putBoolean("add", false)
                    bnd.putInt("workspace_id", element.id)
                    bnd.putBoolean("isOwner", true)
                    if (collabIds.contains(currUserId)) {
                        bnd.putBoolean("isOwner", true)
                    } else {
                        bnd.putBoolean("isOwner", false)
                    }
                    startActivity(Intent(this, WorkspaceActivity::class.java).putExtras(bnd))
                }

            }
            R.id.homeFragment -> {
                if (element.name != null) {
                    if (element.id != currUserId) {
                        navController.navigate(
                            HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(
                                element.id
                            )
                        )
                    } else {
                        binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                    }
                } else if (element.state != null) {
                    val collabIds = element.contributor_list!!.map { it.id }
                    val bnd = Bundle()
                    bnd.putString("token", currUserToken)
                    bnd.putInt("user_id", currUserId)
                    bnd.putBoolean("add", false)
                    bnd.putInt("workspace_id", element.id)
                    bnd.putBoolean("isOwner", true)
                    if (collabIds.contains(currUserId)) {
                        bnd.putBoolean("isOwner", true)
                    } else {
                        bnd.putBoolean("isOwner", false)
                    }
                    startActivity(Intent(this, WorkspaceActivity::class.java).putExtras(bnd))
                } else {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(element.link))
                    startActivity(browserIntent)
                }

            }
        }
        destroyToolbar()
    }

    override fun onNotificationButtonClicked(ntf: Notification, position: Int) {
    }

    override fun onDeleteNotificationClicked(ntf: Notification, position: Int) {
       mActivityViewModel.deleteNotification(ntf.id, currUserToken)
        this.handledFollowRequestPosition = position
    }

    override fun onFollowRequestNameClicked(request: FollowRequest, position: Int) {
        when(binding.bottomNavBar.selectedItemId) {
            R.id.workspaceListFragment -> {
                if (request.id != currUserId) {
                    navController.navigate(
                        WorkspaceListFragmentDirections.actionWorkspaceListFragmentToOtherProfileFragment(
                            request.id
                        )
                    )
                } else {
                    binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                }
            }
            R.id.homeFragment -> {
                if (request.id != currUserId) {
                    navController.navigate(
                        HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(
                            request.id
                        )
                    )
                } else {
                    binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                }
            }
        }
        destroyToolbar()
    }

    override fun onFollowRequestAcceptClicked(request: FollowRequest, position: Int) {
        mActivityViewModel.acceptFollowRequest(request.id, currUserId, currUserToken)
        this.handledFollowRequestPosition = position
    }

    override fun onFollowRequestRejectClicked(request: FollowRequest, position: Int) {
        mActivityViewModel.deleteFollowRequest(request.id, currUserId, currUserToken)
        this.handledFollowRequestPosition = position
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_home, menu)
        search = (menu?.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setOnSearchClickListener{
            onSearchBarClicked()
        }
        //define suggestion adapter
        search.suggestionsAdapter = SimpleCursorAdapter(
            this,
            R.layout.spinner_item,
            null,
            arrayOf("query"),
            arrayOf(R.id.tv_spinner).toIntArray(),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        //listener for close button click
        search.setOnCloseListener {
            //destroy toolbar
            destroyToolbar()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onInvitationAcceptClicked(request: WorkspaceInvitation, position: Int) {
        mActivityViewModel.answerWorkspaceInvitations(request.invitation_id, 1,  currUserToken)
        handledFollowRequestPosition = position
    }

    override fun onInvitationRejectClicked(request: WorkspaceInvitation, position: Int) {
        mActivityViewModel.answerWorkspaceInvitations(request.invitation_id, 0, currUserToken)
        handledFollowRequestPosition = position
    }

}