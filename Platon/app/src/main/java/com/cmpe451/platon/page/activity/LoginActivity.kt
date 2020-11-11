package com.cmpe451.platon.page.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.`interface`.FragmentChangeListener
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.page.fragment.PreLogin.view.PreLoginFragment

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

    override fun onBackPressed() {
        //todo
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login_btn ->             // do something
                return true
            R.id.register_btn ->             // do something
                return false
            else -> super.onContextItemSelected(item)
        }
    }


}