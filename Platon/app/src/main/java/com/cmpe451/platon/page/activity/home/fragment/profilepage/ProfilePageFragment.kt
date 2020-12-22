package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
import com.cmpe451.platon.databinding.AddSkillBinding
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.databinding.ResearchesCellBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
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
    ): View? {
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
        mActivityViewModel.getUserResourceResponse.observe(viewLifecycleOwner, Observer { t ->
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
                        (activity as HomeActivity).currUserId!!,
                        (activity as HomeActivity).currUserToken!!
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
        mProfilePageViewModel.getResearchesResourceResponse.observe(
            viewLifecycleOwner,
            Observer { t ->
                when (t.javaClass) {
                    Resource.Success::class.java -> {
                        maxPageNumberResearch = t.data!!.number_of_pages
                        (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).submitElements(
                            t.data!!.research_info!!
                        )
                    }
                    Resource.Error::class.java -> Toast.makeText(
                        activity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        mProfilePageViewModel.userSkills.observe(viewLifecycleOwner, Observer { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    (binding.rvProfilePageSkills.adapter as SkillsAdapter).submitElements(t.data!!.skills!!.map { it.name })
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
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
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
        }
        binding.projectsTitle.setOnClickListener{
            findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToAddResearchInfoFragment())
        }
        binding.skillsTitle.setOnClickListener{
            mProfilePageViewModel.getAllSkills()
            onAddDeleteSkillClicked()
        }

    }



    private fun uploadProfilePhoto(data: Uri?) {
        if(data != null){
            ActivityCompat.requestPermissions(
                activity as HomeActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            );

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

                mProfilePageViewModel.uploadPhoto(body, (activity as HomeActivity).currUserToken!!)
            }else{
                Toast.makeText(
                    requireContext(),
                    "Please give read permissions!",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }
        mProfilePageViewModel.getUploadPhotoResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Update profile photo is successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    mProfilePageViewModel.getUploadPhotoResourceResponse.removeObservers(
                        viewLifecycleOwner
                    )
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getUploadPhotoResourceResponse.removeObservers(
                        viewLifecycleOwner
                    )
                }
            }


        })
        

    }

    private fun onAddDeleteSkillClicked(){
        mProfilePageViewModel.allSkills.observe(viewLifecycleOwner, Observer { t ->
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
                                        (activity as HomeActivity).currUserToken!!
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
                                    (activity as HomeActivity).currUserToken!!
                                )
                            } else {
                                mProfilePageViewModel.deleteSkillFromUser(
                                    t.data!![which],
                                    (activity as HomeActivity).currUserToken!!
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
                        (activity as HomeActivity).currUserId!!,
                        (activity as HomeActivity).currUserToken!!
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
        (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).clearElements()

        mProfilePageViewModel.setCurrentResearch(mProfilePageViewModel.getResearchesResourceResponse.value?.data!!.research_info!![position])
        findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditResearchInfoFragment())
    }

}