package com.cmpe451.platon.page.fragment.profilepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.ProfilePageAdapter
import com.cmpe451.platon.adapter.UserProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.follow.FollowViewModel
import com.cmpe451.platon.page.fragment.researchInfo.ResearchInfoViewModel
import com.cmpe451.platon.util.Definitions

class ProfilePageFragment : Fragment(), UserProjectsAdapter.UserProjectButtonClickListener {

    private lateinit var binding: FragmentProfilePageBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mResearchInfoViewModel: ResearchInfoViewModel by activityViewModels()

    private lateinit var userProjectsRecyclerView: RecyclerView
    private lateinit var userProjectsAdapter: UserProjectsAdapter
    private lateinit var informationsRecyclerView: RecyclerView
    private lateinit var informationsAdapter: ProfilePageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapters()
        setListeners()
    }

    private fun initializeAdapters() {
        userProjectsRecyclerView = binding.rvProfilePageProjects
        userProjectsAdapter = UserProjectsAdapter(ArrayList(), requireContext(), this)
        userProjectsRecyclerView.adapter = userProjectsAdapter
        userProjectsRecyclerView.layoutManager = LinearLayoutManager(this.activity)

        informationsRecyclerView = binding.rvProfilePageInfo
        informationsAdapter = ProfilePageAdapter(ArrayList())
        informationsRecyclerView.adapter = informationsAdapter
        informationsRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
    }


    private fun setListeners() {
        setButtonListeners()
        setObservers()
    }

    private fun setObservers() {
        mProfilePageViewModel.getUser.observe(viewLifecycleOwner, { t->
            if(t != null){
                informationsAdapter.submitList(mProfilePageViewModel.getPersonalInformation())
                val naming = t.name + " " + t.surname
                binding.textNameSurname.text = naming

                mProfilePageViewModel.fetchResearch((activity as HomeActivity).token, t.id)

                Glide.with(this)
                    .load(t.profile_photo)
                    .placeholder(R.drawable.ic_o_logo)
                    .into(binding.profilePhoto)
            }
        })

        mProfilePageViewModel.getResearches.observe(viewLifecycleOwner, { t ->
            if(t != null && t.isNotEmpty()) {
                userProjectsAdapter.submitElements(t)
            }
        })

    }


    private fun setButtonListeners() {
        binding.buttonFollowers.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment("follower"))
        }

        binding.buttonFollowing.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment("following"))
        }

        binding.buttonEditProfile.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
        }
        binding.addProjectIv.setOnClickListener{
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToAddResearchInfoFragment())
        }

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
        menu.findItem(R.id.logout_menu_btn)?.isVisible = true
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