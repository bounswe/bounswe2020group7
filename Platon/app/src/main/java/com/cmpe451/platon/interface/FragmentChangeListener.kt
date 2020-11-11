package com.cmpe451.platon.`interface`

import android.os.Bundle
import androidx.fragment.app.Fragment

interface FragmentChangeListener {
    fun addFragment(fragment: Fragment, bundle: Bundle? = null)
    fun destroyCurrentFragment()

}