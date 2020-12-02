package com.cmpe451.platon.page.activity

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cmpe451.platon.databinding.ActivityHomeBinding
import com.cmpe451.platon.page.fragment.login.LoginViewModel
import com.cmpe451.platon.page.fragment.profilepage.presenter.ProfilePageViewModel

class HomeActivity : BaseActivity(), SearchElementsAdapter.SearchButtonClickListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    val myDataset = ArrayList<String>()
    private lateinit var searchRecyclerView: RecyclerView
    var token:String? = null
    lateinit var binding : ActivityHomeBinding

    private val mProfilePageViewModel: ProfilePageViewModel by viewModels()


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        item -> item.onNavDestinationSelected(navController)
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
        searchRecyclerView = binding.searchElementRecyclerView
        searchRecyclerView.adapter = SearchElementsAdapter(myDataset,this, this)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        (searchRecyclerView.adapter as SearchElementsAdapter).notifyDataSetChanged()


        if (token != null){
            mProfilePageViewModel.fetchUser(token!!)
        }else{
            finish()
        }

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

        if (item.itemId == R.id.logout_menu_btn){
            onLogOutButtonClicked()
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun onLogOutButtonClicked(){
        val sharedPrefs = getSharedPreferences("token_file", 0)
        sharedPrefs.edit().remove("login_mail").apply()
        sharedPrefs.edit().remove("login_pass").apply()
        sharedPrefs.edit().putBoolean("remember_me", false).apply()
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
        TODO("Not yet implemented")
    }


}