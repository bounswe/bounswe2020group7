package com.cmpe451.platon.core

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.cmpe451.platon.R

// Base activity that all activities implement this
open class BaseActivity : AppCompatActivity() {

    /*
    The system first creates the activity. On activity creation, the activity enters the Created state. In the onCreate() method,
     basic application startup logic happens here once for the entire life of the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}