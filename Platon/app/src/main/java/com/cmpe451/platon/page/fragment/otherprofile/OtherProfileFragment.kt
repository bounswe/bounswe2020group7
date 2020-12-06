package com.cmpe451.platon.page.fragment.otherprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.OtherUserProjectsAdapter
import com.cmpe451.platon.adapter.ProfilePageAdapter
import com.cmpe451.platon.adapter.UserProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentProfilePageOthersPrivateBinding
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageFragmentDirections
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions
import com.cmpe451.platon.util.Definitions.USERSTATUS
import kotlin.properties.Delegates

class OtherProfileFragment: Fragment(), OtherUserProjectsAdapter.OtherUserProjectButtonClickListener {


    private lateinit var binding: FragmentProfilePageOthersPrivateBinding
    private lateinit var details: ArrayList<MutableMap<String,String>>
    private var userId :Int? = null
    private val args: OtherProfileFragmentArgs by navArgs()

    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mOtherProfileViewModel: OtherProfileViewModel by viewModels()

    private lateinit var userProjectsRecyclerView: RecyclerView
    private lateinit var userProjectsAdapter: OtherUserProjectsAdapter
    private lateinit var informationsRecyclerView: RecyclerView
    private lateinit var informationsAdapter: ProfilePageAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageOthersPrivateBinding.inflate(inflater)
        details = ArrayList()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapters()
        fetchUser()
        setObservers()
    }

    private fun initializeAdapters() {
        userProjectsRecyclerView = binding.rvProfilePageProjects
        userProjectsAdapter = OtherUserProjectsAdapter(ArrayList(), requireContext(), this)
        userProjectsRecyclerView.adapter = userProjectsAdapter
        userProjectsRecyclerView.layoutManager = LinearLayoutManager(this.activity)



        informationsRecyclerView = binding.rvProfilePageInfo
        informationsAdapter = ProfilePageAdapter(ArrayList())
        informationsRecyclerView.adapter = informationsAdapter
        informationsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun setObservers() {
        mOtherProfileViewModel.currentUser.observe(viewLifecycleOwner){
            if(it != null){
                binding.textNameSurname.text = it.name!! + " " + it.surname!!
                Glide.with(this).load(it.profile_photo).into(binding.profilePhoto);
                mOtherProfileViewModel.setUserInfo()
                setView(mOtherProfileViewModel.isFollowing.value!!, mOtherProfileViewModel.isUserPrivate.value!!)
                setListeners(mOtherProfileViewModel.isFollowing.value!!, mOtherProfileViewModel.isUserPrivate.value!!)
            }
        }
        mOtherProfileViewModel.isFollowing.observe(viewLifecycleOwner){
            if(it != null){
                setView(it, mOtherProfileViewModel.isUserPrivate.value!!)
                setListeners(it, mOtherProfileViewModel.isUserPrivate.value!!)
            }
        }
        mOtherProfileViewModel.currentResarch.observe(viewLifecycleOwner, Observer { t ->
            if(t != null && t.isNotEmpty()) {
                userProjectsAdapter.submitElements(t)
            }
        })
        mOtherProfileViewModel.followResponse.observe(viewLifecycleOwner, Observer{
            if(it != null){
                if(it.first == 200){
                    if(mOtherProfileViewModel.isUserPrivate.value!!){
                        mOtherProfileViewModel.setIsFollowing(USERSTATUS.REQUESTED)
                    }
                    else {
                        mOtherProfileViewModel.setIsFollowing(USERSTATUS.FOLLOWING)
                    }
                }
                else {
                    Toast.makeText(activity, it.second, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setView(status:USERSTATUS, isUserPrivate:Boolean) {
        if(status == USERSTATUS.FOLLOWING){
            binding.buttonFollow.text = "FOLLOWING"
            binding.buttonFollow.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary_dark_lighter2))
            binding.projectInfoLl.visibility = View.VISIBLE
            binding.privatePageWarningTv.visibility = View.GONE
            val x = ArrayList<Map<String, String>>()
            x.add(mapOf(Pair("title", "E-mail"), Pair("info", mOtherProfileViewModel.currentUser.value!!.e_mail!!)))
            x.add(mapOf(Pair("title", "Job"), Pair("info", mOtherProfileViewModel.currentUser.value!!.job!!)))
            x.add(mapOf(Pair("title", "Rating"), Pair("info", mOtherProfileViewModel.currentUser.value!!.rate.toString())))
            informationsAdapter.submitList(x)
            mOtherProfileViewModel.fetchResearch((activity as HomeActivity).token!!,
                mOtherProfileViewModel.currentUser.value?.id!!
            )
        }
        if(status == USERSTATUS.REQUESTED){
            binding.buttonFollow.text = "REQUESTED"
            binding.buttonFollow.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary_dark_lighter2))
            binding.projectInfoLl.visibility = View.GONE
            binding.privatePageWarningTv.visibility = View.VISIBLE
        }
        if(status == USERSTATUS.NOT_FOLLOWING){
            binding.buttonFollow.text = "FOLLOW"
            binding.buttonFollow.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.secondary_green))
            if(isUserPrivate){
                binding.projectInfoLl.visibility = View.GONE
                binding.privatePageWarningTv.visibility = View.VISIBLE

            }
            else {
                binding.projectInfoLl.visibility = View.VISIBLE
                binding.privatePageWarningTv.visibility = View.GONE
                val x = ArrayList<Map<String, String>>()
                x.add(mapOf(Pair("title", "E-mail"), Pair("info", mOtherProfileViewModel.currentUser.value!!.e_mail!!)))
                x.add(mapOf(Pair("title", "Job"), Pair("info", mOtherProfileViewModel.currentUser.value!!.job!!)))
                x.add(mapOf(Pair("title", "Rating"), Pair("info", mOtherProfileViewModel.currentUser.value!!.rate.toString())))
                informationsAdapter.submitList(x)
                mOtherProfileViewModel.fetchResearch((activity as HomeActivity).token!!,
                    mOtherProfileViewModel.currentUser.value?.id!!
                )
            }

        }
    }

    private fun setListeners(status:USERSTATUS, isUserPrivate:Boolean){
        if(status == USERSTATUS.FOLLOWING){
            binding.buttonFollowers.setOnClickListener {
                findNavController().navigate(
                    OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment2(
                        "follower", mOtherProfileViewModel.currentUser.value?.id!!
                    )
                )
            }

            binding.buttonFollowing.setOnClickListener {
                findNavController().navigate(
                    OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment2(
                        "following", mOtherProfileViewModel.currentUser.value?.id!!
                    )
                )
            }
            binding.buttonFollow.setOnClickListener {
                mOtherProfileViewModel.setIsFollowing(USERSTATUS.NOT_FOLLOWING)
            }

        }
        if(status == USERSTATUS.REQUESTED){
            binding.buttonFollowers.setOnClickListener {
                Toast.makeText(activity, "To see the followers, please wait for the user to accept your request", Toast.LENGTH_LONG).show()
            }

            binding.buttonFollowing.setOnClickListener {
                Toast.makeText(activity, "To see the following, please wait for the user to accept your request", Toast.LENGTH_LONG).show()
            }
            binding.buttonFollow.setOnClickListener {
                mOtherProfileViewModel.setIsFollowing(USERSTATUS.NOT_FOLLOWING)
            }
        }
        if(status == USERSTATUS.NOT_FOLLOWING){
            binding.buttonFollow.setOnClickListener {
                mOtherProfileViewModel.follow(mProfilePageViewModel.getUser.value?.id!!, mOtherProfileViewModel.currentUser.value?.id!!, (activity as HomeActivity).token!!)
            }
            if(isUserPrivate){
                binding.buttonFollowers.setOnClickListener {
                    Toast.makeText(activity, "To see the followers, please send a follow request", Toast.LENGTH_LONG).show()
                }

                binding.buttonFollowing.setOnClickListener {
                    Toast.makeText(activity, "To see the following, please send a follow request", Toast.LENGTH_LONG).show()
                }

            }
            else {
                binding.buttonFollowers.setOnClickListener {
                    findNavController().navigate(
                        OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment2(
                            "follower", mOtherProfileViewModel.currentUser.value?.id!!
                        )
                    )
                }

                binding.buttonFollowing.setOnClickListener {
                    findNavController().navigate(
                        OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment2(
                            "following", mOtherProfileViewModel.currentUser.value?.id!!
                        )
                    )
                }
            }

        }
    }

    private fun fetchUser() {
        userId = args.userId
        if(userId != null && (activity as HomeActivity).token != null){
            mOtherProfileViewModel.getUser(userId!!, (activity as HomeActivity).token!!)
        }
    }

    private fun setUser(){
        /*user = presenter.getUser()
        binding.textNameSurname.text = user.name + " " + user.surname
        when(user.rate){
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

         */

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