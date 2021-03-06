package com.cmpe451.platon.page.activity.workspace

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.ActivityWorkspaceBinding
import com.cmpe451.platon.network.models.SearchElement
import com.cmpe451.platon.page.activity.home.fragment.workspace.WorkspaceListFragmentDirections
import com.cmpe451.platon.page.activity.workspace.fragment.workspace.WorkspaceFragmentDirections
import com.cmpe451.platon.util.Definitions

class WorkspaceActivity : BaseActivity(), SearchElementsAdapter.SearchButtonClickListener {

    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    lateinit var binding : ActivityWorkspaceBinding
    private lateinit var dialog: AlertDialog
    private val mActivityViewModel: WorkspaceActivityViewModel by viewModels()


    var token:String? = null
    var user_id:Int? = null
    var workspace_id:Int? = null
    var addClicked:Boolean? = null
    var isOwner:Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Platon)
        super.onCreate(savedInstanceState)

        token = intent.extras?.getString("token")
        user_id = intent.extras?.getInt("user_id")
        workspace_id = intent.extras?.getInt("workspace_id")
        addClicked = intent.extras?.getBoolean("add")
        isOwner = intent.extras?.getBoolean("isOwner")

        if (token == null || user_id == null || workspace_id == null){
            finish()
        }
        binding = ActivityWorkspaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.workspace_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)
        initViews()
    }

    private fun initViews() {
        if(addClicked == true){
            navController.navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToAddWorkspaceFragment())
        }else{
            initListeners()
        }
    }

    private fun initListeners() {
        dialog = Definitions().createProgressBar(this as BaseActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_workspace, menu)

        /*
        search = (menu?.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setOnSearchClickListener{
            onSearchBarClicked()
        }
        */
        return super.onCreateOptionsMenu(menu)
    }

    private fun onIssueClicked() {
        //Toast.makeText(applicationContext,"Issue clicked",Toast.LENGTH_SHORT).show()
        navController.navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToIssuesFragment())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.issue_btn -> onIssueClicked()
        }

        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        when(navController.currentDestination?.id){
            R.id.addWorkspaceFragment -> finish()
            R.id.workspaceFragment -> finish()
            else-> navController.navigateUp()
        }


        return super.onSupportNavigateUp()
    }

    override fun onSearchButtonClicked(element: SearchElement, position: Int) {
        val collabIds = element.contributor_list!!.map { it.id }
        val bnd = Bundle()
        bnd.putString("token", token!!)
        bnd.putInt("user_id", user_id!!)
        bnd.putBoolean("add", false)
        bnd.putInt("workspace_id", element.id)
        bnd.putBoolean("isOwner", true)
        if (collabIds.contains(user_id!!)) {
            bnd.putBoolean("isOwner", true)
        } else {
            bnd.putBoolean("isOwner", false)
        }
        startActivity(Intent(this, WorkspaceActivity::class.java).putExtras(bnd))
    }
}