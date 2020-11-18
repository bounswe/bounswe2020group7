package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import  com.cmpe451.platon.databinding.FragmentFollowersBinding


class FollowerListFragment : Fragment(){
    private lateinit var binding: FragmentFollowersBinding
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
        return binding.root
    }

}