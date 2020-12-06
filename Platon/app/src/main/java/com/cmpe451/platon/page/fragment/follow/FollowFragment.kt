package com.cmpe451.platon.page.fragment.follow

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.FollowerFollowingAdapter
import com.cmpe451.platon.databinding.FragmentFollowersFollowingListBinding
import com.cmpe451.platon.networkmodels.models.FollowPerson
import com.cmpe451.platon.networkmodels.models.OtherUser
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel

class FollowFragment:Fragment() {
    private lateinit var binding: FragmentFollowersFollowingListBinding
    private lateinit var following: ArrayList<OtherUser>
    private lateinit var adapter: FollowerFollowingAdapter
    private lateinit var rvFollowers: RecyclerView
    private var userId:Int? = null
    private lateinit var token:String
    private val mFollowViewModel: FollowViewModel by viewModels()
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    val args: FollowFragmentArgs by navArgs()
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
        userId = mProfilePageViewModel.getUser.value?.id

        setObservers()

        if(args.follow == "follower") mFollowViewModel.fetchFollowers(userId, token)
        else mFollowViewModel.fetchFollowing(userId, token)


    }

    private fun setObservers(){
        mFollowViewModel.following.observe(viewLifecycleOwner, { t->
            if(t.followings.isNotEmpty()) {
                adapter.submitList(t.followings as ArrayList<FollowPerson>)
            }
        })

        mFollowViewModel.followers.observe(viewLifecycleOwner, { i->
            if(i.followers.isNotEmpty()){
                adapter.submitList(i.followers as ArrayList<FollowPerson>)
            }
        })


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
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
    }

    private fun setAdapter(){
        rvFollowers = binding.rvFollow
        adapter = FollowerFollowingAdapter(ArrayList()) { userId:Int->
            Toast.makeText(activity, userId, Toast.LENGTH_LONG)
//            (activity as HomeActivity).navController.navigate(FollowersFollowingFragmentDirections.actionFollowersFollowingFragmentToProfilePagePrivateFragment(id))
        }
        rvFollowers.adapter = adapter
        rvFollowers.layoutManager = LinearLayoutManager(this.activity)
    }
}