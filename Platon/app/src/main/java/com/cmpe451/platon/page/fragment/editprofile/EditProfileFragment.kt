package com.cmpe451.platon.page.fragment.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentEditProfileBinding
import com.cmpe451.platon.networkmodels.models.User
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.presenter.ProfilePageViewModel

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mEditProfileViewModel: EditProfileViewModel by viewModels()

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

        mEditProfileViewModel.getResponseCode.observe(viewLifecycleOwner) { t ->
            when(t){
                200->{
                    Toast.makeText(activity, "Account information has been successfully updated!", Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.fetchUser((activity as HomeActivity).token)
                    findNavController().navigateUp()
                }
                400->
                    Toast.makeText(activity, "Missing data fields or invalid data.", Toast.LENGTH_SHORT).show()
                404->
                    Toast.makeText(activity, "The user is not found.", Toast.LENGTH_SHORT).show()
                500->
                    Toast.makeText(activity, "The server is not connected to the database.", Toast.LENGTH_SHORT).show()
                else ->
                    Toast.makeText(activity, "Some error occurred!", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonEdit.setOnClickListener {
            mEditProfileViewModel.editProfile(binding.firstnameTv,
                    binding.lastnameTv, binding.jobTv, binding.privateSwitch.isChecked,
                    binding.ppTv, binding.googleScholarTv, binding.researchGateTv, (activity as HomeActivity).token)

        }
    }
    private fun setUserInfo(){
        binding.firstnameTv.setText(mProfilePageViewModel.getUser.value?.name)
        binding.lastnameTv.setText(mProfilePageViewModel.getUser.value?.surname)
        binding.jobTv.setText(mProfilePageViewModel.getUser.value?.job)
        binding.privateSwitch.isChecked = mProfilePageViewModel.getUser.value?.isPrivate ?: false
        binding.ppTv.setText(mProfilePageViewModel.getUser.value?.profile_photo)
        binding.googleScholarTv.setText(mProfilePageViewModel.getUser.value?.google_scholar_name)
        binding.researchGateTv.setText(mProfilePageViewModel.getUser.value?.researchgate_name)

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