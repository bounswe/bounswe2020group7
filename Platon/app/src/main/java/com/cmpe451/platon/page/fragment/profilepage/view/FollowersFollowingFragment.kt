package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.databinding.FragmentFollowingFollowersBinding

class FollowersFollowingFragment : Fragment(){
    private lateinit var binding: FragmentFollowingFollowersBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFollowingFollowersBinding.inflate(inflater)
        return binding.root
    }

}