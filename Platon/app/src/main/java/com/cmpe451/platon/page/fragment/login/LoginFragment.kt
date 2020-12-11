package com.cmpe451.platon.page.fragment.login

/**
 * @author Burak Ömür
 */

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentLoginBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.util.Definitions

/**
 * Login Fragment class of application
 * If user tries to login to app, it will be viewing this fragment
 */
class LoginFragment : Fragment() {

    //definitions
    private val mLoginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var dialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        sharedPreferences = activity?.getSharedPreferences("token_file", Context.MODE_PRIVATE)!!
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun doAutoLogin(){
        val mail = sharedPreferences.getString("mail", null)
        val pass = sharedPreferences.getString("pass", null)
        if(mail != null && pass != null){
            binding.emailEt.setText(mail)
            binding.passEt.setText(pass)
            binding.rememberChk.isChecked = true
            binding.loginBtn.performClick()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        doAutoLogin()
    }


    private fun setListeners() {
        setFieldListeners()
        setButtonListeners()
        setObservers()
    }

    private fun setFieldListeners(){
        binding.emailEt.setOnFocusChangeListener {v,b ->
            if(!b){
                if((v as TextView).text.isNullOrEmpty()) v.error = "Required!"
                else v.error = null
            }
        }

        binding.passEt.setOnFocusChangeListener {v,b ->
            if(!b){
                if((v as TextView).text.isNullOrEmpty()) v.error = "Required!"
                else v.error = null
            }
        }
    }

    private fun setButtonListeners(){
        dialog =Definitions().createProgressBar(activity as BaseActivity)

        // login button listener
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            mLoginViewModel.tryToLogin(email, pass)
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
    }

    private fun setObservers(){
        mLoginViewModel.getLoginResourceResponse.observe(viewLifecycleOwner, Observer{t->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()

                Resource.Success::class.java -> {
                    val token = t.data!!.token
                    sharedPreferences.edit().putString("token", token).apply()

                    if(binding.rememberChk.isChecked){
                        sharedPreferences.edit().putString("mail", binding.emailEt.text.toString().trim()).apply()
                        sharedPreferences.edit().putString("pass", binding.passEt.text.toString().trim()).apply()
                    }
                    activity?.startActivity(Intent(activity, HomeActivity::class.java).putExtra("token", token))
                    activity?.finish()
                    dialog.dismiss()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                    sharedPreferences.edit().remove("mail").apply()
                    sharedPreferences.edit().remove("pass").apply()
                    sharedPreferences.edit().remove("token").apply()
                    dialog.dismiss()
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