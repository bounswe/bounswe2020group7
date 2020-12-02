package com.cmpe451.platon.page.fragment.follow.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.adapter.FollowerFollowingRecyclerViewAdapter
import com.cmpe451.platon.databinding.FragmentFollowerListBinding
import com.cmpe451.platon.networkmodels.models.OtherUser
import com.cmpe451.platon.page.fragment.follow.presenter.FollowPresenter

class FollowerFragment: Fragment(){
    private lateinit var binding: FragmentFollowerListBinding
    private lateinit var presenter: FollowPresenter
    private lateinit var following: ArrayList<OtherUser>
    private lateinit var adapter: FollowerFollowingRecyclerViewAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowerListBinding.inflate(inflater)
        following = ArrayList()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvFollowers = binding.rvFollow
        adapter = FollowerFollowingRecyclerViewAdapter(ArrayList()) { id:Int->
            presenter.goToProfilePage(id)
//            (activity as HomeActivity).navController.navigate(FollowersFollowingFragmentDirections.actionFollowersFollowingFragmentToProfilePagePrivateFragment(id))
        }
        rvFollowers.adapter = adapter
        rvFollowers.layoutManager = LinearLayoutManager(this.activity)
        presenter.bringFollowers()
    }

}