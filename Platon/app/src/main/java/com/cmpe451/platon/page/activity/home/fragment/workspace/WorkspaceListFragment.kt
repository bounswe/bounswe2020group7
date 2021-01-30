package com.cmpe451.platon.page.activity.home.fragment.workspace

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.RecommendedCollaboratorsAdapter
import com.cmpe451.platon.adapter.WorkspaceListAdapter
import com.cmpe451.platon.adapter.WorkspaceRecommendationAdapter
import com.cmpe451.platon.databinding.DialogRecommendedBinding
import com.cmpe451.platon.databinding.FragmentWorkspaceListBinding
import com.cmpe451.platon.databinding.WorkspaceCellBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.RecommendedWorkspace
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.page.activity.home.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions

/*
 *  It consists of the UI Code, data bindings and general logic of application
 */

class WorkspaceListFragment : Fragment(), WorkspaceListAdapter.WorkspaceListButtonClickListener, WorkspaceRecommendationAdapter.WorkspaceRecommendationButtonClickListener{

    private lateinit var binding: FragmentWorkspaceListBinding
    private val mWorkspaceListViewModel: WorkspaceListViewModel by viewModels()
    private val mProfilePageViewModel : ProfilePageViewModel by activityViewModels()
    private lateinit var dialog: AlertDialog

    private lateinit var workspaceListAdapter: WorkspaceListAdapter
    private lateinit var wsRecommendedBinding:DialogRecommendedBinding
    private lateinit var recDialog:AlertDialog

    /*
    *  Click on top menu button item handled
    */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.workspace_recommendation_btn->{
                onWorkspaceRecommendationsClicked()
            }
        }
        return item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
    }

    /*
    *  Click on workspace recommendation button handled
    */
    private fun onWorkspaceRecommendationsClicked() {
        mWorkspaceListViewModel.getWorkspaceRecommendations(20, (activity as HomeActivity).currUserToken)
        mWorkspaceListViewModel.getWorkspaceRecommendationsResponse.observe(viewLifecycleOwner, {t->
            when(t.javaClass){
                Resource.Loading::class.java ->dialog.show()
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceListViewModel.getWorkspaceRecommendationsResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> dialog.dismiss()
                Resource.Success::class.java->{
                    if(t.data!!.recommendation_list.isEmpty()){
                        Toast.makeText(requireContext(), "No new recommendations found", Toast.LENGTH_LONG).show()
                    }
                    else {
                        wsRecommendedBinding = DialogRecommendedBinding.inflate(
                            layoutInflater,
                            requireView().parent as ViewGroup,
                            false
                        )
                        recDialog = AlertDialog.Builder(context).setView(wsRecommendedBinding.root)
                            .setCancelable(true).create()
                        recDialog.show()
                        wsRecommendedBinding.recommendedRv.adapter = WorkspaceRecommendationAdapter(ArrayList(), requireContext(), this)
                        wsRecommendedBinding.recommendedRv.layoutManager = LinearLayoutManager(requireContext())
                        (wsRecommendedBinding.recommendedRv.adapter as WorkspaceRecommendationAdapter).submitElements(t.data!!.recommendation_list)
                        wsRecommendedBinding.recommendedTitleTv.setOnClickListener {
                            recDialog.dismiss()
                        }
                    }

                    mWorkspaceListViewModel.getWorkspaceRecommendationsResponse.value = Resource.Done()

                }
            }
        })
    }

    /*
     *  Creates and returns the view hierarchy associated with the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkspaceListBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    /*
    *  After view creation listeners and observers implemented
    */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        fetchWorkspaces()
    }

    /*
    *  Layout manager and adapters of recycler view added
    */
    private fun setListeners() {
        workspaceListAdapter = WorkspaceListAdapter(ArrayList(), requireContext(), this)
        val layoutManager = LinearLayoutManager(activity)
        binding.workspaceListRv.layoutManager = layoutManager
        binding.workspaceListRv.adapter = workspaceListAdapter

//        binding.workspaceListRv.addOnScrollListener(object: PaginationListener(layoutManager){
//            override fun loadMoreItems() {
//                //isLoading = true;
//                currentPage++;
//
//            }
//
//            override var isLastPage: Boolean = false
//            override var isLoading: Boolean = false
//            override var currentPage: Int = PAGE_START
//
//        })
    }

    /*
     *  Observers of the view model responses handled
     */
    private fun setObservers(){
        dialog = Definitions().createProgressBar(requireContext())
        mWorkspaceListViewModel.workspaces.observe(viewLifecycleOwner, Observer {
            when(it.javaClass){
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    workspaceListAdapter.submitElements(it.data!!.workspaces)
                }
            }
        })
    }

    /*
     *  Fetch workspace request handled
     */
    private fun fetchWorkspaces(){
        mWorkspaceListViewModel.getWorkspaces((activity as HomeActivity).currUserToken!!)
    }

    /*
     *  Visibilities of top menu bar items handled
     */
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        menu.findItem(R.id.workspace_recommendation_btn)?.isVisible = true
        menu.findItem(R.id.add_workspace_btn)?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    /*
     *  Click on workspace list handled
     */
    override fun onWorkspaceListButtonClicked(binding: WorkspaceCellBinding, projectId: Int) {
        val bnd = Bundle()
        bnd.putString("token", (activity as HomeActivity).currUserToken)
        bnd.putInt("user_id", (activity as HomeActivity).currUserId)
        bnd.putBoolean("add", false)
        bnd.putInt("workspace_id", projectId)
        bnd.putBoolean("isOwner", true)
        startActivity(Intent(activity, WorkspaceActivity::class.java).putExtras(bnd))
    }

    override fun onResume() {
        workspaceListAdapter.clearElements()
        mWorkspaceListViewModel.getWorkspaces((activity as HomeActivity).currUserToken)
        super.onResume()
    }

    /*
     *  Click on workspace handled
     */
    override fun onWorkspaceClicked(binding: WorkspaceCellBinding, project: RecommendedWorkspace) {
        val bnd = Bundle()
        bnd.putString("token", (activity as HomeActivity).currUserToken)
        bnd.putInt("user_id", (activity as HomeActivity).currUserId)
        bnd.putBoolean("add", false)
        bnd.putInt("workspace_id", project.id)
        val isContributing = project.contributor_list.map { it.id }.contains((activity as HomeActivity).currUserId)
        bnd.putBoolean("isOwner", isContributing)
        recDialog.dismiss()
        startActivity(Intent(activity, WorkspaceActivity::class.java).putExtras(bnd))
    }
}