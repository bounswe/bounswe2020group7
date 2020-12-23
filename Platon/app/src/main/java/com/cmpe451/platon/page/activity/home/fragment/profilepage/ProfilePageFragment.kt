package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.compose.navArgument
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.MediaStoreSignature
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.adapter.UserProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Job
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.HomeActivityViewModel
import com.cmpe451.platon.util.Definitions
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfilePageFragment : Fragment(), UserProjectsAdapter.UserProjectButtonClickListener{

    private lateinit var binding: FragmentProfilePageBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mActivityViewModel: HomeActivityViewModel by activityViewModels()

    private lateinit var dialog:AlertDialog
    private var maxPageNumberResearch:Int=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        val height = resources.displayMetrics.heightPixels

        val layoutManager = LinearLayoutManager(this.activity)

        binding.rvProfilePageProjects.adapter = UserProjectsAdapter(
            ArrayList(),
            requireContext(),
            this
        )
        binding.rvProfilePageProjects.layoutManager = layoutManager

        binding.rvProfilePageProjects.layoutParams =
                LinearLayout.LayoutParams(binding.rvProfilePageProjects.layoutParams.width, height / 3)

        binding.rvProfilePageProjects.addOnScrollListener(object :
            PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                if (maxPageNumberResearch - 1 > currentPage) {
                    currentPage++
                    mProfilePageViewModel.fetchResearch(
                        (activity as HomeActivity).currUserToken,
                        (activity as HomeActivity).currUserId,
                        currentPage,
                        5
                    )
                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        })

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
        mActivityViewModel.getUserResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val user = t.data!!
                    binding.tvEmail.text = user.e_mail
                    binding.tvInstitution.text =
                        if (user.institution != "") user.institution else "Institution not specified!"
                    binding.tvJob.text = user.job
                    val naming = user.name + " " + user.surname
                    binding.textNameSurname.text = naming

                    (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).clearElements()


                    mProfilePageViewModel.fetchResearch(
                        (activity as HomeActivity).currUserToken,
                        user.id,
                        0,
                        5
                    )
                    mProfilePageViewModel.getUserSkills(
                        (activity as HomeActivity).currUserId,
                        (activity as HomeActivity).currUserToken
                    )

                    binding.ratingBar.rating = user.rate.toFloat()

                    val df = SimpleDateFormat("hmsS", Locale.getDefault())
                    val formattedDate =  df.format(Date()).toLong()

                    Glide.with(this)
                        .load(Definitions.API_URL + "api" + user.profile_photo)
                        .placeholder(R.drawable.ic_o_logo)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                        .signature(
                            MediaStoreSignature(
                                "image/png",
                                formattedDate,
                                0
                            )
                        )
                        .into(binding.profilePhoto)
                }
                Resource.Error::class.java -> Toast.makeText(
                    activity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun setObservers() {
        mProfilePageViewModel.getUploadPhotoResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Update profile photo is successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    mProfilePageViewModel.getUploadPhotoResourceResponse.value = Resource.Done()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getUploadPhotoResourceResponse.value = Resource.Done()
                }
            }
        })

        mProfilePageViewModel.getResearchesResourceResponse.observe(viewLifecycleOwner, { t ->
                when (t.javaClass) {
                    Resource.Success::class.java -> {
                        maxPageNumberResearch = t.data!!.number_of_pages
                        (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).submitElements(
                            t.data!!.research_info!!
                        )
                    }
                    Resource.Error::class.java -> {
                        Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        mProfilePageViewModel.userSkills.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    (binding.rvProfilePageSkills.adapter as SkillsAdapter).submitElements(t.data!!.skills!!.map { it.name })
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })


        mProfilePageViewModel.getEditProfileResourceResponse.observe(viewLifecycleOwner, { t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                    mProfilePageViewModel.getEditProfileResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getEditProfileResourceResponse.value = Resource.Done()

                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }

            }
        })

        mProfilePageViewModel.getAddResearchResourceResponse.observe(viewLifecycleOwner, { t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    Toast.makeText(activity, "Research Information is added!", Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getAddResearchResourceResponse.value  = Resource.Done()
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getAddResearchResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java->dialog.dismiss()
            }
        })

    mProfilePageViewModel.getDeleteResearchResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    mProfilePageViewModel.getDeleteResearchResourceResponse.value = Resource.Done()
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                }
                Resource.Error::class.java ->{
                    mProfilePageViewModel.getDeleteResearchResourceResponse.value = Resource.Done()
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Done::class.java->dialog.dismiss()
            }

        })


    mProfilePageViewModel.getEditResearchResourceResponse.observe(viewLifecycleOwner, {t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    mProfilePageViewModel.getEditResearchResourceResponse.value = Resource.Done()
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getEditResearchResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java->dialog.dismiss()
            }
        })
    }


    private fun setButtonListeners() {
        //Instead of onActivityResult() method use this one
        val someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode == Activity.RESULT_OK && it.data != null && it.data!!.data != null) {
                Glide.with(this)
                    .load(it.data!!.data)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .circleCrop()
                    .into(binding.profilePhoto)
                uploadProfilePhoto(it.data!!.data)
            }
        }

        binding.profilePhoto.setOnClickListener{
            ActivityCompat.requestPermissions(
                activity as HomeActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )

            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"

            someActivityResultLauncher.launch(photoPickerIntent)
        }

        binding.buttonFollowers.setOnClickListener {
            findNavController().navigate(
                ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment(
                    "follower"
                )
            )
        }

        binding.buttonFollowing.setOnClickListener {
            findNavController().navigate(
                ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment(
                    "following"
                )
            )
        }

        binding.infoTitle.setOnClickListener {
            mActivityViewModel.getAllJobs()
            val editBinding = DialogEditProfileBinding.inflate(layoutInflater, binding.root, false)
            val editDialog = AlertDialog.Builder(requireContext())
                .setView(editBinding.root)
                .show()
            editDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            editBinding.buttonEdit.setOnClickListener {
                val jobStr = if (editBinding.etNewJob.text.isEmpty()) editBinding.spJob.selectedItem.toString().trim() else editBinding.etNewJob.text.toString().trim()
                val institution = if (editBinding.etInstitution.text.isEmpty()) "" else editBinding.etInstitution.text.toString().trim()
                editDialog.dismiss()
                mProfilePageViewModel.editProfile(editBinding.firstnameTv,
                    editBinding.lastnameTv, jobStr ,institution, editBinding.privateSwitch.isChecked, null, null, (activity as HomeActivity).currUserToken)
            }

            val user = mActivityViewModel.getUserResourceResponse.value?.data

            if (user != null){
                editBinding.firstnameTv.setText(user.name)
                editBinding.lastnameTv.setText(user.surname)
                editBinding.etInstitution.setText(user.institution)
                editBinding.privateSwitch.isChecked = user.is_private
            }

            mActivityViewModel.getJobListResourceResponse.observe(viewLifecycleOwner,{ t ->
                when (t.javaClass) {
                    Resource.Loading::class.java -> {
                        val x  = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf("Loading..."))
                        x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        editBinding.spJob.adapter = x
                    }
                    Resource.Success::class.java -> {
                        (editBinding.spJob.adapter as ArrayAdapter<String>).clear()
                        (editBinding.spJob.adapter as ArrayAdapter<String>).addAll(t.data!!.map { it.name })
                        (editBinding.spJob.adapter as ArrayAdapter<String>).add("Not in list")
                        editBinding.spJob.setSelection(t.data!!.map { it.name }.indexOf(user?.job))
                    }
                    Resource.Error::class.java -> {
                        Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                        mActivityViewModel.getJobListResourceResponse.value = Resource.Done()
                    }
                }
            } )


            editBinding.spJob.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (editBinding.spJob.adapter.count-1 == position){
                        editBinding.etNewJob.visibility = View.VISIBLE
                    }else{
                        editBinding.etNewJob.visibility = View.GONE
                        editBinding.etNewJob.text.clear()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            //findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
        }
        binding.projectsTitle.setOnClickListener{
            val addBinding = DialogResearchInfoAddBinding.inflate(layoutInflater, binding.root, false)
            val addDialog = AlertDialog.Builder(requireContext())
                .setView(addBinding.root)
                .show()
            addDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            addBinding.buttonProjectAdd.setOnClickListener {
                    // check if the title and year is empty
                    if(addBinding.projectNameTv.text.isNullOrEmpty() && addBinding.projectYearTv.text.isNullOrEmpty()){
                        Toast.makeText(activity as HomeActivity, "Title and Year cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    else {
                        when {
                            addBinding.projectNameTv.text.isNullOrEmpty() -> {
                                Toast.makeText(activity, "Title cannot be left empty", Toast.LENGTH_LONG).show()
                            }
                            addBinding.projectYearTv.text.isNullOrEmpty() -> {
                                Toast.makeText(activity , "Year cannot be left empty", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                var description:String? = null
                                if(!addBinding.projectDescriptionTv.text.isNullOrEmpty()){
                                    description = addBinding.projectDescriptionTv.text.toString()
                                }
                                mProfilePageViewModel.addResearchInfo(addBinding.projectNameTv.text.toString(), description,
                                    addBinding.projectYearTv.text.toString().toInt(),
                                    (activity as HomeActivity).currUserToken
                                )
                                addDialog.dismiss()
                            }
                        }
                    }
            }


            addBinding.tvLinkScholar.setOnClickListener {
                addBinding.tvLinkScholar.setTextColor(resources.getColor(R.color.primary_light, requireActivity().theme))
                addBinding.tvLinkResearchGate.setTextColor(resources.getColor(R.color.secondary_purple, requireActivity().theme))
                addBinding.projectNameTv.visibility  =View.GONE
                addBinding.projectDescriptionTv.visibility  =View.GONE
                addBinding.projectYearTv.visibility  =View.GONE
                addBinding.layLinkAccount.visibility = View.VISIBLE
                addBinding.buttonProjectAdd.text = getString(R.string.link_str)
                addBinding.buttonProjectAdd.setOnClickListener{
                    if(addBinding.cbAcceptLink.isChecked){
                        mProfilePageViewModel.editProfile(null, null, null, null, null,
                            addBinding.etLinkUrl, null,
                            (activity as HomeActivity).currUserToken)
                        addDialog.dismiss()
                    }else{
                        Toast.makeText(requireContext(), "You must accept information sharing!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            addBinding.tvLinkResearchGate.setOnClickListener{
                addBinding.tvLinkResearchGate.setTextColor(resources.getColor(R.color.primary_light, requireActivity().theme))
                addBinding.tvLinkScholar.setTextColor(resources.getColor(R.color.secondary_purple, requireActivity().theme))
                addBinding.projectNameTv.visibility  =View.GONE
                addBinding.projectDescriptionTv.visibility  =View.GONE
                addBinding.projectYearTv.visibility  =View.GONE
                addBinding.layLinkAccount.visibility = View.VISIBLE
                addBinding.buttonProjectAdd.text = getString(R.string.link_str)
                addBinding.buttonProjectAdd.setOnClickListener{
                    if(addBinding.cbAcceptLink.isChecked){
                        mProfilePageViewModel.editProfile(null, null, null, null, null,
                            null,addBinding.etLinkUrl,
                            (activity as HomeActivity).currUserToken)
                        addDialog.dismiss()
                    }else{
                        Toast.makeText(requireContext(), "You must accept information sharing!", Toast.LENGTH_SHORT).show()
                    }


                }
            }

        }
        binding.skillsTitle.setOnClickListener{
            mProfilePageViewModel.getAllSkills()
            onAddDeleteSkillClicked()
        }

    }



    private fun uploadProfilePhoto(data: Uri?) {
        if(data != null){
            if((activity as HomeActivity).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                val file = File(Definitions().getRealPathFromUri(requireContext(), data))
                val fBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    file
                )

                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("profile_photo", file.name, fBody)
                    .build()

                mProfilePageViewModel.uploadPhoto(body, (activity as HomeActivity).currUserToken)
            }else{
                Toast.makeText(
                    requireContext(),
                    "Please give read permissions!",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        

    }

    private fun onAddDeleteSkillClicked(){
        mProfilePageViewModel.allSkills.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val skillNameList =
                        (binding.rvProfilePageSkills.adapter as SkillsAdapter).getAllElements()
                    val bArray = t.data!!.map { skillNameList.contains(it) }.toBooleanArray()
                    AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setNeutralButton("Add Nonexistent Skill") { _, _ ->
                            val tmpBinding = AddSkillBinding.inflate(
                                layoutInflater,
                                requireView().parent as ViewGroup,
                                false
                            )
                            AlertDialog.Builder(context).setView(tmpBinding.root)
                                .setCancelable(false)
                                .setNegativeButton("Completed") { _, _ ->
                                    mProfilePageViewModel.getAddDeleteSkillResourceResponse.removeObservers(
                                        viewLifecycleOwner
                                    )
                                }
                                .create().show()
                            tmpBinding.btnAddSkill.setOnClickListener {
                                if (!tmpBinding.etNewSkill.text.isNullOrEmpty()) {
                                    mProfilePageViewModel.addSkillToUser(
                                        tmpBinding.etNewSkill.text.toString().trim(),
                                        (activity as HomeActivity).currUserToken
                                    )
                                }
                            }
                        }
                        .setNegativeButton("Completed") { _, _ ->
                            mProfilePageViewModel.getAddDeleteSkillResourceResponse.removeObservers(
                                viewLifecycleOwner
                            )
                        }
                        .setMultiChoiceItems(
                            t.data!!.toTypedArray(),
                            bArray
                        ) { _, which, isChecked ->
                            if (isChecked) {
                                mProfilePageViewModel.addSkillToUser(
                                    t.data!![which],
                                    (activity as HomeActivity).currUserToken
                                )
                            } else {
                                mProfilePageViewModel.deleteSkillFromUser(
                                    t.data!![which],
                                    (activity as HomeActivity).currUserToken
                                )
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
                    mProfilePageViewModel.getUserSkills(
                        (activity as HomeActivity).currUserId,
                        (activity as HomeActivity).currUserToken
                    )
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
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }



    override fun onUserProjectButtonClicked(binding: ResearchesCellBinding, position: Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onUserProjectEditClicked(position: Int) {
        val editBinding = DialogResearchInfoEditBinding.inflate(layoutInflater, binding.root, false)

        val editDialog = AlertDialog.Builder(requireContext())
            .setView(editBinding.root)
            .show()
        editDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val currResearch = (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).getElement(position)


        editBinding.projectNameTv.setText(currResearch.title)
        editBinding.projectYearTv.setText(currResearch.year.toString())
        if(currResearch.description.isNotEmpty()){
            editBinding.projectDescriptionTv.setText(currResearch.description)
        }

        editBinding.buttonEdit.setOnClickListener {
            // check if the title and year is empty
            if(editBinding.projectNameTv.text.isNullOrEmpty() && editBinding.projectYearTv.text.isNullOrEmpty()){
                Toast.makeText(activity as HomeActivity, "Title and Year cannot be left empty", Toast.LENGTH_LONG).show()
            }
            else {
                when {
                    editBinding.projectNameTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity, "Title cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    editBinding.projectYearTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Year cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        var description:String? = null
                        if(!editBinding.projectDescriptionTv.text.isNullOrEmpty()){
                            description = editBinding.projectDescriptionTv.text.toString()
                        }
                        mProfilePageViewModel.editResearchInfo(currResearch.id,
                            editBinding.projectNameTv.text.toString(), description,
                            editBinding.projectYearTv.text.toString().toInt(),
                            (activity as HomeActivity).currUserToken
                        )
                        editDialog.dismiss()

                    }
                }
            }
        }

        editBinding.buttonDelete.setOnClickListener{
            mProfilePageViewModel.deleteResearchInfo(currResearch.id,
                (activity as HomeActivity).currUserToken)
            editDialog.dismiss()
        }

    }

}