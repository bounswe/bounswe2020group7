package com.cmpe451.platon.page.activity

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.fragment.login.view.LoginFragment
import com.cmpe451.platon.page.fragment.register.view.RegisterFragment
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionManager
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragmentDirections

class LoginActivity :BaseActivity(), FragmentChangeListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toolbar = findViewById(R.id.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)

    }


    override fun addFragment(fragment: Fragment, bundle: Bundle?) {
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().addToBackStack(fragment.javaClass.name)
                .add(R.id.login_activity_container, fragment).commit()
    }


    override fun destroyCurrentFragment() {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        // todo
        if (item.itemId == R.id.search_btn){
            val search = findViewById<SearchView>(item.itemId)
            search.setOnQueryTextListener( object:
                    SearchView.OnQueryTextListener{
                override fun onQueryTextChange(newText: String?): Boolean {
                    return Log.println(Log.ERROR, "SEARCH:", newText.toString().trim())*0 == 0
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return Log.println(Log.ERROR, "SEARCH:", query.toString().trim())*0 == 0
                }
            }
            )
            return super.onOptionsItemSelected(item)
        }

        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }


}