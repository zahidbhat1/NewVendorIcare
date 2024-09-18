package com.raybit.newvendor.ui.fragment.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope

import com.raybit.newvendor.ui.viewModel.HomeViewModel

import com.raybit.newvendor.utils.loadImage
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentEditProfileBinding
import com.raybit.newvendor.utils.FileUtils
import com.raybit.newvendor.utils.ProgressDialog
import com.raybit.newvendor.utils.onSnackbar
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditProfileFragment : BaseContainerFragment<FragmentEditProfileBinding>() {
    var SELECT_IMAGE = 3
    var showPassword = true
    private var imageBody: MultipartBody.Part? = null
    private lateinit var userData: LoginResponse
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var progressDialog: ProgressDialog
    @Inject
    lateinit var gson: Gson
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
        viewModel.update.observe(this, Observer {
            lifecycleScope.launch {
                mDataStoreHelper.addGsonValue(DataStoreConstants.USER_DATA, gson.toJson(it))
                requireContext().toast("Profile Updated Successfully!")
            }
        })

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
                setUserData(userData)
            }
        }
        intialize()
        listeners()
    }

    private fun getMediaPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1011
            )

        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1011
            )
        }
    }

    private fun hasMediaPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun listeners() {
//        tvChats.setOnClickListener {
//            findNavController().navigate(R.id.fragmentConversation)
//        }
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnUpdate.setOnClickListener {
            hideKeyboard()
            if (checkValidations()) {
                var hashMap = HashMap<String, RequestBody>()
                hashMap["name"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.etName.text.toString().trim()
                )
               if(binding.etPassword.text.toString().isNotEmpty()){
                   hashMap["password"] = RequestBody.create(
                       "text/plain".toMediaTypeOrNull(), binding.etPassword.text.toString().trim()
                   )
               }
                hashMap["phone"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.etPhoneNo.text.toString().trim()
                )
                hashMap["id"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),userData.id.toString()
                )
                hashMap["country"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.etCountry.text.toString().trim()
                )
                hashMap["state"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.etState.text.toString().trim()
                )
                hashMap["dob"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.etDob.text.toString().trim()
                )
                hashMap["city"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.etCity.text.toString().trim()
                )
                hashMap["address"] = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.etAddress.text.toString().trim()
                )
                if (imageBody != null) {
                    viewModel.update(hashMap, imageBody!!)
                }else{
                    viewModel.update(hashMap)
                }
            }
        }
        binding.tvShow.setOnClickListener {
            if (showPassword) {
                showPassword = false
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvShow.text = getString(R.string.hide)

            } else {
                showPassword = true
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.tvShow.text = getString(R.string.show)
            }
        }
        binding.ivProfile.setOnClickListener {
            if (hasMediaPermission()) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    SELECT_IMAGE
                )
            } else {
                getMediaPermission()
            }
        }

    }
    private fun checkValidations(): Boolean {
        when {

//            binding.etPhoneNo.text?.isEmpty()!! -> {
//                binding.root.onSnackbar(getString(R.string.phone_empty))
//                return false
//            }


            binding.etName.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.empty_name))
                return false
            }

            binding.etPhoneNo.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.phone_empty))
                return false
            }
            binding.etCountry.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.country_empty))
                return false
            }
            binding.etState.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.state_empty))
                return false
            }
            binding.etCity.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.city_empty))
                return false
            }
            binding.etAddress.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.address_empty))
                return false
            }
            binding.etDob.text!!.isEmpty() -> {
                binding.root.onSnackbar(getString(R.string.error_date_of_birth))
                return false
            }

            else -> {
                return true
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
//            Log.e("PATH", FileUtils.getPath(data!!.data!!)!!)
            val file = FileUtils.getFile(requireContext(), data?.data)
            Glide.with(this).load(file).into(binding.ivProfile)
            val requestFile: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            imageBody = MultipartBody.Part.createFormData(
                "image",
                file.name + Calendar.getInstance().timeInMillis,
                requestFile
            )

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
        binding.etName.setText( doctor.user_profile.fullname)
        binding.etPhoneNo.setText(doctor.user_profile.phone)
        binding.etDob.setText(doctor.user_profile.dob)
        binding.etCountry.setText(doctor.user_profile.country)
        binding.etState.setText(doctor.user_profile.state)
        binding.etCity.setText(doctor.user_profile.city)
        binding.etAddress.setText(doctor.user_profile.address)
//        binding.tvAboutV.text =
//            "${doctor.user_profile.address}, ${doctor.user_profile.city} \n${doctor.user_profile.country}"
        if (doctor.user_profile.image_url != null) {
            doctor.user_profile.image_url = doctor.user_profile.image_url.replace(
                "http://127.0.0.1:8000/",
                "http://192.168.100.21/hospital/public/"
            )
            binding.ivProfile.loadImage(doctor.user_profile.image_url, R.drawable.placeholder)
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_edit_profile


}