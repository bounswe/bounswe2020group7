package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cmpe451.platon.databinding.FragmentFollowingFollowersBinding
import com.cmpe451.platon.page.fragment.profilepage.view.adapters.FollowViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.FieldPosition
import kotlin.properties.Delegates

class FollowersFollowingFragment : Fragment(){
    private lateinit var binding: FragmentFollowingFollowersBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager : ViewPager2
    private val tabNames  = arrayOf("Following","Followers")
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
        return binding.root
    }

}