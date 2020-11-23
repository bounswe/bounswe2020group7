package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.contract.ProfilePageContract
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import com.cmpe451.platon.page.fragment.profilepage.presenter.ProfilePagePresenter
import com.cmpe451.platon.adapter.ProfilePageRecyclerViewAdapter
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.adapter.UpcomingEventsAdapter
import com.cmpe451.platon.adapter.UserProjectsRecyclerViewAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.TrendProjectCellBinding
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.networkmodels.Research
import com.cmpe451.platon.networkmodels.ResearchResponse
import com.cmpe451.platon.util.Definitions

class ProfilePageFragment : Fragment(), ProfilePageContract.View, UserProjectsRecyclerViewAdapter.UserProjectButtonClickListener {

    private lateinit var binding: FragmentProfilePageBinding
    private lateinit var presenter: ProfilePageContract.Presenter
    private lateinit var details: ArrayList<MutableMap<String,String>>
    private lateinit var user :Definitions.User
    private lateinit var projects : List<Research>
    private lateinit var userProjectsRecyclerView: RecyclerView
    private lateinit var userProjectsAdapter: UserProjectsRecyclerViewAdapter
    private lateinit var informationsRecyclerView: RecyclerView
    private lateinit var informationsAdapter: ProfilePageRecyclerViewAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)
        details = ArrayList()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initializePresenter()
        presenter.bringResearches()
        initializeAdapter()

        setUser()
    }
    private fun setUser(){
        user = presenter.getUser()
        binding.textNameSurname.text = user.name + " " + user.surname
        when(user.rating){
            1.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.GONE
                binding.thirdStarYellow.visibility = View.GONE
                binding.forthStarYellow.visibility = View.GONE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.VISIBLE
                binding.thirdGrayStar.visibility = View.VISIBLE
                binding.forthGrayStar.visibility = View.VISIBLE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            2.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.GONE
                binding.forthStarYellow.visibility = View.GONE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.VISIBLE
                binding.thirdGrayStar.visibility = View.VISIBLE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            3.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.VISIBLE
                binding.forthStarYellow.visibility = View.GONE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.VISIBLE
                binding.thirdGrayStar.visibility = View.GONE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            4.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.VISIBLE
                binding.forthStarYellow.visibility = View.VISIBLE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.GONE
                binding.thirdGrayStar.visibility = View.GONE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            5.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.VISIBLE
                binding.forthStarYellow.visibility = View.VISIBLE
                binding.fifthStarYellow.visibility = View.VISIBLE
                binding.firstGrayStar.visibility = View.GONE
                binding.secondGrayStar.visibility = View.GONE
                binding.thirdGrayStar.visibility = View.GONE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE

            }
            else -> binding.textRate.visibility = View.VISIBLE
        }
    }

    private fun initializeAdapter() {


        userProjectsRecyclerView = binding.rvProfilePageProjects
        userProjectsAdapter = UserProjectsRecyclerViewAdapter(ArrayList(), requireContext(), this)
        userProjectsRecyclerView.adapter = userProjectsAdapter
        userProjectsRecyclerView.layoutManager = LinearLayoutManager(this.activity)



        informationsRecyclerView = binding.rvProfilePageInfo
        informationsAdapter = ProfilePageRecyclerViewAdapter(ArrayList())
        informationsRecyclerView.adapter = informationsAdapter
        informationsRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        (informationsRecyclerView.adapter as ProfilePageRecyclerViewAdapter).submitList(presenter.getProfilePageDetails())


    }

    private fun setListeners() {

        binding.buttonFollowers.setOnClickListener {
            presenter.onFollowersButtonClicked()
        }

        binding.buttonFollowing.setOnClickListener {
            presenter.onFollowingButtonClicked()
        }

        binding.buttonEditProfile.setOnClickListener {
            presenter.onEditProfileButtonClicked()
        }

        //password.addTextChangedListener(textWatcher)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // clear search bar, and make it icon
        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
    }

    override fun researchesFetched(reserachInfo: ResearchResponse) {
        val ert = reserachInfo
        userProjectsAdapter.submitElements(reserachInfo.research_info)
    }

    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = ProfilePageRepository(sharedPreferences)
        presenter = ProfilePagePresenter(this, repository, sharedPreferences, (activity as HomeActivity).navController )

    }


    override fun onUserProjectButtonClicked(binding: UserProjectsCellBinding, position: Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }
}