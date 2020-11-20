package com.cmpe451.platon.page.activity

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.core.BaseActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.`interface`.ActivityChangeListener
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.databinding.ActivityLoginBinding

class LoginActivity :BaseActivity(), FragmentChangeListener, SearchElementsAdapter.SearchButtonClickListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    private lateinit var searchRecyclerView: RecyclerView
    val myDataset = ArrayList<String>()

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.login_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)

        initViews()
    }

    private fun initViews() {
        searchRecyclerView = binding.searchElementRecyclerView
        searchRecyclerView.adapter = SearchElementsAdapter(myDataset,this, this)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        (searchRecyclerView.adapter as SearchElementsAdapter).notifyDataSetChanged()
    }

    override fun addFragment(fragment: Fragment, bundle: Bundle?) {
        TODO("Not yet implemented")
    }


    override fun destroyCurrentFragment() {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)

        val search = (menu?.findItem(R.id.search_btn)?.actionView as SearchView)
        val searchBar = search.findViewById<LinearLayout>(R.id.search_bar)
        searchBar.layoutTransition = LayoutTransition()

        search.setOnSearchClickListener{
            binding.searchAmongRb.visibility = View.VISIBLE
        }

        search.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return Log.println(Log.ERROR, "SEARCH:", newText.toString().trim())*0 == 0
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                myDataset.add(0, query.toString())
                searchRecyclerView.adapter?.notifyItemInserted(0)
                return Log.println(Log.ERROR, "SEARCH:",(query + searchRecyclerView.adapter?.itemCount.toString()+ myDataset.toString()).trim())*0 == 0
            }
        })

        search.setOnCloseListener {
            binding.searchAmongRb.visibility = View.GONE
            myDataset.clear()
            searchRecyclerView.adapter?.notifyDataSetChanged()
            search.onActionViewCollapsed()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onSearchButtonClicked(buttonName: String) {
        TODO("Not yet implemented")
    }
}