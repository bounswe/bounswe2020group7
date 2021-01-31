package com.cmpe451.platon.page.activity.login.fragment.register

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentRegisterBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.util.Definitions

/*
 *  It consists of the UI Code, data bindings and general logic of application
 */

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var dialog: AlertDialog
    private val mRegisterViewModel: RegisterViewModel by viewModels()

    /*
     *  Creates and returns the view hierarchy associated with the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    /*
     *  After view creation listeners and observers implemented
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        mRegisterViewModel.getAllJobs()
    }

    /*
     *  Add listener methods called
     */
    private fun setListeners() {
        setFieldListeners()
        setButtonListeners()
        setObservers()
    }

    /*
     *  Field listener added
     */
    private fun setFieldListeners(){
        // terms and conditions must be accepted, otherwise show error
        binding.chkTerms.setOnCheckedChangeListener { t, b ->
            when(b){
                true -> t.error = null
                false -> t.error = "Terms and Conditions must be accepted"
            }
        }

        binding.etFirstName.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as EditText).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
                 }
        }

        binding.etLastName.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as EditText).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
            }  }

        binding.etEmail.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as EditText).text.isNullOrEmpty()) t.error = "Required!"
                else if(!Patterns.EMAIL_ADDRESS.matcher(t.text.toString()).matches()) t.error = "Please enter a valid email!"
                else t.error = null
            }  }

        binding.etPw1.setOnFocusChangeListener{ t, b ->
            if(!b){
                if((t as EditText).text.isNullOrEmpty()) t.error = "Required!"
                else t.error = null
            }  }

        binding.etPw2.setOnFocusChangeListener { t, b ->
            if (!b) {
                when {
                    (t as EditText).text.isNullOrEmpty() -> t.error = "Required!"
                    t.text.toString() != binding.etPw1.text.toString() -> t.error = "Passwords must match!"
                    else -> t.error = null
                }
            }
        }
    }

    /*
     *  Button click listeners added
     */
    private fun setButtonListeners(){
        dialog = Definitions().createProgressBar(activity as BaseActivity)

        binding.btnRegister.setOnClickListener {
            val firstNameStr = binding.etFirstName.text.toString().trim()
            val lastNameStr = binding.etLastName.text.toString().trim()
            val mailStr = binding.etEmail.text.toString().trim()
            val pass1Str = binding.etPw1.text.toString().trim()
            val pass2Str = binding.etPw2.text.toString().trim()
            val jobStr = if (binding.etNewJob.text.isEmpty()) binding.spJob.selectedItem.toString().trim() else binding.etNewJob.text.toString().trim()
            val institution = if (binding.etInstitution.text.isEmpty()) null else binding.etInstitution.text.toString().trim()

            val flag = firstNameStr.isEmpty() || lastNameStr.isEmpty()
                    || mailStr.isEmpty() || jobStr.isEmpty()
                    || pass1Str.isEmpty() || pass2Str.isEmpty() || pass1Str != pass2Str
                    || !Patterns.EMAIL_ADDRESS.matcher(mailStr).matches()

            if (!flag) mRegisterViewModel.onRegisterButtonClicked(
                firstNameStr,
                lastNameStr,
                mailStr,
                jobStr,
                pass1Str,
                pass2Str,
                institution
            )
            else Toast.makeText(
                activity,
                "Please fill the necessary fields properly!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnAlreadyHave.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }

        binding.chkTerms.isClickable = true

        binding.chkTerms.setOnClickListener {
            val dial = AlertDialog.Builder(activity as BaseActivity)
            dial.setTitle("Terms and Conditions")
            dial.setMessage(mRegisterViewModel.getTermsAndConditions())
            dial.setPositiveButton("Accept") { _, _ ->
                binding.chkTerms.isChecked = true
            }
            dial.setNegativeButton("Reject") { _, _ ->
                binding.chkTerms.isChecked = false
            }
            dial.show()
        }
        binding.etNewJob.visibility = View.GONE
        binding.spJob.onItemSelectedListener = object: OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (binding.spJob.adapter.count-1 == position){
                    binding.etNewJob.visibility = View.VISIBLE
                }else{
                    binding.etNewJob.visibility = View.GONE
                    binding.etNewJob.text.clear()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    /*
     *  Observers of the view model responses handled
     */
    private fun setObservers(){
        mRegisterViewModel.getRegisterResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    findNavController().navigateUp()
                    dialog.dismiss()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })


        mRegisterViewModel.getJobListResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> {
                    val x  = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf("Loading..."))
                    x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spJob.adapter = x

                }
                Resource.Success::class.java -> {
                    (binding.spJob.adapter as ArrayAdapter<*>).clear()
                    (binding.spJob.adapter as ArrayAdapter<String>).addAll(t.data!!.map { it.name })
                    (binding.spJob.adapter as ArrayAdapter<String>).add("Not in list")
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
            }


        })
    }

    /*
     *  Visibilities of top menu bar items handled
     */
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}

