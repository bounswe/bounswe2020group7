package com.cmpe451.platon.page.fragment.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.adapter.FollowerFollowingRecyclerViewAdapter
import com.cmpe451.platon.databinding.FragmentFollowersFollowingListBinding
import com.cmpe451.platon.networkmodels.models.FollowPerson
import com.cmpe451.platon.networkmodels.models.OtherUser
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.follow.view.FollowFragmentArgs
import com.cmpe451.platon.page.fragment.profilepage.presenter.ProfilePageViewModel

class FollowFragment:Fragment() {
    private lateinit var binding: FragmentFollowersFollowingListBinding
    private lateinit var following: ArrayList<OtherUser>
    private lateinit var adapter: FollowerFollowingRecyclerViewAdapter
    private lateinit var rvFollowers: RecyclerView
    private var userId:Int? = null
    private lateinit var token:String
    private val mFollowViewModel: FollowViewModel by viewModels()
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    val args: FollowFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowersFollowingListBinding.inflate(inflater)
        following = ArrayList()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        token = (activity as HomeActivity).token.toString()
        userId = mProfilePageViewModel.getUser.value?.id
        if(args.follow == "follower"){
            mFollowViewModel.followers.observe(viewLifecycleOwner){
                if(it != null && it.followers.isNotEmpty()){
                    adapter.submitList(it.followers as ArrayList<FollowPerson>)
                }
            }
            mFollowViewModel.fetchFollowers(userId, token)
        }
        else {
            mFollowViewModel.following.observe(viewLifecycleOwner){
                if(it != null && it.followings.isNotEmpty()) {
                    adapter.submitList(it.followings as ArrayList<FollowPerson>)
                }
            }
            mFollowViewModel.fetchFollowing(userId, token)
        }

    }
    private fun setAdapter(){
        rvFollowers = binding.rvFollow
        adapter = FollowerFollowingRecyclerViewAdapter(ArrayList()) { userId:Int->
            Toast.makeText(activity, userId, Toast.LENGTH_LONG)
//            (activity as HomeActivity).navController.navigate(FollowersFollowingFragmentDirections.actionFollowersFollowingFragmentToProfilePagePrivateFragment(id))
        }
        rvFollowers.adapter = adapter
        rvFollowers.layoutManager = LinearLayoutManager(this.activity)
    }
}