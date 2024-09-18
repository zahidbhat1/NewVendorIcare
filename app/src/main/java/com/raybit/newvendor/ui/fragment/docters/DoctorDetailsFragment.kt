package com.raybit.newvendor.ui.fragment.docters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope

import com.raybit.newvendor.ui.adapter.DocsAdapter
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.ProgressDialog
import com.raybit.newvendor.utils.loadImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.FirebaseConsultationRequest
import com.raybit.newvendor.data.models.chat.Conversation
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentDoctorDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DoctorDetailsFragment : BaseContainerFragment<FragmentDoctorDetailBinding>() {
    private var alreadyRequested: Boolean = false
    private lateinit var myref: DatabaseReference
    private lateinit var doctor: LoginResponse
    private lateinit var conversation: Conversation
    private lateinit var userData: LoginResponse
    private lateinit var catAdapter: DocsAdapter
    private lateinit var binding: FragmentDoctorDetailBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var database: FirebaseDatabase

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
            requireContext().toast(it.message!!)
        })
        viewModel.user.observe(this, Observer {
            conversation = it
            setUserData(conversation.user)
        })

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        progressDialog = ProgressDialog(requireActivity())
        database = Firebase.database

//        viewModel.getDoctors(requireArguments()["id"] as Int)
//        (requireActivity() as HomeActivity)
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
                userData = it!!
            }
        }
        intialize()
        listeners()
    }

    private fun listeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.llChat.setOnClickListener {
            if (!alreadyRequested) {
                var conversation = FirebaseConsultationRequest(
                    myref.key ?: "",
                    0,
                    "pending",
                    Date().time,
                    userData.id
                )
                myref.push().setValue(conversation)
            } else {
                requireContext().toast("Already a pending Request")
            }
//            if (requireArguments().containsKey("fromChat")) {
//                requireActivity().onBackPressed()
//            } else {
//                myref.child(key ?: "").setValue(conversation)
//                findNavController().navigate(
//                    R.id.action_detail_to_chat,
//                    bundleOf("DATA" to Gson().toJson(conversation))
//                )
//            }
//
        }
    }

    private fun intialize() {

        if (requireArguments()["DATA"] != null) {
            doctor = Gson().fromJson<LoginResponse>(
                requireArguments()["DATA"].toString(),
                LoginResponse::class.java
            )
            viewModel.loadUserData(doctor.id, userData.id)
        }
        myref = database.getReference("requests").child(doctor.id.toString())
        myref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    alreadyRequested = false
                    var data = snapshot.value as HashMap<*, *>
                    data.forEach {
                        var requestData = it.value as HashMap<*, *>
                        if (requestData["userId"] as Long == userData.id.toLong() && requestData["status"].toString() == "pending") {
                            alreadyRequested = true
                        }
                    }
                } else {
                    alreadyRequested = false
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setUserData(doctor: LoginResponse) {
//        doctor.conversation.user=doctor
        binding.tvName.text = doctor.user_profile.fullname
        binding.tvAboutV.text =
            "${doctor.user_profile.address}, ${doctor.user_profile.city} \n${doctor.user_profile.country}"
        if (doctor.role_id == 2) {
            binding.tvDesc.text = if (doctor.category != null) doctor.category.title else ""
            doctor.user_qualifications.groupBy {
                binding.tvQualV.text =
                    binding.tvQualV.text.toString() + " " + if (it != null) it.title else "N/A"
            }
        } else {
            binding.tvQualV.hide()
            binding.tvQual.hide()
        }
        if (doctor.user_profile.image_url != null) {
            doctor.user_profile.image_url = doctor.user_profile.image_url.replace(
                "http://127.0.0.1:8000/",
                "http://192.168.100.21/hospital/public/"
            )
            binding.ivPic.loadImage(doctor.user_profile.image_url, R.drawable.placeholder)
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_doctor_detail


}