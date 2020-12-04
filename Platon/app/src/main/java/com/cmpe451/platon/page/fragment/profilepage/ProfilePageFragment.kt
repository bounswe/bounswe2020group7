package com.cmpe451.platon.page.fragment.profilepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.ProfilePageRecyclerViewAdapter
import com.cmpe451.platon.adapter.UserProjectsRecyclerViewAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.home.HomeViewModel
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageFragmentDirections
import com.cmpe451.platon.page.fragment.researchInfo.ResearchInfoViewModel
import com.cmpe451.platon.util.Definitions

class ProfilePageFragment : Fragment(), UserProjectsRecyclerViewAdapter.UserProjectButtonClickListener {

    private lateinit var binding: FragmentProfilePageBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mResearchInfoViewModel: ResearchInfoViewModel by viewModels()

    private lateinit var userProjectsRecyclerView: RecyclerView
    private lateinit var userProjectsAdapter: UserProjectsRecyclerViewAdapter
    private lateinit var informationsRecyclerView: RecyclerView
    private lateinit var informationsAdapter: ProfilePageRecyclerViewAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializePresenter()
        initializeAdapter()


        if(mProfilePageViewModel.getUser.value != null){
            val x = ArrayList<Map<String, String>>()
            x.add(mapOf(Pair("title", "E-mail"), Pair("info", mProfilePageViewModel.getUser.value!!.e_mail)))
            x.add(mapOf(Pair("title", "Job"), Pair("info", mProfilePageViewModel.getUser.value!!.job)))
            x.add(mapOf(Pair("title", "Rating"), Pair("info", mProfilePageViewModel.getUser.value!!.rate.toString())))
            informationsAdapter.submitList(x)

            binding.textNameSurname.text = mProfilePageViewModel.getUser.value!!.name + " " + mProfilePageViewModel.getUser.value!!.surname
            mProfilePageViewModel.fetchResearch((activity as HomeActivity).token,
                mProfilePageViewModel.getUser.value?.id
            )

        }

        mProfilePageViewModel.getUser.observe(viewLifecycleOwner) { t ->
            if(t != null){
                val x = ArrayList<Map<String, String>>()
                x.add(mapOf(Pair("title", "E-mail"), Pair("info", t.e_mail)))
                x.add(mapOf(Pair("title", "Job"), Pair("info", t.job)))
                x.add(mapOf(Pair("title", "Rating"), Pair("info", t.rate.toString())))
                informationsAdapter.submitList(x)

                binding.textNameSurname.text = t.name + " " + t.surname

            }
        }

        mProfilePageViewModel.getResearches.observe(viewLifecycleOwner
        ) { t->
            if(t != null && t.isNotEmpty()) {
                userProjectsAdapter.submitElements(t)
            }
        }

        setListeners()
    }


    private fun initializeAdapter() {
        userProjectsRecyclerView = binding.rvProfilePageProjects
        userProjectsAdapter = UserProjectsRecyclerViewAdapter(ArrayList(), requireContext(), this)
        userProjectsRecyclerView.adapter = userProjectsAdapter
        userProjectsRecyclerView.layoutManager = LinearLayoutManager(this.activity)



        informationsRecyclerView = binding.rvProfilePageInfo
        informationsAdapter = ProfilePageRecyclerViewAdapter(ArrayList())
        informationsRecyclerView.adapter = informationsAdapter
        informationsRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
    }

    private fun setListeners() {

        binding.buttonFollowers.setOnClickListener {
           findNavController().navigate(
               ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment(
                   "follower"
               )
           )
        }

        binding.buttonFollowing.setOnClickListener {
            findNavController().navigate(
                ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment(
                    "following"
                )
            )
        }

        binding.buttonEditProfile.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
        }
        binding.addProjectIv.setOnClickListener{
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToAddResearchInfoFragment())
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



    override fun onUserProjectButtonClicked(binding: UserProjectsCellBinding, position: Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onUserProjectEditClicked(position: Int) {
        if(mProfilePageViewModel.getResearches.value?.get(position) != null){
            mResearchInfoViewModel.setCurrentResearch(mProfilePageViewModel.getResearches.value?.get(position)!!)
        }
        findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditResearchInfoFragment())
    }
}