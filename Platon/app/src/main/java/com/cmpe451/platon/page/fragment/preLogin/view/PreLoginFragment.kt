package com.cmpe451.platon.page.fragment.preLogin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.adapter.UpcomingEventsAdapter
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract

class PreLoginFragment : Fragment(), PreLoginContract.View, TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener  {

    private var presenter: PreLoginContract.Presenter? = null
    private lateinit var fragmentChangeListener: FragmentChangeListener
    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView
    val myDataset = arrayOf<Int>(8, 7, 6, 5, 4, 3, 2, 1, -1, -2, -3, -4)
    private lateinit var textHello: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pre_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setFragmentChangeListener()
        setListeners()
        initializePresenter()
    }


    private fun initViews(root: View) {

        trendingProjectsRecyclerView = root.findViewById(R.id.pre_login_trending_projects_recycler_view)
        trendingProjectsRecyclerView.adapter = TrendingProjectsAdapter(myDataset,requireContext(), this)
        trendingProjectsRecyclerView.layoutManager = LinearLayoutManager(context)

        upcomingEventsRecyclerView = root.findViewById(R.id.pre_login_upcoming_events_recycler_view)
        upcomingEventsRecyclerView.adapter = UpcomingEventsAdapter(myDataset,requireContext(), this)
        upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setFragmentChangeListener() {
        this.fragmentChangeListener = activity as FragmentChangeListener
    }

    private fun setListeners() {
        //loginButton.setOnClickListener {
        //    showProgressBar()
        //    val username = username.text.toString().trim()
        //    val password = password.text.toString().trim()
        //    presenter?.login(username, password)
        //}

        //password.addTextChangedListener(textWatcher)
    }

    private fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        //val repository = LoginRepository(sharedPreferences)
        //setPresenter(LoginPresenter(this, repository, sharedPreferences))
    }


    override fun setPresenter(presenter: PreLoginContract.Presenter) {
        TODO("Not yet implemented")
    }

    override fun onUpcomingButtonClicked(buttonName: String) {
        TODO("Not yet implemented")
    }

    override fun onTrendingProjectButtonClicked(buttonName: String) {
        TODO("Not yet implemented")
    }
}