package com.cmpe451.platon.page.activity.home.fragment.lottie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.databinding.FragmentLottieLogoutBinding
import com.cmpe451.platon.page.activity.home.HomeActivity

/*
 *  It consists of the UI Code, data bindings and general logic of application
 */

class LottieFragment: Fragment() {

    private lateinit var binding: FragmentLottieLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as HomeActivity).supportActionBar?.hide()
    }

    /*
     *  Creates and returns the view hierarchy associated with the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLottieLogoutBinding.inflate(inflater)

        return binding.root
    }

    /*
     *  After view creation listeners and observers implemented
     */
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