package com.raybit.newvendor.ui.fragment.chatModule
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.raybit.newvendor.R
import com.raybit.newvendor.ui.adapter.ChatAdapter
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.AlertDialogUtil
import com.raybit.newvendor.utils.CallAction
import com.raybit.newvendor.utils.SocketManager
import com.raybit.newvendor.utils.SocketManager.Companion.EVENT_ADD_MESSAGE_RESPONSE
import com.raybit.newvendor.utils.compressImage
import com.raybit.newvendor.utils.getPathUri
import com.google.gson.Gson
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.show
import com.pawegio.kandroid.toast
import com.pawegio.kandroid.visible
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.chat.ChatMessage
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChatFragment : BaseContainerFragment<FragmentChatBinding>(), SocketManager.OnMessageReceiver {

    private var requestId: Int = 0
    private lateinit var userName: String
    private var userId: Int = 0
    private var fileUrl: String = ""
    private var chattingList = ArrayList<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private   var userData: LoginResponse?= null
    private lateinit var binding: FragmentChatBinding
    private val socketManager = SocketManager.getInstance()
    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper
    val viewModel: HomeViewModel by viewModels()
    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val docPaths = data?.data
                if (docPaths != null) {
                    lifecycleScope.launch {
                        val fileToUpload = File(getPathUri(requireContext(), docPaths))
                        val compressedFile = compressImage(requireActivity(), fileToUpload)
                        if (compressedFile != null) {
                            uploadFileOnServer(compressedFile)
                        }
                    }
                }
            }
        }
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                selectImages()
            } else {
                requireContext().toast("Permission denied")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {
                if (it) {

                }
            }
        }

    }

    private fun selectImages() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun uploadFileOnServer(fileToUpload: File) {
        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fileToUpload)
        val imageBody = MultipartBody.Part.createFormData(
            "image",
            fileToUpload.name,
            requestFile
        )

        viewModel.uploadFile(imageBody)
    }

    private fun bindObserver() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
           binding. pbLoaderBottom?.visible = it
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            requireContext().toast(it.message!!)
        })
        viewModel.file.observe(viewLifecycleOwner, Observer {
            fileUrl = it.url
           requireContext(). toast(it.url)
        })
        viewModel.chats.observe(viewLifecycleOwner, Observer {
            chattingList.clear()
            chattingList.addAll(it)
            chatAdapter.notifyDataSetChanged()
            binding. rvChatData.scrollToPosition(chattingList.size - 1)
        })
        viewModel.request.observe(viewLifecycleOwner, Observer { request ->
            if (request.status != CallAction.INPROGRESS) {
                binding.rlChatInput.hide()
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        bindObserver()


        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest { user ->
                Log.d("ChatFragment", "Retrieved user: $user")
                if (user != null) {
                    userData = user
                    socketManager.connect(requireActivity(), userData!!, this@ChatFragment)
                    setAdapter()
                    if(arguments?.getInt("id") != null){
                        hitApi()
                    }

                }
                else {
                    Log.d("ChatFragment", "User data is null")
                }
            }
        }

        if (arguments?.getInt("id") != null) {
            userId = arguments?.getInt("user_id") ?: 0
            userName = arguments?.getString("user_name") ?: ""
            var status = arguments?.getString("status") ?: ""
            if (status == CallAction.INPROGRESS) {
                binding.rlChatInput.show()
            } else {
                binding.rlChatInput.hide()
            }
            requestId = arguments?.getInt("id") ?: 0
            binding.tvUserName.text = userName
//         if(arguments==null){
//             Log.d("ChatFragment","arguments are null")
//         }
//            else{
//                hitApi()
//         }

        }

        listeners()

//        (requireActivity() as HomeActivity)
    }

    private fun setAdapter() {
        if (userData == null) {
            Log.e("ChatFragment", "userData is null on adapters")
            return
        }

        // No need for !! on chattingList since it's initialized
        chatAdapter = ChatAdapter(chattingList, requireContext(), requireActivity(), userData!!)
        binding.rvChatData.adapter = chatAdapter
    }


    private fun listeners() {
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (userData?.id != null) {
                    if (binding.etMessage.text.toString().isNotEmpty()) {
                        sendIamTyping(true)
                    } else sendIamTyping(false)
                }
            }
        })
        binding.btnCamera.setOnClickListener {
            hideKeyboard()
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            )
        }
        binding.ivBack.setOnClickListener {
            hideKeyboard()
            requireActivity().onBackPressed()
        }
        binding.ivCompleteChat.setOnClickListener {
            showCompleteRequestDialog()
        }
        binding. ivSend.setOnClickListener {
            if (binding.etMessage.text.toString().trim().isNotEmpty()) {
                sendMessage(binding.etMessage.text.toString().trim())
                binding.etMessage.setText("")
            } else if (fileUrl.isNotEmpty()) {
                sendMessage("File")
                binding.etMessage.setText("")
            }
        }
    }

    override fun onDestroy() {
        SocketManager.destroy()
        super.onDestroy()
    }

    private fun showCompleteRequestDialog() {
        AlertDialogUtil.instance.createOkCancelDialog(requireContext(), R.string.service,
            R.string.end_chat_desc, R.string.end_chat, R.string.cancel, false,
            object : AlertDialogUtil.OnOkCancelDialogListener {
                override fun onOkButtonClicked() {
                    hitApiAcceptRequest()
                }

                override fun onCancelButtonClicked() {
                }
            }).show()
    }

    private fun hitApiAcceptRequest() {
        viewModel.acceptRequest(requestId, "complete")
    }

    private fun sendMessage(message: String) {
        val arguments = JSONObject()
        arguments.putOpt("consultation_request_id", requestId)
        arguments.putOpt("sender_id", userData!!.id)
        arguments.putOpt("receiver_id", userId)
        arguments.putOpt("file_url", fileUrl)
        arguments.putOpt("message", message)
        if (fileUrl.isNotEmpty()) {
            arguments.putOpt("type", "image")
        } else {
            arguments.putOpt("type", "text")
        }
        socketManager.sendMessage(arguments, this)
        fileUrl = ""
    }

    private fun sendIamTyping(b: Boolean) {
        val arguments = JSONObject()
        arguments.putOpt("is_typing", b)
        arguments.putOpt("sender_id", userData!!.id)
//        arguments.putOpt("receiver_id", conversation!!.user.id)
//        socketManager.sendTyping(arguments, this)
    }

    private fun hitApi() {
        if (requestId != 0 && userData!=null) {
            viewModel.getChat(requestId)
            socketManager.connect(requireActivity(), userData!!, this@ChatFragment)
            socketManager.addChatMessageReadListener(this)
            socketManager.addOnlineStatusListener(this)
            socketManager.onTyping(this)
            socketManager.addChatMessageListener(this)
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_chat

    override fun onMessageReceive(message: String, event: String) {
        runOnUiThread {
//            toast(message)
            if (event == EVENT_ADD_MESSAGE_RESPONSE) {
                var chat: ChatMessage = Gson().fromJson(message, ChatMessage::class.java)
                chattingList.add(chat)
                chatAdapter.notifyDataSetChanged()
                binding.rvChatData?.scrollToPosition(chattingList.size - 1)
            }

        }
    }

    override fun onResume() {
        if (userData!= null){

            socketManager.connect(requireActivity(), userData!!, this@ChatFragment)

        }
        else{
        Log.d("ChatFragment","userdata is null on resume  ")
        }
        super.onResume()

    }


}