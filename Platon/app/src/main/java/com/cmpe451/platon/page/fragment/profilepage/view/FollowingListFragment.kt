package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentFollowingBinding

class FollowingListFragment : Fragment(){

    private lateinit var binding:FragmentFollowingBinding
    companion object{
        fun newInstance(): FollowingListFragment{
            val args = Bundle()
            val fragment = FollowingListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowingBinding.inflate(inflater)
        return binding.root
    }
}
