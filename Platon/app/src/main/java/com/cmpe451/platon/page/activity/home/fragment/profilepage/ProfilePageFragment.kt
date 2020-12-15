package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.adapter.UserProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.AddSkillBinding
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.HomeActivityViewModel
import com.cmpe451.platon.util.Definitions

class ProfilePageFragment : Fragment(), UserProjectsAdapter.UserProjectButtonClickListener{

    private lateinit var binding: FragmentProfilePageBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mActivityViewModel: HomeActivityViewModel by activityViewModels()

    private lateinit var dialog:AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapters()
        setListeners()
    }

    private fun initializeAdapters() {
        binding.rvProfilePageProjects.adapter = UserProjectsAdapter(ArrayList(), requireContext(), this)
        binding.rvProfilePageProjects.layoutManager = LinearLayoutManager(this.activity)

        binding.rvProfilePageSkills.adapter = SkillsAdapter(ArrayList(), requireContext())
        binding.rvProfilePageSkills.layoutManager = GridLayoutManager(this.activity, 3)

        dialog = Definitions().createProgressBar(requireContext())
    }


    private fun setListeners() {
        setButtonListeners()
        setObservers()
        setFields()
    }

    private fun setFields(){
        mActivityViewModel.getUserResourceResponse.observe(viewLifecycleOwner, Observer { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val user = t.data!!
                    binding.tvEmail.text = user.e_mail
                    binding.tvInstitution.text = if (user.institution != "") user.institution else "Institution not specified!"
                    binding.tvJob.text = user.job
                    val naming = user.name + " " + user.surname
                    binding.textNameSurname.text = naming

                    mProfilePageViewModel.fetchResearch((activity as HomeActivity).token, user.id)
                    mProfilePageViewModel.getUserSkills((activity as HomeActivity).user_id!!, (activity as HomeActivity).token!!)

                    if (user.rate == -1.0) {
                        binding.ratingBar.visibility = View.GONE
                        binding.noRatingTv.visibility = View.VISIBLE
                    } else {
                        binding.ratingBar.visibility = View.VISIBLE
                        binding.noRatingTv.visibility = View.GONE
                        binding.ratingBar.rating = user.rate.toFloat()
                    }

                    Glide.with(this)
                            .load(user.profile_photo)
                            .placeholder(R.drawable.ic_o_logo)
                            .into(binding.profilePhoto)
                }
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setObservers() {
        mProfilePageViewModel.getResearchesResourceResponse.observe(viewLifecycleOwner, Observer { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).submitElements(t.data!!.research_info)
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

        mProfilePageViewModel.userSkills.observe(viewLifecycleOwner, Observer { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    (binding.rvProfilePageSkills.adapter as SkillsAdapter).submitElements(t.data!!.skills.map { it.name })
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun setButtonListeners() {
        binding.buttonFollowers.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment("follower"))
        }

        binding.buttonFollowing.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment("following"))
        }

        binding.buttonEditProfile.setOnClickListener {
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
        }
        binding.addProjectIv.setOnClickListener{
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToAddResearchInfoFragment())
        }
        binding.addSkillIv.setOnClickListener{
            onAddDeleteSkillClicked()
            mProfilePageViewModel.getAllSkills()
        }

    }

    private fun onAddDeleteSkillClicked(){
        mProfilePageViewModel.allSkills.observe(viewLifecycleOwner, Observer { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val skillNameList = mProfilePageViewModel.userSkills.value!!.data!!.skills.map { it.name }
                    val bArray = t.data!!.map { skillNameList.contains(it) }.toBooleanArray()
                    AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setNeutralButton("Add Nonexistent Skill") { _, _ ->
                                val tmpBinding = AddSkillBinding.inflate(layoutInflater, requireView().parent as ViewGroup, false)
                                AlertDialog.Builder(context).setView(tmpBinding.root)
                                        .setCancelable(true)
                                        .setPositiveButton("Add") { _, _ ->
                                            if (!tmpBinding.etNewSkill.text.isNullOrEmpty()) {
                                                mProfilePageViewModel.addSkillToUser(tmpBinding.etNewSkill.text.toString().trim(), (activity as HomeActivity).token!!)
                                            }
                                        }
                                        .create().show()
                            }
                            .setNegativeButton("Completed") { dial, _ ->
                                dial.dismiss()
                                mProfilePageViewModel.getAddDeleteSkillResourceResponse.removeObservers(viewLifecycleOwner)
                            }
                            .setMultiChoiceItems(t.data!!.toTypedArray(), bArray) { _, which, isChecked ->
                                if (isChecked) {
                                    mProfilePageViewModel.addSkillToUser(t.data!![which], (activity as HomeActivity).token!!)
                                } else {
                                    mProfilePageViewModel.deleteSkillFromUser(t.data!![which], (activity as HomeActivity).token!!)
                                }
                            }
                            .create().show()
                    dialog.dismiss()
                    mProfilePageViewModel.allSkills.removeObservers(viewLifecycleOwner)
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                Resource.Loading::class.java ->
                    dialog.show()
            }
        })



        mProfilePageViewModel.getAddDeleteSkillResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    mProfilePageViewModel.getUserSkills((activity as HomeActivity).user_id!!, (activity as HomeActivity).token!!)
                    dialog.dismiss()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
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
        menu.findItem(R.id.logout_menu_btn)?.isVisible = true
        menu.findItem(R.id.notification_btn)?.isVisible = false
    }



    override fun onUserProjectButtonClicked(binding: UserProjectsCellBinding, position: Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onUserProjectEditClicked(position: Int) {
        mProfilePageViewModel.setCurrentResearch(mProfilePageViewModel.getResearchesResourceResponse.value?.data!!.research_info[position])
        findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditResearchInfoFragment())
    }

}