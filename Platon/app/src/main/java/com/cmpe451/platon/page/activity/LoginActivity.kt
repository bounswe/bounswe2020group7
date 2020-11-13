package com.cmpe451.platon.page.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.fragment.login.view.LoginFragment
import com.cmpe451.platon.page.fragment.preLogin.view.PreLoginFragment
import com.cmpe451.platon.page.fragment.register.view.RegisterFragment
import com.cmpe451.platon.page.fragment.splash.SplashFragment

class LoginActivity :BaseActivity(), FragmentChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        changeFragment(SplashFragment())
    }

    override fun addFragment(fragment: Fragment, bundle: Bundle?) {
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().addToBackStack(fragment.javaClass.name)
                .add(R.id.login_activity_container, fragment).commit()

    }


    fun changeFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.login_activity_container, fragment).commit()
    }

    override fun destroyCurrentFragment() {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login_menu_btn ->             // do something
                addFragment(LoginFragment()) == Unit
            R.id.register_menu_btn ->             // do something
                addFragment(RegisterFragment()) == Unit
            android.R.id.home ->
                supportFragmentManager.popBackStack() == Unit
            else -> super.onContextItemSelected(item)
        }
    }


}