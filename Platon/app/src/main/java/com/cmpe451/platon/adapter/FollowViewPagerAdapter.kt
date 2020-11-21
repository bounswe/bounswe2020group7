package com.cmpe451.platon.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cmpe451.platon.page.fragment.profilepage.view.FollowerListFragment
import com.cmpe451.platon.page.fragment.profilepage.view.FollowingListFragment


class FollowViewPagerAdapter(fragment:Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2;
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FollowerListFragment.newInstance()
            1 -> FollowingListFragment.newInstance()
            else -> throw NotImplementedError()
        }
    }
}