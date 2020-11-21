package com.cmpe451.platon.page.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.fragment.home.view.HomeFragmentDirections
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity(), FragmentChangeListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    lateinit var bottomNavBar : BottomNavigationView

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.bottom_nav_bar_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_nav_bar_workspaces -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_nav_bar_profile_page -> {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToProfilePageFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        bottomNavBar = findViewById(R.id.bottom_nav_bar)

        toolbar = findViewById(R.id.toolbar)
//


//
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(bottomNavBar, navController)
        bottomNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun addFragment(fragment: Fragment, bundle: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun destroyCurrentFragment() {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val search = menu?.findItem(R.id.search_btn)?.actionView as SearchView
        search.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return Log.println(Log.ERROR, "SEARCH:", newText.toString().trim())*0 == 0
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return Log.println(Log.ERROR, "SEARCH:", query.toString().trim())*0 == 0
            }
        }
        )
        return super.onPrepareOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }



}