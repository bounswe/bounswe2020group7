package com.cmpe451.platon.page.activity.login

/**
 * @author Burak Ömür
 */

import android.app.AlertDialog
import android.database.MatrixCursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.adapter.ToolbarElementsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.ActivityLoginBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.SearchElement
import com.cmpe451.platon.network.models.SearchHistoryElement
import com.cmpe451.platon.page.activity.home.HomeActivityViewModel
import com.cmpe451.platon.util.Definitions

/**
 * Main starter activity of the application.
 * It controls the initial flow of the application.
 */
class LoginActivity :BaseActivity(), SearchElementsAdapter.SearchButtonClickListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    lateinit var binding : ActivityLoginBinding
    lateinit var search:SearchView
    private lateinit var dialog: AlertDialog
    private val mActivityViewModel: HomeActivityViewModel by viewModels()
    private val searchPageSize = 10;
    private var maxPageNumberSearch = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        // default theme is splash theme to show a simple splash screen
        // we change it to app's original theme
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)
        // initialize the binding, and inflate the view
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // initialize toolbar
        toolbar = findViewById(R.id.toolbar)
        // initialize navigation controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.login_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        //set action bar to custom toolbar
        setSupportActionBar(toolbar)
        // set navigation controller to control toolbar ui.
        NavigationUI.setupActionBarWithNavController(this, navController)
        // initialize general views
        initViews()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(this)
        // init layout manager of toolbar recycler view
        binding.toolbarRecyclerview.layoutManager = layoutManager

        initListeners()

    }


    private var jobIdList:ArrayList<Int>?  = null

    private fun initListeners() {
        dialog = Definitions().createProgressBar(this as BaseActivity)

        binding.rgSearchAmong.setOnCheckedChangeListener { _, id ->
            when(id){
                R.id.rb_searchUser -> {
                    binding.layJobQuery.visibility = View.VISIBLE
                    mActivityViewModel.getAllJobs()
                }
                R.id.rb_searchWorkspace ->{
                    binding.layJobQuery.visibility = View.GONE
                }
                R.id.rb_searchUpcoming ->{
                    binding.layJobQuery.visibility = View.GONE
                }
            }}
    }

    private fun onSearchBarClicked() {
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


                val layoutManager = LinearLayoutManager(this)
                // init layout manager of toolbar recycler view
                binding.toolbarRecyclerview.layoutManager = layoutManager

                binding.toolbarRecyclerview.addOnScrollListener(object: PaginationListener(layoutManager, searchPageSize){
                    override fun loadMoreItems() {
                        if(maxPageNumberSearch-1 > currentPage){
                            currentPage++
                            var jobQuery: Int? = null
                            val pos = binding.spJobQuery.selectedItemPosition
                            if (pos != 0) {
                                jobQuery = jobIdList?.get(pos)
                            }
                            mActivityViewModel.searchUser(null, search.query.toString().trim(), jobQuery, currentPage, PAGE_SIZE)
                        }
                    }
                    override var isLastPage: Boolean = false
                    override var isLoading: Boolean = false
                    override var currentPage: Int = 0

                })


                //listener for search radio group
                binding.rgSearchAmong.setOnCheckedChangeListener { _, id ->
                    when(id){
                        R.id.rb_searchUser -> {
                            binding.layJobQuery.visibility = View.VISIBLE
                            mActivityViewModel.getAllJobs()
                        }
                        R.id.rb_searchWorkspace ->binding.layJobQuery.visibility = View.GONE

                        R.id.rb_searchUpcoming ->binding.layJobQuery.visibility = View.GONE
                    }}

                // make radio group visible
                binding.rgSearchAmong.visibility = View.VISIBLE
                binding.toolbarRecyclerview.visibility = View.VISIBLE
                // listener for search user results
                mActivityViewModel.getSearchUserResourceResponse.observe(this, { t ->
                    when (t.javaClass) {
                        Resource.Loading::class.java -> dialog.show()
                        Resource.Success::class.java -> {
                            maxPageNumberSearch = t.data!!.number_of_pages
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
                        mActivityViewModel.searchUser(null, query, jobQuery,0, searchPageSize)
                    }
                    R.id.rb_searchWorkspace ->{}
                    R.id.rb_searchUpcoming -> {}}
                return true
            } })
    }
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
        binding.rgSearchAmong.visibility = View.GONE
        binding.toolbarRecyclerview.visibility = View.GONE

        binding.rgSearchAmong.setOnCheckedChangeListener(null)

        binding.rgSearchAmong.clearCheck()
        // if adapter not null, clear it
        if (binding.toolbarRecyclerview.adapter != null){
            (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
        }
        binding.toolbarRecyclerview.clearOnScrollListeners()
        //make GONE the views
        mActivityViewModel.getSearchUserResourceResponse.removeObservers(this)
        mActivityViewModel.getJobListResourceResponse.removeObservers(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_login, menu)
        search = (menu?.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setOnSearchClickListener{
            onSearchBarClicked()
        }

        //listener for close button click
        search.setOnCloseListener {
            //destroy toolbar
            destroyToolbar()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        destroyToolbar()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        when(navController.currentDestination?.id){
            R.id.landingFragment ->{
                val exitDialog = AlertDialog.Builder(this)
                        .setMessage("Do you want to exit?")
                        .setPositiveButton("EXIT") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("No", null)
                        .create().show()
            }
            else ->{
                navController.navigateUp()
            }
        }

        return super.onSupportNavigateUp()
    }

    override fun onSearchButtonClicked(element: SearchElement, position: Int) {
        //TODO("Not yet implemented")
    }

}