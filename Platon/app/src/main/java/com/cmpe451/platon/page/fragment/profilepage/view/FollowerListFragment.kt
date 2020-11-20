package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentFollowersBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.contract.ProfilePageContract
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import com.cmpe451.platon.page.fragment.profilepage.presenter.ProfilePagePresenter
import com.cmpe451.platon.page.fragment.profilepage.view.adapters.FollowerRecyclerViewAdapter


class FollowerListFragment : Fragment(), ProfilePageContract.View{
    private lateinit var binding: FragmentFollowersBinding
    private lateinit var presenter: ProfilePagePresenter
    private lateinit var followers: ArrayList<String>

    companion object{
        fun newInstance(): FollowerListFragment{
            val args = Bundle()

            val fragment = FollowerListFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFollowersBinding.inflate(inflater)
        followers = ArrayList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePresenter()
        val rvFollowers = binding.rvFollow
        followers.addAll(presenter.getFollowers())
        rvFollowers.adapter = FollowerRecyclerViewAdapter(followers)
        rvFollowers.layoutManager = LinearLayoutManager(this.activity)

    }
    private fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = ProfilePageRepository(sharedPreferences)
        presenter = ProfilePagePresenter(this, repository, sharedPreferences, (activity as HomeActivity).navController )
    }
    override fun setPresenter(presenter: ProfilePageContract.Presenter) {
        TODO("Not yet implemented")
    }

}