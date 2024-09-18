package com.raybit.newvendor.ui.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.raybit.newvendor.ui.activity.HomeActivity
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.ProgressDialog
import com.raybit.newvendor.utils.loadImage
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : BaseContainerFragment<FragmentProfileBinding>() {

    private lateinit var userData: LoginResponse
    private lateinit var binding: FragmentProfileBinding
    private lateinit var progressDialog: ProgressDialog
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
            progressDialog.setLoading(it)
        })

        viewModel.error.observe(this, Observer {
           requireContext(). toast(it.message!!)
        })
//        viewModel.user.observe(this, Observer {
//            conversation=it
//           setUserData(conversation.user)
//        })

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        progressDialog= ProgressDialog(requireActivity())
//        viewModel.getDoctors(requireArguments()["id"] as Int)
//        (requireActivity() as HomeActivity)
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
                userData = it!!
                viewModel.loadUserData(userData.id, userData.id)
                setUserData(userData)
            }
        }
        intialize()
        listeners()
        viewModel.user.observe(this, Observer {
            setUserData(it.user)
        })
    }

    private fun listeners() {
        binding. tvChats.setOnClickListener {
            findNavController().navigate(R.id.fragmentConversation)
        }
        binding.  tvEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_edit)
        }
        binding. tvLogout.setOnClickListener {
            (requireActivity() as HomeActivity).logoutUser()
        }
        binding.llHistory.setOnClickListener{
            findNavController().navigate(R.id.action_profile_to_transactions)
        }
        binding.btnTopUp.setOnClickListener{
//            findNavController().navigate(R.id.action_profile_to_topUp)
        }
    }

    private fun intialize() {

//        if (requireArguments()["DATA"] != null) {
//            var doctor = Gson().fromJson<LoginResponse>(
//                requireArguments()["DATA"].toString(),
//                LoginResponse::class.java
//            )

//            setUserData(doctor)
//            binding.ivPic.loadImage(doctor.userDatauser_profile.image_url,R.drawable.placeholder)
//        }

    }

    private fun setUserData(doctor: LoginResponse) {
//        doctor.conversation.user=doctor
        binding.tvName.text = doctor.user_profile.fullname
        binding.tvEmail.text = doctor.email
        binding.tvBalance.text="₹${doctor.wallet_balance}"
        binding.tvAboutV.text =
            "${doctor.user_profile.address}, ${doctor.user_profile.city} \n${doctor.user_profile.country}"
        if (doctor.role_id == 2) {
            binding.tvEdit.hide()
            doctor.user_qualifications.groupBy { binding.tvQualV.text =
                binding.tvQualV.text.toString() + " " + if (it != null) it.title
                    ?: "N/A" else "N/A"
            }
        } else {
            binding.tvQualV.hide()
            binding.tvQual.hide()

        }
        doctor.user_profile.image_url = doctor.user_profile.image_url.replace(
            "http://127.0.0.1:8000/",
            "http://192.168.100.21/hospital/public/"
        )
        binding.ivProfile.loadImage(doctor.user_profile.image_url, R.drawable.placeholder)
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_profile


}