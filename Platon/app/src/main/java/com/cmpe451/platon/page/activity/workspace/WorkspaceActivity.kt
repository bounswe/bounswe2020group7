package com.cmpe451.platon.page.activity.workspace

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.ActivityWorkspaceBinding
import com.cmpe451.platon.page.activity.workspace.fragment.workspace.WorkspaceFragmentDirections
import com.cmpe451.platon.util.Definitions

class WorkspaceActivity : BaseActivity() {

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
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
}