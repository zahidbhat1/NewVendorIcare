package com.raybit.newvendor.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.pawegio.kandroid.toast
import com.raybit.newvendor.BuildConfig
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.activity.BaseActivity
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.ActivityCallingBinding
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.AlertDialogUtil
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallingActivity : BaseActivity(), AlertDialogUtil.OnOkCancelDialogListener {
    companion object {
        private val REQUESTED_PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO)
    }

    private lateinit var binding: ActivityCallingBinding
    private var isSpeakerEnabled = false
    private var userData: LoginResponse? = null
    private var channelName: String = ""
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var rtcEngine: RtcEngine

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper

    override val layoutResId: Int
        get() = R.layout.activity_calling

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            requestTokenAndJoinChannel()
        } else {
            toast("Permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        fetchUserData()


        channelName = intent.getStringExtra("REQUEST_ID") ?: ""
        userId = intent.getStringExtra("user_id") ?: ""
        userName = intent.getStringExtra("user_name") ?: ""
        binding.tvName.text = userName
    }

    private fun setupUI() {
        binding.ivSpeaker.setOnClickListener { toggleSpeaker() }

        binding.ivRejectCall.setOnClickListener {
            AlertDialogUtil().createOkCancelDialog(
                this,
                R.string.alert,
                R.string.complete_msg,
                R.string.yes,
                R.string.no,
                true,
                this
            ).show()
        }
    }

    private fun fetchUserData() {
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(DataStoreConstants.USER_DATA, LoginResponse::class.java)
                .collectLatest { user ->
                    userData = user
                    if (checkPermissions()) {
                        requestTokenAndJoinChannel()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestTokenAndJoinChannel() {
        if (userData == null) {
            toast("User data not loaded.")
            return
        }

        Log.d("Agora", "Requesting token for channel: $channelName")
        viewModel.getVoiceToken(
            hashMapOf(
                "appId" to BuildConfig.AG_APP_ID,
                "certificate" to BuildConfig.AG_CERTIFY,
                "channel" to channelName,
                "uid" to userData?.id.toString(),
                "role" to "publisher",
                "expire" to 30000
            )
        )
        observeAgoraData()
    }

    private fun observeAgoraData() {
        viewModel.agoraData.observe(this) { tokenData ->
            joinAgoraChannel(tokenData.rtcToken)
            sendCallRequest()
        }
    }

    private fun joinAgoraChannel(token: String) {
        val appId = BuildConfig.AG_APP_ID
        val uid = userData?.id ?: 0

        try {
            rtcEngine = RtcEngine.create(baseContext, appId, agoraEventHandler)
            rtcEngine?.enableAudio()
            rtcEngine?.enableAudioVolumeIndication(5000, 3 ,true)
            rtcEngine?.setChannelProfile(io.agora.rtc2.Constants.CHANNEL_PROFILE_COMMUNICATION)
            rtcEngine?.joinChannel(token, channelName, uid, ChannelMediaOptions())

            rtcEngine?.startPreview()
            toast("Joined channel: $channelName $uid")
            Log.d("Agora", "User joined channel $channelName with token $token")
        } catch (e: Exception) {
            Log.e("Agora", "Error initializing Agora engine: $e")
        }
    }

    private fun sendCallRequest() {
        viewModel.sendCallRequest(
            hashMapOf(
                "request_id" to channelName,
                "sender_id" to userData?.id.toString(),
                "receiver_id" to userId
            )
        )
    }

    private val agoraEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                Log.d("Agora", " REmote User with the ID: $uid joined the channel")
                toast("User $uid joined")
            }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            runOnUiThread {
                toast("Successfully joined channel: $channel")
                Log.d("Agora", "Successfully joined channel: $channel, with UID: $uid")
            }

        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                toast("User went offline: $uid")
                Log.d("Agora", "User offline with UID: $uid, reason: $reason")
            }
        }

//        override fun onAudioVolumeIndication(
//            speakers: Array<out AudioVolumeInfo>?,
//            totalVolume: Int
//        ) {
//            runOnUiThread {
//                speakers?.forEach { speaker ->
//                    Log.d("Remote Agora", "User ${speaker.uid} has volume: ${speaker.volume}")
//                }
//                Log.d("Agora", "Total audio volume: $totalVolume")
//            }
//        }

        override fun onError(err: Int) {
            runOnUiThread {
                Log.e("Agora", "Agora error: $err")
            }
        }
    }

    private fun toggleSpeaker() {
        isSpeakerEnabled = !isSpeakerEnabled
        rtcEngine.setEnableSpeakerphone(isSpeakerEnabled)

        val drawable = if (isSpeakerEnabled) {
            R.drawable.background_circle_green
        } else {
            R.drawable.background_circle_white
        }
        binding.ivSpeaker.setBackgroundResource(drawable)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::rtcEngine.isInitialized) {
            rtcEngine.leaveChannel()
            RtcEngine.destroy()
        }
    }

    override fun onOkButtonClicked() {
        viewModel.acceptRequest(channelName.toInt(), "complete")
    }

    override fun onCancelButtonClicked() {
        finish()
    }
}
