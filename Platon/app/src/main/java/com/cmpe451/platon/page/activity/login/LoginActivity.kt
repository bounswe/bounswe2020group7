package com.cmpe451.platon.page.activity.login

/**
 * @author Burak Ömür
 */

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
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
import com.cmpe451.platon.network.Resource
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
        // init layout manager of toolbar recycler view
        binding.toolbarRecyclerview.layoutManager = LinearLayoutManager(this)
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
        when(binding.rgSearchAmong.visibility){
            View.VISIBLE ->{
                destroyToolbar()
                mActivityViewModel.getSearchUserResourceResponse.removeObservers(this)
                mActivityViewModel.getJobListResourceResponse.removeObservers(this)
            }
            View.GONE -> {
                destroyToolbar(false)
                binding.rgSearchAmong.visibility = View.VISIBLE

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

                        mActivityViewModel.searchUser(null, query, jobQuery,null, null)
                    }
                    R.id.rb_searchWorkspace ->{ }
                    R.id.rb_searchUpcoming ->{ } }
                return true
            } })
    }

    private fun destroyToolbar(flag:Boolean=true) {
        if(flag){
            search.onActionViewCollapsed()
        }
        binding.layJobQuery.visibility=View.GONE
        binding.rgSearchAmong.clearCheck()
        binding.rgSearchAmong.visibility = View.GONE
        if (binding.toolbarRecyclerview.adapter != null){
            (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
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
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        destroyToolbar()
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }


    override fun onSearchButtonClicked(buttonName: String) {
        TODO("Not yet implemented")
    }
}