package com.cmpe451.platon.page.fragment.editprofile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentEditProfileBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.page.fragment.register.RegisterViewModel
import com.cmpe451.platon.util.Definitions

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mEditProfileViewModel: EditProfileViewModel by viewModels()
    private val mRegisterViewModel:RegisterViewModel by viewModels()

    private lateinit var dialog:AlertDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setUserInfo()
    }
    private fun setListeners() {
        mRegisterViewModel.getAllJobs()

        setButtonListeners()
        setObservers()
    }

    private fun setButtonListeners() {
        dialog = Definitions().createProgressBar(activity as BaseActivity)
        binding.buttonEdit.setOnClickListener {
            mEditProfileViewModel.editProfile(binding.firstnameTv,
                    binding.lastnameTv, binding.spJob ,binding.institutionTv, binding.privateSwitch.isChecked,
                    binding.ppTv, binding.googleScholarTv, binding.researchGateTv, (activity as HomeActivity).token)

            dialog.show()

        }
    }

    private fun setObservers(){
        mEditProfileViewModel.getEditProfileResourceResponse.observe(viewLifecycleOwner, Observer { t ->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    Toast.makeText(activity, "Account information has been successfully updated!", Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.fetchUser((activity as HomeActivity).token)
                    findNavController().navigateUp()
                }
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()

            }
            dialog.dismiss()
        })

        mRegisterViewModel.getJobListResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> {
                    val x  = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf("Loading..."))
                    x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spJob.adapter = x

                }
                Resource.Success::class.java -> {
                    val user = mProfilePageViewModel.getUserResourceResponse.value!!.data
                    (binding.spJob.adapter as ArrayAdapter<*>).clear()
                    (binding.spJob.adapter as ArrayAdapter<String>).addAll(t.data!!.map { it.name })
                    binding.spJob.setSelection(t.data!!.map { it.name }.indexOf(user?.job))
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
            }


        })

    }

    private fun setUserInfo(){
        val user = mProfilePageViewModel.getUserResourceResponse.value!!.data
        binding.firstnameTv.setText(user?.name)
        binding.lastnameTv.setText(user?.surname)
        binding.institutionTv.setText(user?.institution)
        binding.privateSwitch.isChecked = user?.is_private ?:false
        binding.ppTv.setText(user?.profile_photo)
        binding.googleScholarTv.setText(user?.google_scholar_name)
        binding.researchGateTv.setText(user?.researchgate_name)

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