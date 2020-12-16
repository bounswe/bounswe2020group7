package com.cmpe451.platon.page.activity.home

import android.app.AlertDialog
import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.FollowRequestElementsAdapter
import com.cmpe451.platon.adapter.NotificationElementsAdapter
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.adapter.ToolbarElementsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.ActivityHomeBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.cmpe451.platon.page.activity.home.fragment.home.HomeFragmentDirections
import com.cmpe451.platon.page.activity.home.fragment.workspace.WorkspaceListFragmentDirections
import com.cmpe451.platon.page.activity.login.LoginActivity
import com.cmpe451.platon.util.Definitions
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity(),
        SearchElementsAdapter.SearchButtonClickListener,
        FollowRequestElementsAdapter.FollowRequestButtonClickListener,
        NotificationElementsAdapter.NotificationButtonClickListener {

    private lateinit var navController: NavController
    lateinit var binding : ActivityHomeBinding
    lateinit var search:SearchView
    private lateinit var dialog: AlertDialog
    private val mActivityViewModel: HomeActivityViewModel by viewModels()

    // when notification handled, will be stored here
    private var handledFollowRequestPosition = -1
    var token:String? = null
    var userId:Int? = null

    //keeper for jobs'ids
    //TODO this can be improved
    private var jobIdList:ArrayList<Int>?  = null


    /**
     * Clears the toolbar/action bar's state.
     * Notification/search and others
     */
    private fun destroyToolbar(flag:Boolean=true) {
        if(flag){
            // collapse the search action view
            search.onActionViewCollapsed()
        }

        binding.layJobQuery.visibility=View.GONE
        binding.notificationRg.visibility = View.GONE
        binding.rgSearchAmong.visibility = View.GONE

        binding.rgSearchAmong.setOnCheckedChangeListener(null)
        binding.notificationRg.setOnCheckedChangeListener(null)

        binding.rgSearchAmong.clearCheck()
        binding.notificationRg.clearCheck()
        // if adapter not null, clear it
        if (binding.toolbarRecyclerview.adapter != null){
            (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
        }

        //make GONE the views
        mActivityViewModel.getSearchUserResourceResponse.removeObservers(this)
        mActivityViewModel.getSearchHistoryResourceResponse.removeObservers(this)
        mActivityViewModel.getJobListResourceResponse.removeObservers(this)

        mActivityViewModel.getUserNotificationsResourceResponse.removeObservers(this)
        mActivityViewModel.getUserFollowRequestsResourceResponse.removeObservers(this)
        mActivityViewModel.acceptRequestResourceResponse.removeObservers(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)

        // get user id and token from login
        token = intent.extras?.getString("token")
        userId = intent.extras?.getInt("user_id")

        // if null, close the app
        if (token == null || userId == null){
            finish()
        }
        //inflate
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup home navigation host view fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // prepare action bar
        setSupportActionBar(findViewById(R.id.toolbar))
        //actionbar will be able to navigate
        NavigationUI.setupActionBarWithNavController(this, navController)

        initViews()
    }

    private fun initViews() {
        // init layout manager of toolbar recycler view
        binding.toolbarRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.bottomNavBar.setOnNavigationItemSelectedListener {
            // destroy the toolbar when going to another fragment using bottom navbar
            destroyToolbar()
            when(it.itemId){
                R.id.profilePageFragment ->{
                    // get current user's information
                    mActivityViewModel.fetchUser(token)
                }
            }
            it.onNavDestinationSelected(navController)
        }

        initListeners()
    }

    private fun initListeners() {
        // create dialog, which is not singleton
        //TODO make alert dialog singleton
        dialog = Definitions().createProgressBar(this as BaseActivity)
    }

    /**
     * Triggered when search button clicked
     */
    private fun onSearchBarClicked() {
        // keeper for searchHistory
        var searchHistory: List<SearchHistoryElement>? = null

        // when clicked on search button
        when(binding.rgSearchAmong.visibility){
            // if visible destroy the toolbar, and remove observers
            View.VISIBLE ->{
                destroyToolbar()
            }
            // if gone, create view
            View.GONE -> {
                // destroy toolbar, but do not collapse search view
                destroyToolbar(false)

                //listener for search radio group
                binding.rgSearchAmong.setOnCheckedChangeListener { _, id ->
                    when(id){
                        R.id.rb_searchUser -> {
                            binding.layJobQuery.visibility = View.VISIBLE
                            mActivityViewModel.fetchSearchHistory(token!!, 0)
                            mActivityViewModel.getAllJobs()
                        }
                        R.id.rb_searchWorkspace ->{
                            binding.layJobQuery.visibility = View.GONE
                            mActivityViewModel.fetchSearchHistory(token!!, 1)
                        }
                        R.id.rb_searchUpcoming ->{
                            binding.layJobQuery.visibility = View.GONE
                            mActivityViewModel.fetchSearchHistory(token!!, 2)
                        }
                    }}

                // make radio group visible
                binding.rgSearchAmong.visibility = View.VISIBLE

                //observer for search history
                mActivityViewModel.getSearchHistoryResourceResponse.observe(this, { t ->
                    when (t.javaClass) {
                        Resource.Loading::class.java -> dialog.show()
                        Resource.Success::class.java -> {
                            searchHistory = t.data!!.search_history
                            val historyCursor = MatrixCursor(arrayOf("_id", "query"))
                            searchHistory!!.forEachIndexed { i, e ->
                                historyCursor.addRow(arrayOf(i, e.query))
                            }
                            // set adapter's cursor with retrieved suggestion items
                            search.suggestionsAdapter.changeCursor(historyCursor)

                            dialog.dismiss()
                        }
                        Resource.Error::class.java -> {
                            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                })

                // listener for search user results
                mActivityViewModel.getSearchUserResourceResponse.observe(this, { t ->
                    when (t.javaClass) {
                        Resource.Loading::class.java -> dialog.show()
                        Resource.Success::class.java -> {
                            // define new adapter
                            binding.toolbarRecyclerview.adapter = SearchElementsAdapter(t.data!!.result_list as ArrayList<SearchElement>, this, this)
                            dialog.dismiss()
                        }
                        Resource.Error::class.java -> {
                            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                })

                // listener for all job list
                mActivityViewModel.getJobListResourceResponse.observe(this, { t ->
                    when (t.javaClass) {
                        Resource.Loading::class.java -> dialog.show()
                        Resource.Success::class.java -> {
                            val aList = arrayListOf("Any")
                            jobIdList = arrayListOf(-1)
                            t.data!!.forEach {
                                aList.add(it.name)
                                // fill joblistId array defined above
                                jobIdList!!.add(it.id)
                            }
                            binding.spJobQuery.adapter = ArrayAdapter(this, R.layout.spinner_item, aList)

                            dialog.dismiss()
                        }
                        Resource.Error::class.java -> {
                            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                })

            }
        }


        search.setOnSuggestionListener(object: SearchView.OnSuggestionListener{
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                search.setQuery(searchHistory?.get(position)!!.query ,false)
                return true
            }

        })

        search.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                when(binding.rgSearchAmong.checkedRadioButtonId){
                    R.id.rb_searchUser ->{
                        var jobQuery:Int? = null
                        val pos = binding.spJobQuery.selectedItemPosition
                        if (pos != 0){
                            jobQuery = jobIdList?.get(pos)
                        }
                        mActivityViewModel.searchUser(token!!, query, jobQuery,null, null)
                    }
                    R.id.rb_searchWorkspace ->{}
                    R.id.rb_searchUpcoming -> {}}
                return true
            } })
    }


    private fun onSeeNotificationsClicked() {
        // arrange view of notification radio group
        when(binding.notificationRg.visibility){
            View.VISIBLE ->{
                destroyToolbar()
            }
            View.GONE ->{
                destroyToolbar()
                //listener for notification radio group
                binding.notificationRg.setOnCheckedChangeListener { d, id->
                    when(id){
                        R.id.general_ntf_rb -> mActivityViewModel.getNotifications(token!!)
                        R.id.personal_ntf_rb -> mActivityViewModel.getFollowRequests(userId!!, token!!)
                    }
                }

                binding.notificationRg.visibility = View.VISIBLE

                mActivityViewModel.getUserNotificationsResourceResponse.observe(this, Observer{ t->
                    when(t.javaClass){
                        Resource.Success::class.java ->{
                            binding.toolbarRecyclerview.adapter = NotificationElementsAdapter(t.data!!.notification_list as ArrayList<Notification>,this, this)
                            dialog.dismiss()
                        }
                        Resource.Loading::class.java -> dialog.show()
                        Resource.Error::class.java -> {
                            destroyToolbar()
                            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                })

                mActivityViewModel.getUserFollowRequestsResourceResponse.observe(this, Observer{ t->
                    when(t.javaClass){
                        Resource.Success::class.java ->{
                            binding.toolbarRecyclerview.adapter = FollowRequestElementsAdapter(t.data!!.follow_requests as ArrayList<FollowRequest>,this, this)
                            dialog.dismiss()
                        }
                        Resource.Loading::class.java -> dialog.show()
                        Resource.Error::class.java -> {
                            destroyToolbar()
                            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                })

                mActivityViewModel.acceptRequestResourceResponse.observe(this, Observer{i->
                    when(i.javaClass){
                        Resource.Success::class.java -> {
                            if (this.handledFollowRequestPosition != -1){
                                (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter)
                                        .removeElement(this.handledFollowRequestPosition)
                                this.handledFollowRequestPosition = -1
                            }
                            dialog.dismiss()
                        }
                        Resource.Loading::class.java -> dialog.show()
                        Resource.Error::class.java-> {
                            Toast.makeText(this, i.message, Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                })
            }
        }
    }

    private fun onLogOutButtonClicked(){
        val sharedPrefs = getSharedPreferences("token_file", 0)
        sharedPrefs.edit().remove("mail").apply()
        sharedPrefs.edit().remove("pass").apply()
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
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        destroyToolbar()
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onSearchButtonClicked(element: SearchElement, position: Int) {
        when(binding.bottomNavBar.selectedItemId) {
            R.id.workspaceListFragment -> {
                if (element.id != userId) {
                    navController.navigate(WorkspaceListFragmentDirections.actionWorkspaceListFragmentToOtherProfileFragment(element.id))
                } else {
                    binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                }
            }
            R.id.homeFragment -> {
                if (element.id != userId) {
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(element.id))
                } else {
                    binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                }
            }
        }
        destroyToolbar()
    }

    override fun onNotificationButtonClicked(ntf: Notification, position: Int) {
    }

    override fun onFollowRequestNameClicked(request: FollowRequest, position: Int) {
        when(binding.bottomNavBar.selectedItemId) {
            R.id.workspaceListFragment -> {
                if (request.id != userId) {
                    navController.navigate(WorkspaceListFragmentDirections.actionWorkspaceListFragmentToOtherProfileFragment(request.id))
                } else {
                    binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                }
            }
            R.id.homeFragment -> {
                if (request.id != userId) {
                    navController.navigate(HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(request.id))
                } else {
                    binding.bottomNavBar.selectedItemId = R.id.profilePageFragment
                }
            }
        }
        destroyToolbar()
    }

    override fun onFollowRequestAcceptClicked(request: FollowRequest, position: Int) {
        if(mActivityViewModel.getUserResourceResponse.value!=null && token!=null){
            mActivityViewModel.acceptFollowRequest(request.id, mActivityViewModel.getUserResourceResponse.value?.data?.id!!, token!!)
        }
        this.handledFollowRequestPosition = position
    }

    override fun onFollowRequestRejectClicked(request: FollowRequest, position: Int) {
        if(mActivityViewModel.getUserResourceResponse.value!=null && token!=null){
            mActivityViewModel.deleteFollowRequest(request.id, mActivityViewModel.getUserResourceResponse.value?.data?.id!!, token!!)
        }
        this.handledFollowRequestPosition = position
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        search = (menu?.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setOnSearchClickListener{
            onSearchBarClicked()
        }
        //define suggestion adapter
        search.suggestionsAdapter = SimpleCursorAdapter(this,
                R.layout.spinner_item,
                null,
                arrayOf("query"),
                arrayOf(R.id.tv_spinner).toIntArray(),
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

        //listener for close button click
        search.setOnCloseListener {
            //destroy toolbar
            destroyToolbar()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

}