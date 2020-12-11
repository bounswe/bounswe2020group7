package com.cmpe451.platon.page.activity.login

/**
 * @author Burak Ömür
 */

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.adapter.ToolbarElementsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.ActivityLoginBinding

/**
 * Main starter activity of the application.
 * It controls the initial flow of the application.
 */
class LoginActivity :BaseActivity(), SearchElementsAdapter.SearchButtonClickListener {

    // custom toolbar/action bar
    lateinit var toolbar: Toolbar
    // navigation controller for navigating between fragments
    lateinit var navController: NavController
    // recyclerView for searching
    private lateinit var toolbarRecyclerView: RecyclerView
    // view binding of the activity layout
    lateinit var binding : ActivityLoginBinding

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
    }

    /**
     * It setups the search mechanism
     * @param view View of the search element
     */
    private fun setupSearch(view:SearchView){
        toolbarRecyclerView = binding.toolbarRecyclerview
        toolbarRecyclerView.adapter = SearchElementsAdapter(ArrayList(),this, this)
        toolbarRecyclerView.layoutManager = LinearLayoutManager(this)

        // obtain search bar
        val searchBar = view.findViewById<LinearLayout>(R.id.search_bar)
        // add smooth transition animation to layout
        searchBar.layoutTransition = LayoutTransition()

        // set click listener
        view.setOnSearchClickListener{
            // make search options visible
            binding.searchAmongRb.visibility = View.VISIBLE

        }

        // set query listeners
        view.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return Log.println(Log.ERROR, "SEARCH:", newText.toString().trim())*0 == 0
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                (toolbarRecyclerView.adapter as SearchElementsAdapter).addElement(0, query.toString())
                return Log.println(Log.ERROR, "SEARCH:",(query + toolbarRecyclerView.adapter?.itemCount.toString()).trim())*0 == 0
            }
        })

        // set close listener
        view.setOnCloseListener {
            destroyToolbar()
            view.onActionViewCollapsed()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // inflate custom actionbar/toolbar menu
        menuInflater.inflate(R.menu.actionbar, menu)
        // obtain search view element in the menu
        // setup the search mechanism
        setupSearch(menu?.findItem(R.id.search_btn)?.actionView as SearchView)
        return super.onCreateOptionsMenu(menu)
    }

    private fun destroyToolbar() {
        binding.searchAmongRb.visibility = View.GONE
        (toolbarRecyclerView.adapter as ToolbarElementsAdapter).clearElements()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // according to selected item make navigation
        destroyToolbar()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        // when back button pressed
        destroyToolbar()
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onSearchButtonClicked(buttonName: String) {
        TODO("Not yet implemented")
    }
}