package com.cmpe451.platon.page.activity.login

/**
 * @author Burak Ömür
 */

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.database.MatrixCursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
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
import java.util.*
import kotlin.collections.ArrayList

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

    private var maxPageNumberToolbarElements=0
    private val toolbarPageSize = 10

    private var jobIdList:ArrayList<Int>  = arrayListOf(-1)
    private var searchHistory:List<SearchHistoryElement> = emptyList()

    private lateinit var toolbarLayoutManager:LinearLayoutManager
    private lateinit var paginationListener:PaginationListener

    /**
     * Clears the toolbar/action bar's state.
     * Notification/search and others
     */
    private fun destroyToolbar(flag: Boolean = true) {
        if(flag){
            // collapse the search action view
            search.onActionViewCollapsed()
        }

        //make GONE the views
        binding.layWsAndUeFilters.visibility = View.GONE
        binding.laySearchUser.visibility=View.GONE
        binding.toolbarRecyclerview.visibility = View.GONE
        binding.rgSearchAmong.visibility = View.GONE

        binding.rgSearchAmong.setOnCheckedChangeListener(null)

        binding.rgSearchAmong.clearCheck()
        // if adapter not null, clear it
        if (binding.toolbarRecyclerview.adapter != null){
            (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
        }

        paginationListener.currentPage = 0
        maxPageNumberToolbarElements = 0
    }

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
        toolbarLayoutManager = LinearLayoutManager(this)
        binding.toolbarRecyclerview.layoutManager = toolbarLayoutManager

        initListeners()
        setObservers()
    }


    private fun setObservers(){
        setObserversForSearch()
    }


    private fun setObserversForSearch() {
        //observer for search history
        mActivityViewModel.getSearchHistoryResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    searchHistory = t.data!!.search_history
                    val historyCursor = MatrixCursor(arrayOf("_id", "query"))
                    searchHistory.forEachIndexed { i, e ->
                        historyCursor.addRow(arrayOf(i, e.query))
                    }
                    // set adapter's cursor with retrieved suggestion items
                    search.suggestionsAdapter.changeCursor(historyCursor)
                    mActivityViewModel.getSearchHistoryResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchHistoryResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })

        // listener for search user results
        mActivityViewModel.getSearchUserResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    // set max number of pages for pagination
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    // already exists, append on it, else create new
                    if (binding.toolbarRecyclerview.adapter?.javaClass == SearchElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as SearchElementsAdapter).submitElements(
                            t.data!!.result_list
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = SearchElementsAdapter(
                            t.data!!.result_list as java.util.ArrayList<SearchElement>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })

        mActivityViewModel.getSearchWorkspaceResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    // set max number of pages for pagination
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    // already exists, append on it, else create new
                    if (binding.toolbarRecyclerview.adapter?.javaClass == SearchElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as SearchElementsAdapter).submitElements(
                            t.data!!.result_list
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = SearchElementsAdapter(
                            t.data!!.result_list as java.util.ArrayList<SearchElement>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchUserResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })



        mActivityViewModel.getSearchUpcomingEventResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    // set max number of pages for pagination
                    maxPageNumberToolbarElements = t.data!!.number_of_pages
                    // already exists, append on it, else create new
                    if (binding.toolbarRecyclerview.adapter?.javaClass == SearchElementsAdapter::class.java) {
                        (binding.toolbarRecyclerview.adapter as SearchElementsAdapter).submitElements(
                            t.data!!.result_list
                        )
                    } else {
                        binding.toolbarRecyclerview.adapter = SearchElementsAdapter(
                            t.data!!.result_list as java.util.ArrayList<SearchElement>,
                            this,
                            this
                        )
                    }
                    paginationListener.isLoading = false
                    mActivityViewModel.getSearchUpcomingEventResourceResponse.value =
                        Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getSearchUpcomingEventResourceResponse.value =
                        Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
            }
        })


        // listener for all job list
        mActivityViewModel.getJobListResourceResponse.observe(this, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val aList = arrayListOf("Any")
                    t.data!!.forEach {
                        aList.add(it.name)
                        // fill jobList Id array defined above
                        jobIdList.add(it.id)
                    }
                    binding.spJobQuery.adapter = ArrayAdapter(this, R.layout.spinner_item, aList)
                }
                Resource.Error::class.java -> {
                    Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
                    mActivityViewModel.getJobListResourceResponse.value = Resource.Done()
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {

        // pagination listener define
        paginationListener = object: PaginationListener(toolbarLayoutManager, toolbarPageSize){
            override fun loadMoreItems() {
                if(maxPageNumberToolbarElements-1 > currentPage){
                    isLoading = true
                    currentPage++
                    when (binding.rgSearchAmong.checkedRadioButtonId) {
                        R.id.rb_searchUser -> {
                            val sortBy = binding.spSortByUser.selectedItemPosition
                            mActivityViewModel.searchUser(
                                null,
                                search.query.toString().trim(),
                                if (jobIdList[binding.spJobQuery.selectedItemPosition] == -1) null else jobIdList[binding.spJobQuery.selectedItemPosition],
                                if (sortBy != 0) sortBy - 1 else null, currentPage,
                                PAGE_SIZE
                            )
                        }
                        R.id.rb_searchUpcoming -> {
                            val sortBy = binding.spSortByUe.selectedItemPosition
                            val startDateE = binding.etStartDateE.text.toString().trim()
                            val startDateS = binding.etDeadlineS.text.toString().trim()
                            val deadlineS = binding.etStartDateS.text.toString().trim()
                            val deadlineE = binding.etDeadlineE.text.toString().trim()

                            mActivityViewModel.searchUpcomingEvent(
                                null,
                                search.query.toString().trim(),
                                if (startDateS.isNotEmpty()) startDateS else null,
                                if (startDateE.isNotEmpty()) startDateE else null,
                                if (deadlineS.isNotEmpty()) deadlineS else null,
                                if (deadlineE.isNotEmpty()) deadlineE else null,
                                if (sortBy != 0) sortBy - 1 else null,
                                currentPage,
                                toolbarPageSize
                            )

                        }
                        R.id.rb_searchWorkspace -> {
                            val sortBy = binding.spSortByWs.selectedItemPosition
                            val name = binding.etFilterName.text.toString().trim()
                            val surname = binding.etFilterSurname.text.toString().trim()
                            val startDateE = binding.etStartDateE.text.toString().trim()
                            val startDateS = binding.etDeadlineS.text.toString().trim()
                            val deadlineS = binding.etStartDateS.text.toString().trim()
                            val deadlineE = binding.etDeadlineE.text.toString().trim()
                            val event = binding.etFilterEvent.text.toString().trim()

                            mActivityViewModel.searchWorkspace(
                                null,
                                search.query.toString().trim(),
                                binding.etFilterSkill.text.toString().trim(),
                                if (name.isNotEmpty()) name else null,
                                if (surname.isNotEmpty()) surname else null,
                                if (startDateS.isNotEmpty()) startDateS else null,
                                if (startDateE.isNotEmpty()) startDateE else null,
                                if (deadlineS.isNotEmpty()) deadlineS else null,
                                if (deadlineE.isNotEmpty()) deadlineE else null,
                                if (sortBy != 0) sortBy - 1 else null,
                                if (event.isNotEmpty()) event else null,
                                currentPage,
                                toolbarPageSize
                            )
                                }
                            }
                }
            }
            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        binding.toolbarRecyclerview.addOnScrollListener(paginationListener)


        binding.spSortByUser.adapter = ArrayAdapter(
            this, R.layout.spinner_item, arrayOf(
                "Semantic Rating",
                "Alphabetical Order(A=>Z)",
                "Alphabetical Order (Z=>A)"
            )
        )
        binding.spSortByWs.adapter = ArrayAdapter(
            this, R.layout.spinner_item,
            arrayOf(
                "Semantic Rating",
                "Ascending Date",
                "Descending Date",
                "Ascending Number of Collaborators Needed",
                "Descending Number of Collaborators Needed",
                "Ascending Alphabetical Order",
                "Descending Alphabetical Order"
            )
        )
        binding.spSortByUe.adapter = ArrayAdapter(
            this, R.layout.spinner_item,
            arrayOf("Semantic Rating", "Alphabetical Order(A=>Z)", "Date Order")
        )

        binding.etStartDateS.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etStartDateS.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etStartDateS.setText("")
                }
                datePickerDialog.show()
            }
            true
        }

        binding.etStartDateE.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etStartDateE.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etStartDateE.setText("")
                }
                datePickerDialog.show()
            }
            true
        }

        binding.etDeadlineE.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etDeadlineE.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etDeadlineE.setText("")
                }
                datePickerDialog.show()
            }
            true
        }

        binding.etDeadlineS.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months + 1)
                        val dayString = String.format("%02d", day)
                        binding.etDeadlineS.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth
                )
                datePickerDialog.setOnCancelListener{
                    binding.etDeadlineS.setText("")
                }
                datePickerDialog.show()
            }
            true
        }


        dialog = Definitions().createProgressBar(this as BaseActivity)
    }

    /**
     * Triggered when search button clicked
     */
    private fun onSearchBarClicked() {
        // when clicked on search button
        when(binding.rgSearchAmong.visibility){
            // if visible destroy the toolbar, and remove observers
            View.VISIBLE -> {
                destroyToolbar()
            }
            // if gone, create view
            View.GONE -> {
                // destroy toolbar, but do not collapse search view
                destroyToolbar(false)

                // make radio group visible
                binding.rgSearchAmong.visibility = View.VISIBLE
                binding.toolbarRecyclerview.visibility = View.VISIBLE

                //listener for search radio group
                binding.rgSearchAmong.setOnCheckedChangeListener { _, id ->
                    paginationListener.currentPage = 0
                    if (binding.toolbarRecyclerview.adapter != null) {
                        (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
                    }
                    when (id) {
                        R.id.rb_searchUser -> {
                            binding.layWsAndUeFilters.visibility = View.GONE
                            binding.laySearchUser.visibility = View.VISIBLE
                            mActivityViewModel.getAllJobs()
                        }
                        R.id.rb_searchWorkspace -> {
                            binding.spSortByUe.visibility = View.GONE
                            binding.spSortByWs.visibility = View.VISIBLE
                            binding.layWsAndUeFilters.visibility = View.VISIBLE
                            binding.layWorkspaceFilter.visibility = View.VISIBLE
                            binding.laySearchUser.visibility = View.GONE
                        }
                        R.id.rb_searchUpcoming -> {
                            binding.spSortByUe.visibility = View.VISIBLE
                            binding.spSortByWs.visibility = View.GONE

                            binding.layWsAndUeFilters.visibility = View.VISIBLE
                            binding.layWorkspaceFilter.visibility = View.GONE
                            binding.laySearchUser.visibility = View.GONE
                        }
                    }
                }
            }
        }

        search.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                search.setQuery(searchHistory[position].query, false)
                return true
            }

        })

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                search.clearFocus()
                paginationListener.currentPage = 0
                if (binding.toolbarRecyclerview.adapter != null) {
                    (binding.toolbarRecyclerview.adapter as ToolbarElementsAdapter).clearElements()
                }

                when (binding.rgSearchAmong.checkedRadioButtonId) {
                    R.id.rb_searchUser -> {
                        val sortBy = binding.spSortByUser.selectedItemPosition
                        mActivityViewModel.searchUser(
                            null, query,
                            if (jobIdList[binding.spJobQuery.selectedItemPosition] == -1) null else jobIdList[binding.spJobQuery.selectedItemPosition],
                            if (sortBy != 0) sortBy - 1 else null, 0, toolbarPageSize
                        )
                    }
                    R.id.rb_searchWorkspace -> {
                        val sortBy = binding.spSortByWs.selectedItemPosition
                        val name = binding.etFilterName.text.toString().trim()
                        val surname = binding.etFilterSurname.text.toString().trim()
                        val startDateE = binding.etStartDateE.text.toString().trim()
                        val startDateS = binding.etDeadlineS.text.toString().trim()
                        val deadlineS = binding.etStartDateS.text.toString().trim()
                        val deadlineE = binding.etDeadlineE.text.toString().trim()
                        val event = binding.etFilterEvent.text.toString().trim()

                        mActivityViewModel.searchWorkspace(
                            null,
                            search.query.toString().trim(),
                            binding.etFilterSkill.text.toString().trim(),
                            if (name.isNotEmpty()) name else null,
                            if (surname.isNotEmpty()) surname else null,
                            if (startDateS.isNotEmpty()) startDateS else null,
                            if (startDateE.isNotEmpty()) startDateE else null,
                            if (deadlineS.isNotEmpty()) deadlineS else null,
                            if (deadlineE.isNotEmpty()) deadlineE else null,
                            if (sortBy != 0) sortBy - 1 else null,
                            if (event.isNotEmpty()) event else null,
                            0,
                            toolbarPageSize
                        )
                    }
                    R.id.rb_searchUpcoming -> {
                        val sortBy = binding.spSortByUe.selectedItemPosition
                        val startDateE = binding.etStartDateE.text.toString().trim()
                        val startDateS = binding.etDeadlineS.text.toString().trim()
                        val deadlineS = binding.etStartDateS.text.toString().trim()
                        val deadlineE = binding.etDeadlineE.text.toString().trim()
                        mActivityViewModel.searchUpcomingEvent(
                            null,
                            search.query.toString().trim(),
                            if (startDateS.isNotEmpty()) startDateS else null,
                            if (startDateE.isNotEmpty()) startDateE else null,
                            if (deadlineS.isNotEmpty()) deadlineS else null,
                            if (deadlineE.isNotEmpty()) deadlineE else null,
                            if (sortBy != 0) sortBy - 1 else null,
                            0,
                            toolbarPageSize
                        )

                    }
                }
                return true
            }
        })
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