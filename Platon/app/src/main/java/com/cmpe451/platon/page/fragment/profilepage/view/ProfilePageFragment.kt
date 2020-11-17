package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R

class ProfilePageFragment : Fragment() {
    private lateinit var binding: FragmentProfilePageBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)
        return binding.root
//        return inflater.inflate(R.layout.fragment_profile_page_info,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}