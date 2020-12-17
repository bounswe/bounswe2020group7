package com.cmpe451.platon.page.activity.login.fragment.forgotpass

/**
 * @author Burak Ömür
 */

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentForgotPasswordBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.util.Definitions

/**
 * Forgot Password's fragment class
 * If user forgets his/her password, s/he will be viewing this fragment class.
 */
class ForgotPasswordFragment : Fragment() {

    // define
    private lateinit var binding: FragmentForgotPasswordBinding
    private val mForgotPasswordViewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var dialog:AlertDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater)
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

    private fun setObservers(){

        mForgotPasswordViewModel.getSendResetMailResourceResponse.observe(viewLifecycleOwner, { i->
            when(i.javaClass){
                Resource.Success::class.java -> {
                    Toast.makeText(activity, "Keycode sent to mail!", Toast.LENGTH_SHORT).show()
                    binding.newPass2Et.visibility = View.VISIBLE
                    binding.newPass1Et.visibility = View.VISIBLE
                    binding.emailEt.visibility = View.GONE
                    binding.tokenEt.visibility = View.VISIBLE
                    binding.forgotPassBtn.visibility = View.GONE
                    binding.resetPassBtn.visibility = View.VISIBLE
                    }
                Resource.Error::class.java -> Toast.makeText(activity, i.message, Toast.LENGTH_SHORT).show()
                }

            dialog.dismiss()
        })

        mForgotPasswordViewModel.getResetResourceResponse.observe(viewLifecycleOwner, { i->
            when(i.javaClass){
                Resource.Success::class.java -> {
                    Toast.makeText(activity, "Successfully reset!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                Resource.Error::class.java -> Toast.makeText(activity, i.message, Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        })
    }

    private fun setFieldListeners(){
        binding.emailEt.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                when{
                    (v as EditText).text.isEmpty() -> v.error = "Required!"
                    else -> v.error = null
                }
            }
        }

        binding.newPass1Et.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                when{
                    (v as EditText).text.isEmpty() -> v.error = "Required!"
                    else -> v.error = null
                }
            }
        }

        binding.newPass2Et.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                when{
                    (v as EditText).text.isEmpty() -> v.error = "Required!"
                    v.text.toString() != binding.newPass1Et.text.toString() -> v.error =  "Passwords must match!"
                    else -> v.error = null
                }
            }
        }

        binding.tokenEt.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                when{
                    (v as EditText).text.isEmpty() -> v.error = "Required!"
                    else -> v.error = null
                }
            }
        }
    }

    private fun setButtonListeners(){
        dialog = Definitions().createProgressBar(activity as BaseActivity)

        binding.forgotPassBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            if(!mForgotPasswordViewModel.sendResetMail(email)) dialog.show()
            else Toast.makeText(activity, "Something is wrong", Toast.LENGTH_SHORT).show()
        }

        binding.resetPassBtn.setOnClickListener {
            val pass1 = binding.newPass1Et.text.toString().trim()
            val pass2 = binding.newPass2Et.text.toString().trim()
            val keycode = binding.tokenEt.text.toString().trim()
            if (!mForgotPasswordViewModel.resetPassword(pass1, pass2, keycode)) dialog.show()
            else Toast.makeText(activity, "Something is wrong", Toast.LENGTH_SHORT).show()
        }
    }




    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // clear search bar, and make it iconified
        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true
        menu.findItem(R.id.notification_btn)?.isVisible = false
        // hide all elements in the menu
        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.add_workspace_btn)?.isVisible = false

    }
}