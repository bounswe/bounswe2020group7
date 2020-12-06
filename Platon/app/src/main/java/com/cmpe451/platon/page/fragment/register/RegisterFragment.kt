package com.cmpe451.platon.page.fragment.register

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentRegisterBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.util.Definitions

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var dialog: AlertDialog
    private val mRegisterViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        setFieldListeners()
        setButtonListeners()
        setObservers()
    }

    private fun setFieldListeners(){
        // terms and conditions must be accepted, otherwise show error
        binding.termsChk.setOnCheckedChangeListener { t, b ->
            when(b){
                true -> t.error = null
                false -> t.error  = "Terms and Conditions must be accepted"
            }
        }

        binding.firstnameTv.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as TextView).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
                 }
        }

        binding.lastnameTv.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as TextView).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
            }  }

        binding.mailTv.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as TextView).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
            }  }

        binding.jobTv.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as TextView).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
            } }

        binding.pw1Tv.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as TextView).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
            }  }

        binding.pw2Tv.setOnFocusChangeListener { t, b ->
            if (!b) {
                when {
                    (t as TextView).text.isNullOrEmpty() -> t.error = "Required!"
                    t.text.toString() != binding.pw1Tv.text.toString() -> t.error =
                        "Passwords must match!"
                    else -> t.error = null
                }
            }
        }
    }

    private fun setButtonListeners(){
        dialog = Definitions().createProgressBar(activity as BaseActivity)

        binding.registerBtn.setOnClickListener {
            val firstNameStr = binding.firstnameTv.text.toString().trim()
            val lastNameStr = binding.lastnameTv.text.toString().trim()
            val mailStr = binding.mailTv.text.toString().trim()
            val pass1Str = binding.pw1Tv.text.toString().trim()
            val pass2Str = binding.pw2Tv.text.toString().trim()
            val jobStr = binding.jobTv.text.toString().trim()
            if (!mRegisterViewModel.onRegisterButtonClicked(firstNameStr, lastNameStr, mailStr, jobStr, pass1Str, pass2Str)) dialog.show()
            else Toast.makeText(activity, "Something is wrong", Toast.LENGTH_SHORT).show()
        }

        binding.alreadHaveBtn.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }

        binding.termsChk.isClickable = true

        binding.termsChk.setOnClickListener {
            val dial = AlertDialog.Builder(activity as BaseActivity)
            dial.setTitle("Terms and Conditions")
            dial.setMessage(mRegisterViewModel.getTermsAndConditions())
            dial.setPositiveButton("Accept") { _, _ ->
                binding.termsChk.isChecked = true
            }
            dial.setNegativeButton("Reject") { _, _ ->
                binding.termsChk.isChecked = false
            }
            dial.show()
        }
    }

    private fun setObservers(){
        mRegisterViewModel.getRegisterResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java->{
                    findNavController().navigateUp()
                    Toast.makeText(activity, "Successfully registered!", Toast.LENGTH_SHORT).show()
                }
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        })
    }



    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.notification_btn)?.isVisible = false
        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
    }
}

