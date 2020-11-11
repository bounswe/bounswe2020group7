package com.example.platon.page.fragment.PreLogin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.platon.R
import com.example.platon.`interface`.FragmentChangeListener
import com.example.platon.adapter.TrendingProjectsAdapter
import com.example.platon.adapter.UpcomingEventsAdapter
import com.example.platon.page.fragment.PreLogin.contract.PreLoginContract

class PreLoginFragment : Fragment(), PreLoginContract.View, TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener  {

    private var presenter: PreLoginContract.Presenter? = null
    private lateinit var fragmentChangeListener: FragmentChangeListener
    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView
    val myDataset = arrayOf<Int>(8, 7, 6, 5, 4, 3, 2, 1, -1, -2, -3, -4)
    private lateinit var textHello: TextView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
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