package com.cmpe451.platon.page.activity.home.fragment.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.FollowerFollowingAdapter
import com.cmpe451.platon.databinding.FragmentFollowersFollowingListBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.FollowPerson
import com.cmpe451.platon.network.models.OtherUser
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.fragment.profilepage.ProfilePageViewModel

class FollowFragment:Fragment() {
    private lateinit var binding: FragmentFollowersFollowingListBinding
    private lateinit var following: ArrayList<OtherUser>
    private lateinit var adapter: FollowerFollowingAdapter
    private lateinit var rvFollowers: RecyclerView
    private var userId:Int? = null
    private lateinit var token:String

    private val mFollowViewModel: FollowViewModel by viewModels()
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()

    private val args: FollowFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowersFollowingListBinding.inflate(inflater)
        setHasOptionsMenu(true)
        following = ArrayList()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        token = (activity as HomeActivity).token.toString()
        userId = (activity as HomeActivity).userId

        setObservers()

        if(args.userId == 0){
            if(args.follow == "follower") mFollowViewModel.fetchFollowers(userId, token)
            else mFollowViewModel.fetchFollowing(userId, token)
        }
        else  {
            if(args.follow == "follower") mFollowViewModel.fetchFollowers(args.userId, token)
            else mFollowViewModel.fetchFollowing(args.userId, token)
        }

    }

    private fun setObservers(){
        mFollowViewModel.getFollowingResource.observe(viewLifecycleOwner, Observer{ t->
            when(t.javaClass){
                Resource.Success::class.java -> adapter.submitList(t.data?.followings as ArrayList<FollowPerson>)
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

        mFollowViewModel.getFollowersResource.observe(viewLifecycleOwner, Observer{ t->
            when(t.javaClass){
                Resource.Success::class.java -> adapter.submitList(t.data?.followers as ArrayList<FollowPerson>)
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    private fun setAdapter(){
        rvFollowers = binding.rvFollow

        adapter = FollowerFollowingAdapter(ArrayList()) { userId:Int->
//            Toast.makeText(activity, userId, Toast.LENGTH_LONG)
//            (activity as HomeActivity).navController.navigate(FollowersFollowingFragmentDirections.actionFollowersFollowingFragmentToProfilePagePrivateFragment(id))
            if(userId == (activity as HomeActivity).userId){
                findNavController().navigate(FollowFragmentDirections.actionFollowFragmentToProfilePageFragment())
            }
            else {
                findNavController().navigate(FollowFragmentDirections.actionFollowFragmentToOtherProfileFragment(userId))
            }

        }
        rvFollowers.adapter = adapter
        rvFollowers.layoutManager = LinearLayoutManager(this.activity)
    }
}