package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentFollowingFollowersBinding
import com.cmpe451.platon.adapter.FollowViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FollowersFollowingFragment : Fragment(){
    private lateinit var binding: FragmentFollowingFollowersBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager : ViewPager2
    private val tabNames  = arrayOf("Followers","Following")
    private var pos :Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFollowingFollowersBinding.inflate(inflater)
        tabLayout = binding.tabFollow
        arguments?.let {
            val safeArgs = FollowersFollowingFragmentArgs.fromBundle(it)
            pos = safeArgs.followerOrFollowing
        }

        viewPager = binding.vpFollow
        var adapter = FollowViewPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) {
            tab, position ->
                tab.text = tabNames[position]
                viewPager.setCurrentItem(tab.position, true)

        }.attach()
        var tab = tabLayout.getTabAt(pos)
        tab?.select()
        setHasOptionsMenu(true)
        return binding.root
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
    }

}