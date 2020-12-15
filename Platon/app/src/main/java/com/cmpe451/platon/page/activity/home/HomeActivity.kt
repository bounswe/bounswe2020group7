package com.cmpe451.platon.page.activity.home

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.app.SearchManager
import android.app.SearchableInfo
import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
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
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.FollowRequestElementsAdapter
import com.cmpe451.platon.adapter.NotificationElementsAdapter
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.adapter.ToolbarElementsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cmpe451.platon.databinding.ActivityHomeBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.FollowRequest
import com.cmpe451.platon.network.models.Job
import com.cmpe451.platon.network.models.Notification
import com.cmpe451.platon.network.models.SearchHistoryElement
import com.cmpe451.platon.page.activity.home.fragment.editprofile.EditProfileViewModel
import com.cmpe451.platon.page.activity.login.LoginActivity
import com.cmpe451.platon.page.activity.home.fragment.home.HomeFragmentDirections
import com.cmpe451.platon.util.Definitions

class HomeActivity : BaseActivity(),
        SearchElementsAdapter.SearchButtonClickListener,
        FollowRequestElementsAdapter.FollowRequestButtonClickListener,
        NotificationElementsAdapter.NotificationButtonClickListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    lateinit var binding : ActivityHomeBinding
    lateinit var search:SearchView
    private lateinit var dialog: AlertDialog
    private val mActivityViewModel: HomeActivityViewModel by viewModels()

    private var handledFollowRequestPosition: Int = -1
    var token:String? = null
    var user_id:Int? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        destroyToolbar()
        it.onNavDestinationSelected(navController)
    }

    private fun destroyToolbar(flag:Boolean=true) {
        if(flag){
            search.onActionViewCollapsed()
        }
        binding.layJobQuery.visibility=View.GONE
        binding.notificationRg.visibility = View.GONE
        binding.rgSearchAmong.visibility = View.GONE
        if (binding.toolbarRecyclerview.adapter != null){
            (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)

        token = intent.extras?.getString("token")
        user_id = intent.extras?.getInt("user_id")

        if (token == null || user_id == null){
            finish()
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)
        
        NavigationUI.setupWithNavController(binding.bottomNavBar, navController)
        binding.bottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initViews()
    }

    private fun initViews() {
        mActivityViewModel.fetchUser(token)
        initListeners()
    }

    private fun initListeners() {
        dialog = Definitions().createProgressBar(this as BaseActivity)
        binding.notificationRg.setOnCheckedChangeListener { t, id->
            when(id){
                R.id.general_ntf_rb -> mActivityViewModel.getNotifications(token!!)
                R.id.personal_ntf_rb ->mActivityViewModel.getFollowRequests(mActivityViewModel.getUserResourceResponse.value?.data!!.id, token!!)
            }
        }

        binding.rgSearchAmong.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
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
    }
    private var jobIdList:ArrayList<Int>?  = null

    private fun onSearchBarClicked() {
        var searchHistory: List<SearchHistoryElement>? = null

        search.suggestionsAdapter = SimpleCursorAdapter(this,
                R.layout.spinner_item,
                null,
                arrayOf("query"),
                arrayOf(R.id.tv_spinner).toIntArray(),
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

        search.setOnCloseListener {
            destroyToolbar()
            true
        }

        mActivityViewModel.getSearchHistoryResourceResponse.observe(this, { t->
            when(t.javaClass){
                Resource.Loading::class.java -> {
                    dialog.show()
                }
                Resource.Success::class.java ->{
                    searchHistory = t.data!!.search_history

                    val historyCursor = MatrixCursor(arrayOf("_id", "query"))
                    searchHistory!!.forEachIndexed{ i, e ->
                        historyCursor.addRow(arrayOf(i, e.query))
                    }
                    search.suggestionsAdapter.changeCursor(historyCursor)
                    dialog.dismiss()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })

        mActivityViewModel.getSearchUserResourceResponse.observe(this, {t->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    binding.toolbarRecyclerview.adapter = SearchElementsAdapter(t.data!!.result_list
                            .map { it.name + " " + it.surname+ " " + it.is_private.toString()} as ArrayList<String>,this, this)
                    binding.toolbarRecyclerview.layoutManager = LinearLayoutManager(this)
                    dialog.dismiss()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })

        mActivityViewModel.getJobListResourceResponse.observe(this, { t->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    val aList = arrayListOf("Any")
                    jobIdList = arrayListOf(-1)
                    t.data!!.forEach {
                        aList.add(it.name)
                        jobIdList!!.add(it.id)}
                    binding.spJobQuery.adapter = ArrayAdapter(this,R.layout.spinner_item, aList)
                    dialog.dismiss()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })



        when(binding.rgSearchAmong.visibility){
            View.VISIBLE -> destroyToolbar()
            View.GONE -> {
                destroyToolbar(false)
                binding.rgSearchAmong.visibility = View.VISIBLE
                when(binding.rgSearchAmong.checkedRadioButtonId){
                    R.id.rb_searchUser ->{
                        mActivityViewModel.getAllJobs()
                        mActivityViewModel.fetchSearchHistory(token!!, 0)
                        binding.layJobQuery.visibility=View.VISIBLE
                    }
                    R.id.rb_searchWorkspace->{
                        mActivityViewModel.fetchSearchHistory(token!!, 1)
                    }
                    R.id.rb_searchUpcoming ->{
                        mActivityViewModel.fetchSearchHistory(token!!, 2)
                    }
                }
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
                    R.id.rb_searchWorkspace ->{
                    }
                    R.id.rb_searchUpcoming ->{
                    } }
                return true
            } })

    }


    private fun onSeeNotificationsClicked() {
        when(binding.notificationRg.visibility){
            View.VISIBLE -> destroyToolbar()
            View.GONE ->{
                destroyToolbar()

                mActivityViewModel.getUserNotificationsResourceResponse.observe(this, Observer{ t->
                    when(t.javaClass){
                        Resource.Success::class.java ->{
                            binding.toolbarRecyclerview.adapter = NotificationElementsAdapter(t.data!!.notification_list as ArrayList<Notification>,this, this)
                            binding.toolbarRecyclerview.layoutManager = LinearLayoutManager(this)
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
                            val reqList = t.data!!.follow_requests
                            if(reqList.isEmpty()){
                                Toast.makeText(this, "There is nothing here", Toast.LENGTH_SHORT).show()
                            }

                            binding.toolbarRecyclerview.adapter = FollowRequestElementsAdapter(reqList as ArrayList<FollowRequest>,this, this)
                            binding.toolbarRecyclerview.layoutManager = LinearLayoutManager(this)
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
                            if (handledFollowRequestPosition != -1){
                                (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter)
                                        .removeElement(handledFollowRequestPosition)
                                handledFollowRequestPosition = -1
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

                when(binding.notificationRg.checkedRadioButtonId){
                    R.id.general_ntf_rb ->{
                        mActivityViewModel.getNotifications(token!!)
                    }
                    R.id.personal_ntf_rb ->{
                        mActivityViewModel.getFollowRequests(mActivityViewModel.getUserResourceResponse.value?.data!!.id, token!!)
                    }
                }
                binding.notificationRg.visibility = View.VISIBLE
            }
        }
    }

    private fun onLogOutButtonClicked(){
        val sharedPrefs = getSharedPreferences("token_file", 0)
        sharedPrefs.edit().remove("mail").apply()
        sharedPrefs.edit().remove("pass").apply()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
        Toast.makeText(this, "Logout made", Toast.LENGTH_LONG).show()
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

    override fun onSearchButtonClicked(buttonName: String) {
    }

    override fun onNotificationButtonClicked(ntf: Notification, position: Int) {
    }

    override fun onFollowRequestNameClicked(request: FollowRequest, position: Int) {
        navController.navigate(HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(request.id))
        destroyToolbar()
    }

    override fun onFollowRequestAcceptClicked(request: FollowRequest, position: Int) {
        if(mActivityViewModel.getUserResourceResponse.value!=null && token!=null){
            mActivityViewModel.acceptFollowRequest(request.id, mActivityViewModel.getUserResourceResponse.value?.data?.id!!, token!!)
        }
        handledFollowRequestPosition = position
    }

    override fun onFollowRequestRejectClicked(request: FollowRequest, position: Int) {
        if(mActivityViewModel.getUserResourceResponse.value!=null && token!=null){
            mActivityViewModel.deleteFollowRequest(request.id, mActivityViewModel.getUserResourceResponse.value?.data?.id!!, token!!)
        }
        handledFollowRequestPosition = position
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        search = (menu?.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setOnSearchClickListener{
            onSearchBarClicked()
        }
        return super.onCreateOptionsMenu(menu)
    }


}