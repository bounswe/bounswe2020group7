package com.cmpe451.platon.page.activity.home.fragment.otherprofile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.CommentsAdapter
import com.cmpe451.platon.adapter.OtherUserProjectsAdapter
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.adapter.UserProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentProfilePageOthersBinding
import com.cmpe451.platon.databinding.ResearchesCellBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Comment
import com.cmpe451.platon.network.models.OtherUser
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.util.Definitions
import com.cmpe451.platon.util.Definitions.USERSTATUS

class OtherProfileFragment: Fragment(), OtherUserProjectsAdapter.OtherUserProjectButtonClickListener {


    private lateinit var binding: FragmentProfilePageOthersBinding
    private lateinit var details: ArrayList<MutableMap<String,String>>
    private var userId :Int? = null
    private val args: OtherProfileFragmentArgs by navArgs()
    private lateinit var dialog:AlertDialog

    private val mOtherProfileViewModel: OtherProfileViewModel by viewModels()
    private var maxPageNumberResearch:Int=0

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageOthersBinding.inflate(inflater)
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
        val height = resources.displayMetrics.heightPixels

        val layoutManagerComments = LinearLayoutManager(this.activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProfilePageComments.layoutManager = layoutManagerComments

        val layoutManagerProjects = LinearLayoutManager(this.activity)

        binding.rvProfilePageProjects.adapter = OtherUserProjectsAdapter(ArrayList(), requireContext(), this)
        binding.rvProfilePageProjects.layoutManager = layoutManagerProjects

        binding.rvProfilePageProjects.layoutParams =
            LinearLayout.LayoutParams(binding.rvProfilePageProjects.layoutParams.width, height / 3)


        binding.rvProfilePageProjects.addOnScrollListener(object :
            PaginationListener(layoutManagerProjects) {
            override fun loadMoreItems() {
                if (maxPageNumberResearch - 1 > currentPage) {
                    currentPage++
                    mOtherProfileViewModel.fetchResearch(
                        (activity as HomeActivity).currUserToken,
                        userId!!,
                        currentPage,
                        5
                    )
                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        })

        binding.rvProfilePageSkills.adapter = SkillsAdapter(ArrayList(), requireContext())
        binding.rvProfilePageSkills.layoutManager = GridLayoutManager(this.activity, 3)

        dialog = Definitions().createProgressBar(requireContext())
    }

    private fun setObservers() {
        mOtherProfileViewModel.getUserResource.observe(viewLifecycleOwner,Observer{ i->
            when(i.javaClass){
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    val user = i.data!!
                    binding.textNameSurname.text = user.name + " " + user.surname
                    Glide.with(this)
                        .load(Definitions.API_URL + "api" + user.profile_photo)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                        .placeholder(R.drawable.ic_o_logo)
                        .into(binding.profilePhoto);
                    Log.i("Addr", Definitions.API_URL + "api" + user.profile_photo)
                    mOtherProfileViewModel.setUserInfo()
                    setView(mOtherProfileViewModel.isFollowing.value!!, mOtherProfileViewModel.isUserPrivate.value!!)
                    setListeners(mOtherProfileViewModel.isFollowing.value!!, mOtherProfileViewModel.isUserPrivate.value!!)
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java-> {
                    dialog.dismiss()
                    Toast.makeText(activity, i.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        mOtherProfileViewModel.isFollowing.observe(viewLifecycleOwner, Observer{it->
            if(it != null){
                setView(it, mOtherProfileViewModel.isUserPrivate.value!!)
                setListeners(it, mOtherProfileViewModel.isUserPrivate.value!!)
            }
        })

        mOtherProfileViewModel.getResearchesResource.observe(viewLifecycleOwner, Observer { i ->
            when(i.javaClass){
                Resource.Success::class.java -> {

                    if(i.data != null && i.data!!.research_info != null){
                        val researches = i.data!!.research_info!!
                        (binding.rvProfilePageProjects.adapter as OtherUserProjectsAdapter).submitElements(researches)
                    }

                    dialog.dismiss()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java->{
                    Toast.makeText(activity, i.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })

        mOtherProfileViewModel.getFollowResourceResponse.observe(viewLifecycleOwner, Observer{i->
            when(i.javaClass){
                Resource.Success::class.java -> {
                    if(mOtherProfileViewModel.isUserPrivate.value!!){
                        mOtherProfileViewModel.setIsFollowing(USERSTATUS.REQUESTED)
                    }
                    else {
                        mOtherProfileViewModel.setIsFollowing(USERSTATUS.FOLLOWING)
                    }
                    dialog.dismiss()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java->{
                    Toast.makeText(activity, i.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })
        mOtherProfileViewModel.getUnfollowResourceResponse.observe(viewLifecycleOwner, Observer {
            when(it.javaClass){
                Resource.Success::class.java -> {
                    mOtherProfileViewModel.setIsFollowing(USERSTATUS.NOT_FOLLOWING)
                    dialog.dismiss()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java->{
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })
        mOtherProfileViewModel.userSkills.observe(viewLifecycleOwner, Observer{t->
            when(t.javaClass){
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    if(t.data != null && t.data!!.skills != null){
                        (binding.rvProfilePageSkills.adapter as SkillsAdapter).submitElements(t.data!!.skills!!.map { it.name })
                    }

                }
                Resource.Error::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading::class.java -> dialog.show()
            }
        })

    }

    private fun setView(status:USERSTATUS, isUserPrivate:Boolean) {
        val user = mOtherProfileViewModel.getUserResource.value!!.data

        if(status == USERSTATUS.FOLLOWING){
            binding.buttonFollow.text = "FOLLOWING"
            binding.buttonFollow.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.primary_dark_lighter2))
            binding.projectInfoLl.visibility = View.VISIBLE
            binding.privatePageWarningTv.visibility = View.GONE

            binding.tvEmail.text = user?.e_mail
            binding.tvInstitution.text = user?.institution ?: "Institution not specified!"
            binding.tvJob.text = user?.job

            mOtherProfileViewModel.fetchResearch((activity as HomeActivity).currUserToken, user!!.id!!,0,5)
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
                binding.tvEmail.text = user?.e_mail
                binding.tvInstitution.text = user?.institution ?: "Institution not specified!"
                binding.tvJob.text = user?.job
                mOtherProfileViewModel.fetchResearch((activity as HomeActivity).currUserToken, user!!.id!!, 0, 5)
            }

        }
    }

    private fun setListeners(status:USERSTATUS, isUserPrivate:Boolean){
        val user = mOtherProfileViewModel.getUserResource.value!!.data

        if(status == USERSTATUS.FOLLOWING){

            binding.tvCommentsTitle.setOnClickListener{
                when(binding.layComments.visibility){
                    View.GONE->{
                        //getComments
                        binding.rvProfilePageComments.adapter = CommentsAdapter(arrayListOf(Comment(0,"hehe", "haha", "today", 2.5)), requireContext())
                        binding.layComments.visibility = View.VISIBLE
                    }
                    View.VISIBLE->{
                        binding.layComments.visibility = View.GONE
                    }
                }

            }


            binding.buttonFollowers.setOnClickListener {
                findNavController().navigate(
                    OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment2(
                        "follower", user?.id!!
                    )
                )
            }

            binding.buttonFollowing.setOnClickListener {
                findNavController().navigate(
                    OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment2(
                        "following", user?.id!!
                    )
                )
            }
            binding.buttonFollow.setOnClickListener {
                mOtherProfileViewModel.unfollow(mOtherProfileViewModel.getUserResource.value!!.data?.id!!, (activity as HomeActivity).currUserToken)
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
                mOtherProfileViewModel.follow((activity as HomeActivity).currUserId, user?.id!!, (activity as HomeActivity).currUserToken)
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
                            "follower", user?.id!!
                        )
                    )
                }

                binding.buttonFollowing.setOnClickListener {
                    findNavController().navigate(
                        OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment2(
                            "following",user?.id!!
                        )
                    )
                }
            }

        }
    }

    private fun fetchUser() {
        userId = args.userId
        if(userId != null){
            mOtherProfileViewModel.getUser(userId!!, (activity as HomeActivity).currUserToken)
            mOtherProfileViewModel.getUserSkills(userId!!, (activity as HomeActivity).currUserToken)
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onUserProjectButtonClicked(binding: ResearchesCellBinding, position: Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }


}