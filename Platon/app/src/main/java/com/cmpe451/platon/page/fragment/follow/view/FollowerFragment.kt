package com.cmpe451.platon.page.fragment.follow.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.databinding.FragmentFollowerListBinding
import com.cmpe451.platon.adapter.FollowerFollowingRecyclerViewAdapter
import com.cmpe451.platon.networkmodels.Follower
import com.cmpe451.platon.networkmodels.Followers
import com.cmpe451.platon.networkmodels.Followings
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.follow.contract.FollowContract
import com.cmpe451.platon.page.fragment.follow.model.FollowRepository
import com.cmpe451.platon.page.fragment.follow.presenter.FollowPresenter

class FollowerFragment: Fragment(), FollowContract.View {
    private lateinit var binding: FragmentFollowerListBinding
    private lateinit var presenter: FollowPresenter
    private lateinit var following: ArrayList<Follower>
    private lateinit var adapter: FollowerFollowingRecyclerViewAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowerListBinding.inflate(inflater)
        following = ArrayList()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePresenter()
        val rvFollowers = binding.rvFollow
        adapter = FollowerFollowingRecyclerViewAdapter(ArrayList()) { id:Int->
            presenter.goToProfilePage(id)
//            (activity as HomeActivity).navController.navigate(FollowersFollowingFragmentDirections.actionFollowersFollowingFragmentToProfilePagePrivateFragment(id))
        }
        rvFollowers.adapter = adapter
        rvFollowers.layoutManager = LinearLayoutManager(this.activity)
        presenter.bringFollowers()
    }
    override fun fetchFollowers(followers: Followers) {
        adapter.submitList(ArrayList(followers.followers))
    }

    override fun fetchFollowing(following: Followings) {
        adapter.submitList(ArrayList(following.followings))
    }
    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = FollowRepository(sharedPreferences)
        presenter = FollowPresenter(this, repository, sharedPreferences, (activity as HomeActivity).navController )
    }
}