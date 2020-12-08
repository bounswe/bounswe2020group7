package com.cmpe451.platon.page.activity

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
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
import com.cmpe451.platon.page.fragment.home.HomeFragmentDirections
import com.cmpe451.platon.page.fragment.otherprofile.OtherProfileViewModel
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions

class HomeActivity : BaseActivity(),
        SearchElementsAdapter.SearchButtonClickListener,
        FollowRequestElementsAdapter.FollowRequestButtonClickListener,
        NotificationElementsAdapter.NotificationButtonClickListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController

    private lateinit var toolbarRecyclerView: RecyclerView
    var token:String? = null
    lateinit var binding : ActivityHomeBinding

    lateinit var search:SearchView
    private lateinit var dialog: AlertDialog

    private val mProfilePageViewModel: ProfilePageViewModel by viewModels()



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        prepareFragmentSwitch()
        it.onNavDestinationSelected(navController)
    }

    private fun prepareFragmentSwitch() {
        binding.notificationRg.visibility = View.GONE
        binding.searchAmongRb.visibility = View.GONE
        (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)

        token = intent.extras?.get("token").toString()

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
        toolbarRecyclerView = binding.toolbarRecyclerview

        if (token != null){
            mProfilePageViewModel.fetchUser(token!!)
        }else{
            finish()
        }

        initListeners()

    }

    private fun initListeners() {
        dialog = Definitions().createProgressBar(this as BaseActivity)
        binding.notificationRg.setOnCheckedChangeListener { t, id->
            when(id){
                R.id.general_ntf_rb ->{
                    mProfilePageViewModel.getNotifications(token!!)
                }
                R.id.personal_ntf_rb ->{
                    mProfilePageViewModel.getFollowRequests(mProfilePageViewModel.getUserResourceResponse.value?.data!!.id, token!!)
                }
            }
        }

        mProfilePageViewModel.getUserNotificationsResourceResponse.observe(this, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    toolbarRecyclerView.adapter = NotificationElementsAdapter(t.data as ArrayList<Notification>,this, this)
                    toolbarRecyclerView.layoutManager = LinearLayoutManager(this)
                    dialog.dismiss()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })

        mProfilePageViewModel.getUserFollowRequestsResourceResponse.observe(this, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    toolbarRecyclerView.adapter = FollowRequestElementsAdapter(t.data!!.follow_requests as ArrayList<FollowRequest>,this, this)
                    toolbarRecyclerView.layoutManager = LinearLayoutManager(this)
                    dialog.dismiss()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })
        mProfilePageViewModel.acceptRequestResourceResponse.observe(this, {i->
            when(i.javaClass){
                Resource.Success::class.java -> {
                    mProfilePageViewModel.removeHandledRequest()
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

    private fun initSearch(search: SearchView) {

        val searchBar = search.findViewById<LinearLayout>(R.id.search_bar)
        searchBar.layoutTransition = LayoutTransition()

        toolbarRecyclerView.adapter = SearchElementsAdapter(arrayListOf(),this, this)
        toolbarRecyclerView.layoutManager = LinearLayoutManager(this)

        search.setOnSearchClickListener{
            toolbarRecyclerView.adapter = SearchElementsAdapter(arrayListOf(),this, this)
            toolbarRecyclerView.layoutManager = LinearLayoutManager(this)

            when(binding.searchAmongRb.visibility){
                View.VISIBLE -> {
                    binding.searchAmongRb.visibility = View.GONE
                    (toolbarRecyclerView.adapter as SearchElementsAdapter).clearElements()
                }
                View.GONE -> {
                    binding.searchAmongRb.visibility = View.VISIBLE
                    binding.notificationRg.visibility = View.GONE
                }
            }
        }

        search.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return Log.println(Log.ERROR, "SEARCH:", newText.toString().trim())*0 == 0
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                (toolbarRecyclerView.adapter as SearchElementsAdapter).addElement(0, query.toString())
                return true
            }
        })

            search.setOnCloseListener {
                binding.searchAmongRb.visibility = View.GONE
                (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
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
                binding.notificationRg.visibility = View.GONE
                (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
            }
            View.GONE ->{
                when(binding.notificationRg.checkedRadioButtonId){
                    R.id.general_ntf_rb ->{
                        mProfilePageViewModel.getNotifications(token!!)
                    }
                    R.id.personal_ntf_rb ->{
                        mProfilePageViewModel.getFollowRequests(mProfilePageViewModel.getUserResourceResponse.value?.data!!.id, token!!)
                    }
                }
                binding.notificationRg.visibility = View.VISIBLE
                search.isIconified = true
            }
        }
    }

    private fun onLogOutButtonClicked(){
        val sharedPrefs = getSharedPreferences("token_file", 0)
        sharedPrefs.edit().remove("mail").apply()
        sharedPrefs.edit().remove("pass").apply()
        sharedPrefs.edit().remove("token").apply()
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
        Toast.makeText(this, "Logout made", Toast.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onSearchButtonClicked(buttonName: String) {
    }



    override fun onNotificationButtonClicked(ntf: Notification, position: Int) {
    }

    override fun onFollowRequestNameClicked(request: FollowRequest, position: Int) {
        navController.navigate(HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(request.id))
        prepareFragmentSwitch()
    }

    override fun onFollowRequestAcceptClicked(request: FollowRequest, position: Int) {
        if(mProfilePageViewModel.getUserResourceResponse.value!=null && token!=null){
            mProfilePageViewModel.acceptFollowRequest(request.id, mProfilePageViewModel.getUserResourceResponse.value?.data?.id!!, token!!)
        }
        mProfilePageViewModel.setPositionOfHandledRequest(position)
    }

    override fun onFollowRequestRejectClicked(request: FollowRequest, position: Int) {
        if(mProfilePageViewModel.getUserResourceResponse.value!=null && token!=null){
            mProfilePageViewModel.deleteFollowRequest(request.id, mProfilePageViewModel.getUserResourceResponse.value?.data?.id!!, token!!)
        }
        mProfilePageViewModel.setPositionOfHandledRequest(position)
    }


}