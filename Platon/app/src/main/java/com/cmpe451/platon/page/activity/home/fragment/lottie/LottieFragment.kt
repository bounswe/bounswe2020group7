package com.cmpe451.platon.page.activity.home.fragment.lottie

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.cmpe451.platon.databinding.FragmentHomeBinding
import com.cmpe451.platon.databinding.FragmentLottieMerryCristmasBinding
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.login.LoginActivity

class LottieFragment: Fragment() {

    private lateinit var binding: FragmentLottieMerryCristmasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as HomeActivity).supportActionBar?.hide()



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLottieMerryCristmasBinding.inflate(inflater)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lottieView.addAnimatorUpdateListener { valueAnimator ->
            // Set animation progress
            var progress = (valueAnimator.animatedValue as Float * 100).toInt()
            if (progress >= 99) {
                (requireActivity() as HomeActivity).lottieEndded()
            }

        }


    }

}