package com.cmpe451.platon.page.activity.home.fragment.follow

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.FollowerFollowingAdapter
import com.cmpe451.platon.adapter.RecommendedFollowAdapter
import com.cmpe451.platon.adapter.WorkspaceRecommendationAdapter
import com.cmpe451.platon.databinding.DialogRecommendedBinding
import com.cmpe451.platon.databinding.FragmentFollowersFollowingListBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.FollowPerson
import com.cmpe451.platon.network.models.OtherUser
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions

class FollowFragment:Fragment() {
    private lateinit var binding: FragmentFollowersFollowingListBinding
    private lateinit var following: ArrayList<OtherUser>
    private lateinit var adapter: FollowerFollowingAdapter
    private lateinit var rvFollowers: RecyclerView
    private var userId:Int? = null
    private lateinit var token:String

    private val mFollowViewModel: FollowViewModel by viewModels()
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()

    private lateinit var paginationListener:PaginationListener
    private lateinit var dialog:AlertDialog

    private var maxNumOfPages:Int=0
    private val pageSize:Int = 10

    private val args: FollowFragmentArgs by navArgs()
    private lateinit var followRecommendedBinding:DialogRecommendedBinding
    private lateinit var recDialog:AlertDialog

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.follow_recommendation_btn->{
                onFollowRecommendationsClicked()
            }
        }
        return item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
    }

    private fun onFollowRecommendationsClicked() {
        mFollowViewModel.getFollowRecommendations(20, (activity as HomeActivity).currUserToken)
        mFollowViewModel.getFollowRecommendationsResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mFollowViewModel.getFollowRecommendationsResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
                Resource.Success::class.java -> {
                    if (t.data!!.recommendation_list.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No new recommendations found",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        followRecommendedBinding = DialogRecommendedBinding.inflate(
                            layoutInflater,
                            requireView().parent as ViewGroup,
                            false
                        )
                        recDialog =
                            AlertDialog.Builder(context).setView(followRecommendedBinding.root)
                                .setCancelable(true).create()
                        recDialog.show()
                        followRecommendedBinding.recommendedRv.adapter =
                            RecommendedFollowAdapter(ArrayList(),
                                requireContext()){ userId:Int->
                                if(userId == (activity as HomeActivity).currUserId){
                                    findNavController().navigate(FollowFragmentDirections.actionFollowFragmentToProfilePageFragment())
                                }
                                else {
                                    findNavController().navigate(FollowFragmentDirections.actionFollowFragmentToOtherProfileFragment(userId))
                                }
                                recDialog.dismiss()

                            }
                        followRecommendedBinding.recommendedRv.layoutManager =
                            LinearLayoutManager(requireContext())
                        (followRecommendedBinding.recommendedRv.adapter as RecommendedFollowAdapter).submitList(
                            t.data!!.recommendation_list as ArrayList
                        )
                        followRecommendedBinding.recommendedTitleTv.setOnClickListener {
                            recDialog.dismiss()
                        }
                    }

                    mFollowViewModel.getFollowRecommendationsResponse.value = Resource.Done()

                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowersFollowingListBinding.inflate(inflater)
        setHasOptionsMenu(true)
        following = ArrayList()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        token = (activity as HomeActivity).currUserToken.toString()
        userId = (activity as HomeActivity).currUserId

        setObservers()

        setData(0,pageSize)

    }

    private fun setData(page:Int, per_page:Int) {
        if(args.userId == 0){
            if(args.follow == "follower") mFollowViewModel.fetchFollowers(userId, token, page, per_page)
            else mFollowViewModel.fetchFollowing(userId, token,page,per_page)
        }
        else  {
            if(args.follow == "follower") mFollowViewModel.fetchFollowers(args.userId, token, page,per_page)
            else mFollowViewModel.fetchFollowing(args.userId, token,page,per_page)
        }
    }

    private fun setObservers(){
        mFollowViewModel.getFollowingResource.observe(viewLifecycleOwner, { t->
            when(t.javaClass){

                Resource.Success::class.java -> {
                    maxNumOfPages = t.data!!.number_of_pages
                    adapter.submitList(t.data?.followings as ArrayList<FollowPerson>)
                    paginationListener.isLoading = false
                    mFollowViewModel.getFollowingResource.value = Resource.Done()
                }
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })

        mFollowViewModel.getFollowersResource.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java -> {
                    maxNumOfPages = t.data!!.number_of_pages
                    adapter.submitList(t.data?.followers as ArrayList<FollowPerson>)
                    paginationListener.isLoading = false
                    mFollowViewModel.getFollowersResource.value = Resource.Done()
                }
                Resource.Error::class.java -> Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
        menu.findItem(R.id.follow_recommendation_btn)?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    private fun setAdapter(){
        dialog = Definitions().createProgressBar(requireContext())
        rvFollowers = binding.rvFollow

        adapter = FollowerFollowingAdapter(ArrayList(), requireContext()) { userId:Int->
            if(userId == (activity as HomeActivity).currUserId){
                findNavController().navigate(FollowFragmentDirections.actionFollowFragmentToProfilePageFragment())
            }
            else {
                findNavController().navigate(FollowFragmentDirections.actionFollowFragmentToOtherProfileFragment(userId))
            }

        }
        rvFollowers.adapter = adapter
        val layoutManager = LinearLayoutManager(this.activity)
        rvFollowers.layoutManager = layoutManager

        paginationListener = object: PaginationListener(layoutManager, pageSize){
            override fun loadMoreItems() {
                if(maxNumOfPages-1 > currentPage){
                    isLoading = true
                    currentPage++
                    setData(currentPage, PAGE_SIZE)
                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        rvFollowers.addOnScrollListener(paginationListener)
    }
}