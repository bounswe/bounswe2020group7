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
import com.cmpe451.platon.network.models.Notification
import com.cmpe451.platon.network.models.SearchHistoryElement
import com.cmpe451.platon.page.activity.login.LoginActivity
import com.cmpe451.platon.page.activity.home.fragment.home.HomeFragmentDirections
import com.cmpe451.platon.util.Definitions

class HomeActivity : BaseActivity(),
        SearchElementsAdapter.SearchButtonClickListener,
        FollowRequestElementsAdapter.FollowRequestButtonClickListener,
        NotificationElementsAdapter.NotificationButtonClickListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    private lateinit var toolbarRecyclerView: RecyclerView
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

    private fun destroyToolbar() {
        binding.notificationRg.visibility = View.GONE
        binding.rgSearchAmong.visibility = View.GONE
        (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
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
        toolbarRecyclerView = binding.toolbarRecyclerview
        initListeners()
    }

    private fun initListeners() {
        dialog = Definitions().createProgressBar(this as BaseActivity)
        binding.notificationRg.setOnCheckedChangeListener { t, id->
            when(id){
                R.id.general_ntf_rb ->{
                    mActivityViewModel.getNotifications(token!!)
                }
                R.id.personal_ntf_rb ->{
                    mActivityViewModel.getFollowRequests(mActivityViewModel.getUserResourceResponse.value?.data!!.id, token!!)
                }
            }
        }

        mActivityViewModel.getUserNotificationsResourceResponse.observe(this, Observer{ t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    toolbarRecyclerView.adapter = NotificationElementsAdapter(t.data!!.notification_list as ArrayList<Notification>,this, this)
                    toolbarRecyclerView.layoutManager = LinearLayoutManager(this)
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
                    toolbarRecyclerView.adapter = FollowRequestElementsAdapter(t.data!!.follow_requests as ArrayList<FollowRequest>,this, this)
                    toolbarRecyclerView.layoutManager = LinearLayoutManager(this)
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
                        (toolbarRecyclerView.adapter as ToolbarElementsAdapter)
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        search = (menu?.findItem(R.id.search_btn)?.actionView as SearchView)

        initSearch(search)
        return super.onCreateOptionsMenu(menu)
    }


    var historyCursor = MatrixCursor(arrayOf("_id", "query"))

    private fun initSearch(search: SearchView) {

        val searchBar = search.findViewById<LinearLayout>(R.id.search_bar)
        searchBar.layoutTransition = LayoutTransition()

        toolbarRecyclerView.adapter = SearchElementsAdapter(arrayListOf(),this, this)
        toolbarRecyclerView.layoutManager = LinearLayoutManager(this)

        search.setOnSearchClickListener{
            toolbarRecyclerView.adapter = SearchElementsAdapter(arrayListOf(),this, this)
            toolbarRecyclerView.layoutManager = LinearLayoutManager(this)

            when(binding.rgSearchAmong.visibility){
                View.VISIBLE -> {
                    binding.rgSearchAmong.visibility = View.GONE
                    (toolbarRecyclerView.adapter as SearchElementsAdapter).clearElements()
                }
                View.GONE -> {
                    binding.rgSearchAmong.visibility = View.VISIBLE
                    binding.notificationRg.visibility = View.GONE
                }
            }
        }

        search.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 1
        val cursorAdapter = SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                historyCursor,
                arrayOf("query"),
                arrayOf(android.R.id.text1).toIntArray(),
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        search.suggestionsAdapter = cursorAdapter
        search.setOnSuggestionListener(object: SearchView.OnSuggestionListener{
            override fun onSuggestionSelect(position: Int): Boolean {
                Log.i("ERRROR", cursorAdapter.getItem(position).toString())
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                Log.i("ERRROR", cursorAdapter.getItem(position).toString())
                return true
            }

        })


        binding.rgSearchAmong.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.rb_searchUser -> {
                    (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
                    mActivityViewModel.fetchSearchHistory(token!!, 0)
                }
                R.id.rb_searchWorkspace ->{
                    (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
                    mActivityViewModel.fetchSearchHistory(token!!, 1)
                }
                R.id.rb_searchUpcoming ->{
                    (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
                    mActivityViewModel.fetchSearchHistory(token!!, 2)
                }
            }}

        mActivityViewModel.getSearchHistoryResourceResponse.observe(this, { t->
            when(t.javaClass){
                Resource.Loading::class.java -> {
                    dialog.show()
                }
                Resource.Success::class.java ->{
                    t.data!!.search_history.forEachIndexed{ i, e ->
                        historyCursor.addRow(arrayOf(i, e.query))
                    }
                    dialog.dismiss()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })



        search.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return Log.println(Log.ERROR, "SEARCH:", newText.toString().trim())*0 == 0
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                when(binding.rgSearchAmong.checkedRadioButtonId){
                    R.id.rb_searchUser ->{
                        mActivityViewModel.searchUser(token!!, query, null,null, null)
                    }
                    R.id.rb_searchWorkspace ->{

                    }
                    R.id.rb_searchUpcoming ->{

                    }
                }
                return true
            }
        })

        mActivityViewModel.getSearchUserResourceResponse.observe(this, {t->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    (toolbarRecyclerView.adapter as SearchElementsAdapter).clearElements()
                    (toolbarRecyclerView.adapter as SearchElementsAdapter).submitElements(t.data!!.result_list.map { it.name + " " + it.surname+ " " + it.is_private.toString()})
                    dialog.dismiss()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })


            search.setOnCloseListener {
                destroyToolbar()
                search.onActionViewCollapsed()
                true
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.logout_menu_btn -> onLogOutButtonClicked()
            R.id.notification_btn -> onSeeNotificationsClicked()
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun onSeeNotificationsClicked() {
        when(binding.notificationRg.visibility){
            View.VISIBLE ->{
                destroyToolbar()
            }
            View.GONE ->{
                search.isIconified = true
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
        destroyToolbar()
        val sharedPrefs = getSharedPreferences("token_file", 0)
        sharedPrefs.edit().remove("mail").apply()
        sharedPrefs.edit().remove("pass").apply()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
        Toast.makeText(this, "Logout made", Toast.LENGTH_LONG).show()
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


}