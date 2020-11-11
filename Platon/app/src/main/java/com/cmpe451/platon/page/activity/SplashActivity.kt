package com.cmpe451.platon.page.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        rotateLogo()
        startApp()


        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


    private fun rotateLogo(){
        val image: ImageView = findViewById(R.id.logo_img)
        val progBar: ImageView = findViewById(R.id.progressbar_img)

        val scale = AnimationUtils.loadAnimation(this, R.anim.scale)
        image.startAnimation(scale)

        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
        progBar.startAnimation(rotate)
    }

    private fun startApp(){
        val hand = Handler(Looper.myLooper()!!)

        hand.postDelayed({
            val i = Intent(this@SplashActivity, LoginActivity::class.java)
            finish()
            startActivity(i)

        }, 2000)
    }
}