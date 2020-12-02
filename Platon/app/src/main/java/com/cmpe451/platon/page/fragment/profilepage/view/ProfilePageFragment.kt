package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.ProfilePageRecyclerViewAdapter
import com.cmpe451.platon.adapter.UserProjectsRecyclerViewAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.page.fragment.profilepage.presenter.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions

class ProfilePageFragment : Fragment(), UserProjectsRecyclerViewAdapter.UserProjectButtonClickListener {

    private lateinit var binding: FragmentProfilePageBinding
    lateinit var mProfilePageViewModel: ProfilePageViewModel

    private lateinit var userProjectsRecyclerView: RecyclerView
    private lateinit var userProjectsAdapter: UserProjectsRecyclerViewAdapter
    private lateinit var informationsRecyclerView: RecyclerView
    private lateinit var informationsAdapter: ProfilePageRecyclerViewAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)

        //init viewModels
        mProfilePageViewModel= ViewModelProvider(this).get(ProfilePageViewModel::class.java)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializePresenter()
        initializeAdapter()

        /*
        mProfilePageViewModel.getUsers.observe(viewLifecycleOwner, { t ->
            if(t != null && t.isNotEmpty()){
                val x = ArrayList<Map<String, String>>()
                x.add(mapOf(Pair("title", "E-mail"), Pair("info", t[0].e_mail)))
                x.add(mapOf(Pair("title", "Job"), Pair("info", t[0].job)))
                x.add(mapOf(Pair("title", "Rating"), Pair("info", t[0].rate.toString())))
                informationsAdapter.submitList(x)

                binding.textNameSurname.text = t[0].name + " " + t[0].surname

            }
        })

        mProfilePageViewModel.getResearches.observe(viewLifecycleOwner, { t->
            if(t != null && t.isNotEmpty()) {
                userProjectsAdapter.submitElements(t)
            }
            }
        )

         */

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
        informationsRecyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setListeners() {

        binding.buttonFollowers.setOnClickListener {
           findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowerFragment())
        }

        binding.buttonFollowing.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment())
        }

        binding.buttonEditProfile.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
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
}