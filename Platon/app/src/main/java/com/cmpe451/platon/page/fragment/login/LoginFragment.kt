package com.cmpe451.platon.page.fragment.login

/**
 * @author Burak Ömür
 */

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentLoginBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.util.Definitions

/**
 * Login Fragment class of application
 * If user tries to login to app, it will be viewing this fragment
 */
class LoginFragment : Fragment() {

    //definitions
    private val mLoginViewModel: LoginViewModel by activityViewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // say it has its own menu
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        sharedPreferences = activity?.getSharedPreferences("token_file", Context.MODE_PRIVATE)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }


    private fun setListeners() {
        // login button listener
        binding.loginBtn.setOnClickListener {
            // call presenter
            binding.loginBtn.isEnabled = false
            mLoginViewModel.tryToLogin(binding.emailEt, binding.passEt)
        }

        binding.dontHaveAccBtn.setOnClickListener {
            Definitions().vibrate(50, activity as Activity)
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.forgotPwBtn.setOnClickListener {
            Definitions().vibrate(50, activity as Activity)
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }


        mLoginViewModel.getToken.observe(viewLifecycleOwner, { t->
            if(t!= null && t.isNotEmpty()){
                sharedPreferences.edit().putString("token", t).apply()
                activity?.finish()
                activity?.startActivity(Intent(activity, HomeActivity::class.java).putExtra("token", t))
            }
        })

        mLoginViewModel.getResponseCode.observe(viewLifecycleOwner, {t->
            binding.loginBtn.isEnabled = true
            if(t!=null && t != 200){
                Log.i("Code is", t.toString())
                when (t){
                    400->{
                        Toast.makeText(activity, "Input Format Error", Toast.LENGTH_LONG).show()
                    }
                    401->{
                        Toast.makeText(activity, "Account Problems", Toast.LENGTH_LONG).show()
                    }
                    404->{
                        Toast.makeText(activity, "E-mail not found", Toast.LENGTH_LONG).show()
                    }
                    500->{
                        Toast.makeText(activity, "Database Connection/E-mail Server Error", Toast.LENGTH_LONG).show()
                    }
                    501->{
                        Toast.makeText(activity, "Please, Try Again!", Toast.LENGTH_LONG).show()
                    }
                    else ->{
                        Toast.makeText(activity, "Try Again!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // clear search bar, and make it icon
        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
    }


}