package com.cmpe451.platon.page.fragment.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentSplashBinding
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragment

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rotateLogo()
        startApp()

        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun rotateLogo(){
        val scale = AnimationUtils.loadAnimation(activity, R.anim.scale)
        binding.logoImg.startAnimation(scale)

        val rotate = AnimationUtils.loadAnimation(activity, R.anim.rotate)
        binding.progressbarImg.startAnimation(rotate)
    }

    private fun startApp(){
        val hand = Handler(Looper.myLooper()!!)

        hand.postDelayed({
            (activity as LoginActivity).changeFragment(PreLoginFragment())
        }, 2000)
    }
}

