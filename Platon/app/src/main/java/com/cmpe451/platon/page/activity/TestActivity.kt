package com.cmpe451.platon.page.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.stringArrayResource
import androidx.viewpager2.widget.ViewPager2
import com.cmpe451.platon.R
import com.cmpe451.platon.page.fragment.profilepage.view.adapters.FollowAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TestActivity :AppCompatActivity(){
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager : ViewPager2
    private val tabNames  = arrayOf("Following","Followers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_following_followers)
        tabLayout = findViewById(R.id.tab_follow)
        viewPager = findViewById(R.id.vp_follow)
        var adapter = FollowAdapter(this)
        viewPager.adapter = adapter
//        TabLayoutMediator(tabLayout, viewPager) {
//            tab, position ->
//                tab.text = tabNames[position]
//                viewPager.setCurrentItem(tab.position, true)
//
//        }.attach()
    }

}