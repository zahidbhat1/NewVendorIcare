package com.raybit.newvendor.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.raybit.newvendor.ui.activity.CallingActivity
import com.raybit.newvendor.ui.adapter.RequestAdapter
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.CallAction
import com.raybit.newvendor.utils.CallType
import com.raybit.newvendor.utils.POSITION
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.ConsultationRequest
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.data.models.service.Category
import com.raybit.newvendor.databinding.FragmentRequestsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RequestFragment(var homeFragment: HomeFragment) :
    BaseContainerFragment<FragmentRequestsBinding>() {

    private lateinit var selectedrequest: ConsultationRequest
    private lateinit var adapter: RequestAdapter
    private var myRequests = ArrayList<ConsultationRequest>()
    private lateinit var userData: LoginResponse
    private lateinit var binding: FragmentRequestsBinding

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper
    val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {
                if (it) {

                }
            }
        }
        bindObserver()
    }

    private fun bindObserver() {
        viewModel.loading.observe(this, Observer {
//            binding.progressCircular.visible = it
        })

        viewModel.error.observe(this, Observer {
            requireContext().toast(it.message!!)
        })
        viewModel.myRequests.observe(this, Observer {
            myRequests = it
            adapter = RequestAdapter(this, myRequests)
            binding.rvListing.adapter = adapter
        })
        viewModel.request.observe(this, Observer { request ->
            if (request.id == myRequests.get(myRequests.indexOf(selectedrequest)).id) {
                myRequests[myRequests.indexOf(selectedrequest)].status = request.status
                var myRequest=  myRequests[myRequests.indexOf(selectedrequest)]
                adapter.notifyDataSetChanged()
                if (request.status == CallAction.INPROGRESS) {
                    findNavController().navigate(
                        R.id.action_request_to_chat,
                        bundleOf(
                            "id" to myRequest.id,
                            "user_id" to myRequest.user_id,
                            "user_name" to myRequest.user_profile.fullname
                        )
                    )
                }
            }
        })

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()

        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
                if (it != null) {
                    userData = it
                    val serviceType = arguments?.getString(POSITION) ?: CallType.CHAT
                    // Call this function only after userData is initialized
                    viewModel.getMyRequests(userData.id, serviceType)
                } else {
                    Log.e("RequestFragment", "userData is null, can't proceed")
                    // Handle the null state appropriately, show error or retry logic
                }
            }
        }

    }

    fun clickItem(category: Category) {
        findNavController().navigate(
            R.id.action_homeFragment_to_doctors,
            bundleOf("id" to category.id, "name" to category.title)
        )
    }

    fun proceedRequest(request: ConsultationRequest) {
        if (request.status == CallAction.PENDING) {
            selectedrequest = request
            viewModel.acceptRequest(request.id, "accept")
        } else if (request.status == CallAction.ACCEPT) {
            viewModel.acceptRequest(request.id, CallAction.INPROGRESS)
        }else if (request.status == CallAction.INPROGRESS) {
            if(request.type== CallType.CHAT){
                findNavController().navigate(
                    R.id.action_request_to_chat,
                    bundleOf(
                        "id" to request.id,
                        "user_id" to request.user_id,
                        "status" to request.status,
                        "user_name" to request.user_profile.fullname
                    )
                )
            }else  if(request.type== CallType.CALL){
                var it=Intent(requireContext(), CallingActivity::class.java)
                it.putExtra("REQUEST_ID",request.id.toString())
                requireContext().startActivity(it)

            }
        }
    }


    fun cancelAppointment(request: ConsultationRequest) {
//        if(request.status== CallAction.PENDING){
        selectedrequest = request
        viewModel.acceptRequest(request.id, "cancel")
//        }
    }


    override val layoutResourceId: Int
        get() = R.layout.fragment_requests




}