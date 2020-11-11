package com.example.platon.page.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.platon.R
import com.example.platon.`interface`.FragmentChangeListener
import com.example.platon.core.BaseActivity
import com.example.platon.page.fragment.PreLogin.view.PreLoginFragment

class LoginActivity :BaseActivity(), FragmentChangeListener {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.beginTransaction().replace(R.id.login_activity_container, PreLoginFragment()).commit()


    }

    override fun addFragment(fragment: Fragment, bundle: Bundle?) {
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().addToBackStack(fragment.javaClass.name)
                .add(R.id.login_activity_container, fragment).commit()

    }

    override fun destroyCurrentFragment() {
        TODO("Not yet implemented")
    }




}