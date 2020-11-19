package com.cmpe451.platon.page.fragment.preLogin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.getDrawable
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.adapter.UpcomingEventsAdapter
import com.cmpe451.platon.databinding.FragmentPreLoginBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.preLogin.contract.PreLoginContract
import com.cmpe451.platon.page.fragment.preLogin.model.PreLoginRepository
import com.cmpe451.platon.page.fragment.preLogin.presenter.PreLoginPresenter
import com.cmpe451.platon.util.TrendingProject
import com.cmpe451.platon.util.UpcomingEvent

class PreLoginFragment : Fragment(), PreLoginContract.View, TrendingProjectsAdapter.TrendingProjectButtonClickListener, UpcomingEventsAdapter.UpcomingButtonClickListener  {

    private var presenter: PreLoginContract.Presenter? = null
    private lateinit var fragmentChangeListener: FragmentChangeListener
    private lateinit var trendingProjectsRecyclerView: RecyclerView
    private lateinit var upcomingEventsRecyclerView: RecyclerView


    val myDataset = arrayOf(5,7,8,1,20,1,5,58,8)

    private lateinit var textHello: TextView

    lateinit var binding: FragmentPreLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initializePresenter()
        initViews(view)
        setFragmentChangeListener()
        setListeners()


        presenter?.onPreLoginMade()

    }


    private fun initViews(root: View) {
        val myTrendingProject = arrayOf<TrendingProject>(
                TrendingProject("PLATON", context?.let { getDrawable(it, R.drawable.ic_home_24px ) },"A collaboration application for all platforms.", TrendingProject.TREND.HOT),
                TrendingProject("ANDROID: Reborn", context?.let { getDrawable(it, R.drawable.ic_home_24px ) }, "A new version of android.", TrendingProject.TREND.POPULAR),
                TrendingProject("DRAGON CENTER", context?.let { getDrawable(it, R.drawable.ic_account_24px ) }, "Msi is developing a new version of dragon center. Join now!", TrendingProject.TREND.MOST_LIKED),
                TrendingProject("COMPUTER VISION For Phones", context?.let { getDrawable(it, R.drawable.ic_work_24px ) }, "A research for cv, prepared for cvvp.", TrendingProject.TREND.NEW_COMERS),
                TrendingProject("D++", context?.let { getDrawable(it, R.drawable.ic_home_24px ) }, "A new language development for childs.", TrendingProject.TREND.HOT))


        val myUpcomingEvents = arrayOf<UpcomingEvent>(
                UpcomingEvent("CVPR", "Computer Vision conference, yearly", context?.let { getDrawable(it, R.drawable.ic_home_24px ) }, UpcomingEvent.TYPE.CONFERENCE, "12.02.2021"),
                UpcomingEvent("IJSR", "International Journal of Science and Research (IJSR) is a journal", context?.let { getDrawable(it, R.drawable.ic_account_24px ) }, UpcomingEvent.TYPE.JOURNAL, "12.02.2021" ),
                UpcomingEvent("ACS Nano", "Call for papers, acs nano", context?.let { getDrawable(it, R.drawable.ic_work_24px ) }, UpcomingEvent.TYPE.JOURNAL, "12.02.2021" ),
                UpcomingEvent("ECCV", "Computer Vision conference, yearly, European Conference on Computer Vision", context?.let { getDrawable(it, R.drawable.ic_home_24px ) }, UpcomingEvent.TYPE.CONFERENCE, "12.02.2021" )

        )


        trendingProjectsRecyclerView = binding.preLoginTrendingProjectsRecyclerView
        trendingProjectsRecyclerView.adapter = TrendingProjectsAdapter(myTrendingProject, requireContext(), this)
        trendingProjectsRecyclerView.layoutManager = LinearLayoutManager(context)

        upcomingEventsRecyclerView = binding.preLoginUpcomingEventsRecyclerView
        upcomingEventsRecyclerView.adapter = UpcomingEventsAdapter(myUpcomingEvents,requireContext(), this)
        upcomingEventsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setFragmentChangeListener() {
        this.fragmentChangeListener = activity as FragmentChangeListener
    }

    private fun setListeners() {

        //password.addTextChangedListener(textWatcher)
    }

    private fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = PreLoginRepository(sharedPreferences)

        presenter = PreLoginPresenter(this,repository, sharedPreferences )
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