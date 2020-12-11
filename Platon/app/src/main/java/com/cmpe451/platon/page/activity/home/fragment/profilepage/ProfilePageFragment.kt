package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.adapter.UserProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.HomeActivityViewModel
import com.cmpe451.platon.util.Definitions

class ProfilePageFragment : Fragment(), UserProjectsAdapter.UserProjectButtonClickListener, SkillsAdapter.SkillsButtonClickListener {

    private lateinit var binding: FragmentProfilePageBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()

    private val mActivityViewModel: HomeActivityViewModel by activityViewModels()

    private lateinit var userProjectsRecyclerView: RecyclerView
    private lateinit var userProjectsAdapter: UserProjectsAdapter

    private lateinit var skillsRecyclerView: RecyclerView
    private lateinit var skillsAdapter: SkillsAdapter
    private lateinit var dialog:AlertDialog
    private lateinit var addSkillDialog:Dialog

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
        userProjectsRecyclerView = binding.rvProfilePageProjects
        userProjectsAdapter = UserProjectsAdapter(ArrayList(), requireContext(), this)
        userProjectsRecyclerView.adapter = userProjectsAdapter
        userProjectsRecyclerView.layoutManager = LinearLayoutManager(this.activity)

        skillsRecyclerView = binding.rvProfilePageSkills
        skillsAdapter = SkillsAdapter(ArrayList(), requireContext(), this)
        skillsRecyclerView.adapter = skillsAdapter
        skillsRecyclerView.layoutManager = GridLayoutManager(this.activity, 3)

        dialog = Definitions().createProgressBar(requireContext())
    }


    private fun setListeners() {
        setButtonListeners()
        setObservers()
        setFields()
    }

    private fun setFields(){
        mActivityViewModel.getUserResourceResponse.observe(viewLifecycleOwner, Observer{t ->
            when(t.javaClass){
            Resource.Success::class.java ->{
                val user = t.data!!
                binding.tvEmail.text = user.e_mail
                binding.tvInstitution.text = if (user.institution != "") user.institution else  "Institution not specified!"
                binding.tvJob.text = user.job
                val naming = user.name + " " + user.surname
                binding.textNameSurname.text = naming

                mProfilePageViewModel.fetchResearch((activity as HomeActivity).token, user.id)

                if(user.rate == -1.0){
                    binding.ratingBar.visibility = View.GONE
                    binding.noRatingTv.visibility = View.VISIBLE
                }
                else {
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
        } })

    }

    private fun setObservers() {
        mProfilePageViewModel.getResearchesResourceResponse.observe(viewLifecycleOwner, Observer{ t ->
            when(t.javaClass) {
                Resource.Success::class.java -> userProjectsAdapter.submitElements(t.data!!.research_info)
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
        mProfilePageViewModel.allSkills.observe(viewLifecycleOwner, Observer{t->
            when(t.javaClass){
                Resource.Success::class.java -> {
//                    addSkillDialog = addSkillDialog(t.data.map{it})
                }
                Resource.Error::class.java -> {

                }
                Resource.Loading::class.java -> dialog.show()
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
        binding.addProjectIv.setOnClickListener{
            mProfilePageViewModel.getAllSkills()
        }

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

    override fun deleteSkillButtonClicked(skill:String, position:Int) {
        TODO("Not yet implemented")
    }

    fun addSkillDialog(skills:Array<String>): Dialog {
        return activity?.let {
            val selectedItems = ArrayList<Int>() // Where we track the selected items
            val builder = AlertDialog.Builder(it)
            // Set the dialog title
            builder.setTitle("Add your skills")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(skills, null,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(which))
                        }
                    })
                // Set the action buttons
                .setPositiveButton("Submit",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog

                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->

                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}