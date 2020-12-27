package com.cmpe451.platon.page.activity.home.fragment.otherprofile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.cmpe451.platon.databinding.DialogAddCommentBinding
import com.cmpe451.platon.databinding.FragmentProfilePageOthersBinding
import com.cmpe451.platon.databinding.ResearchesCellBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Comment
import com.cmpe451.platon.network.models.OtherUser
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.util.Definitions
import com.cmpe451.platon.util.Definitions.USERSTATUS

class OtherProfileFragment: Fragment(), OtherUserProjectsAdapter.OtherUserProjectButtonClickListener, CommentsAdapter.OnCommentClickedListener {


    private lateinit var binding: FragmentProfilePageOthersBinding
    private lateinit var details: ArrayList<MutableMap<String,String>>
    private var userId :Int? = null
    private val args: OtherProfileFragmentArgs by navArgs()
    private lateinit var dialog:AlertDialog

    private lateinit var paginationListenerResearches:PaginationListener
    private lateinit var paginationListenerComments:PaginationListener
    private var maxPageNumberResearch:Int=0
    private var maxPageNumberComment:Int=0
    private val pageSize = 5

    private val mOtherProfileViewModel: OtherProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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
        val layoutManagerProjects = LinearLayoutManager(this.activity)
        paginationListenerResearches = object:PaginationListener(layoutManagerProjects) {
            override fun loadMoreItems() {
                if (maxPageNumberResearch - 1 > currentPage) {
                    isLoading = true
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
        }

        val layoutManagerComments = LinearLayoutManager(this.activity)
        paginationListenerComments = object:PaginationListener(layoutManagerComments, pageSize) {
            override fun loadMoreItems() {
                if (maxPageNumberComment - 1 > currentPage) {
                    isLoading = true
                    currentPage++
                    mOtherProfileViewModel.getComments(
                        userId!!,
                        (activity as HomeActivity).currUserToken,
                        currentPage,
                        PAGE_SIZE
                    )
                }

            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        val height = resources.displayMetrics.heightPixels

        binding.rvProfilePageProjects.adapter = OtherUserProjectsAdapter(ArrayList(), requireContext(), this)
        binding.rvProfilePageProjects.layoutManager = layoutManagerProjects
        binding.rvProfilePageProjects.layoutParams =
            LinearLayout.LayoutParams(binding.rvProfilePageProjects.layoutParams.width, height / 3)


        binding.rvProfilePageComments.adapter = CommentsAdapter(ArrayList(), requireContext(), this, (activity as HomeActivity).currUserId)
        binding.rvProfilePageComments.layoutManager = layoutManagerComments
        binding.rvProfilePageComments.layoutParams =
            LinearLayout.LayoutParams(binding.rvProfilePageProjects.layoutParams.width, height / 3)
        binding.rvProfilePageComments.addOnScrollListener(paginationListenerComments)


        binding.rvProfilePageProjects.addOnScrollListener(paginationListenerResearches)
        binding.rvProfilePageSkills.adapter = SkillsAdapter(ArrayList(), requireContext())
        binding.rvProfilePageSkills.layoutManager = GridLayoutManager(this.activity, 3)

        dialog = Definitions().createProgressBar(requireContext())
    }

    private fun setObservers() {
        mOtherProfileViewModel.getUserComments.observe(viewLifecycleOwner, {t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    if(t.data!!.result.isNotEmpty()){
                        (binding.rvProfilePageComments.adapter as CommentsAdapter).submitElements(t.data!!.result)
                    }else{
                        Toast.makeText(requireContext(), "No comment found", Toast.LENGTH_SHORT).show()
                    }
                    paginationListenerComments.isLoading = false
                    mOtherProfileViewModel.getUserComments.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mOtherProfileViewModel.getUserComments.value = Resource.Done()
                }
                Resource.Done::class.java->dialog.dismiss()
            }
        })

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
                    paginationListenerResearches.isLoading = false
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

        mOtherProfileViewModel.getAddDeleteCommentResourceResponse.observe(viewLifecycleOwner, {t->
            when(t.javaClass){
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    paginationListenerComments.currentPage = 0
                    (binding.rvProfilePageComments.adapter as CommentsAdapter).clearElements()
                    mOtherProfileViewModel.getComments(userId!!, (activity as HomeActivity).currUserToken, 0, pageSize)
                }
                Resource.Error::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading::class.java -> dialog.show()
            }
        })


        mOtherProfileViewModel.userSkills.observe(viewLifecycleOwner, {t->
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
            binding.ratingBar.rating = user?.rate?.toFloat() ?: 0.0.toFloat()
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


        if(user!!.can_comment){
            val tmpBinding = DialogAddCommentBinding.inflate(layoutInflater, binding.root, false)
            val addCommentDialog  =
                AlertDialog.Builder(requireContext())
                .setView(tmpBinding.root).create()
            addCommentDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.ivAddComment.setOnClickListener{
                addCommentDialog.show()
            }

            tmpBinding.btnAddComment.setOnClickListener{
                var comment:String? = null
                if(!tmpBinding.etRating.text.isNullOrEmpty()){
                    val rating = tmpBinding.etRating.text.toString().toInt()
                    if(!tmpBinding.etComment.text.isNullOrEmpty()){
                        comment=tmpBinding.etComment.text.toString().trim()
                    }
                    mOtherProfileViewModel.addComment(rating, comment,  userId!!,(activity as HomeActivity).currUserToken)
                    addCommentDialog.cancel()
                }else{
                    Toast.makeText(requireContext(), "Please rate the user!", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            binding.ivAddComment.visibility = View.GONE
        }

        if(status == USERSTATUS.FOLLOWING){

            binding.tvCommentsTitle.setOnClickListener{
                when(binding.layComments.visibility){
                    View.GONE->{
                        //getComments
                        mOtherProfileViewModel.getComments(userId!!, (activity as HomeActivity).currUserToken, 0, pageSize)
                           binding.layComments.visibility = View.VISIBLE
                    }
                    View.VISIBLE->{
                        (binding.rvProfilePageComments.adapter as CommentsAdapter).clearElements()
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

    override fun onDeleteCommentClicked(element: Comment, position: Int) {
       mOtherProfileViewModel.deleteComment(element.comment_id, (activity as HomeActivity).currUserToken)
    }


}