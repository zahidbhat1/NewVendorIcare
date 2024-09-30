package com.raybit.newvendor.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.raybit.newvendor.databinding.ActivityVideoCallingBinding
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.AlertDialogUtil
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VideoCallingActivity : BaseActivity(), AlertDialogUtil.OnOkCancelDialogListener {
    companion object {
        private val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }

    private lateinit var binding: ActivityVideoCallingBinding
  //  private var isSpeakerEnabled = false
    private var userData: LoginResponse? = null
    private var channelName: String = ""
    private lateinit var userId: String
//    private var isMuted: Boolean = false
    private lateinit var userName: String
    private lateinit var rtcEngine: RtcEngine

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper

    override val layoutResId: Int
        get() = R.layout.activity_calling

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.RECORD_AUDIO] == true &&
                    permissions[Manifest.permission.CAMERA] == true
            if (granted) {
                initializeLocalVideo()  // Initialize camera preview immediately
                requestTokenAndJoinChannel()
            } else {
                toast("Permissions denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        fetchUserData()

        channelName = intent.getStringExtra("REQUEST_ID") ?: ""
        userId = intent.getStringExtra("user_id") ?: ""
        userName = intent.getStringExtra("user_name") ?: ""
    }

    private fun setupUI() {
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
        binding.ivSpeaker.setOnClickListener {
          //  toggleSpeaker()
        }
        binding.ivflipCam.setOnClickListener {
            flipCamera()
        }
        binding.ivMic.setOnClickListener {
//            toggleMute()
        }


    }


    private fun fetchUserData() {
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(DataStoreConstants.USER_DATA, LoginResponse::class.java)
                .collectLatest { user ->
                    userData = user
                    if (checkPermissions()) {
                        initializeLocalVideo()  // Initialize local video preview before joining the channel
                        requestTokenAndJoinChannel()
                    } else {
                        permissionLauncher.launch(REQUESTED_PERMISSIONS)
                    }
                }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initializeLocalVideo() {
        try {
            val appId = BuildConfig.AG_APP_ID
            rtcEngine = RtcEngine.create(baseContext, appId, agoraEventHandler)
            rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
            rtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

            // Enable video and configure the video settings
            rtcEngine.enableVideo()
            rtcEngine.enableAudio()
            rtcEngine.setVideoEncoderConfiguration(
                VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_320x240,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,

                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
                )

            )
            rtcEngine.enableDualStreamMode(true)


            // Initialize local video view for camera preview
            val surfaceView = SurfaceView(this)
            binding.localVideoViewContainer.addView(surfaceView)
            rtcEngine.setupLocalVideo(
                VideoCanvas(
                    surfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN,
                    0
                )
            )
        rtcEngine.startPreview()

        } catch (e: Exception) {
            Log.e("Agora", "Error initializing Agora engine for local preview: $e")
        }
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

        val uid = userData?.id ?: 0

        try {
            // Joining channel logic remains the same after initializing the local video
            val options = ChannelMediaOptions()
            options.autoSubscribeAudio = true
            options.autoSubscribeVideo = true

            rtcEngine.joinChannel(token, channelName, uid, options)
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
                Log.d("Agora", "Remote User with ID: $uid joined the channel")
                toast("User $uid joined")

                // Initialize remote video view
                val surfaceView = SurfaceView(this@VideoCallingActivity)
                binding.remoteVideoViewContainer.addView(surfaceView)
                rtcEngine.setupRemoteVideo(
                    VideoCanvas(
                        surfaceView,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        uid
                    )
                )
                binding.remoteVideoViewContainer.visibility = View.VISIBLE
                switchToRemoteView()
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

        override fun onError(err: Int) {
            runOnUiThread {
                Log.e("Agora", "Agora error: $err")
            }
        }
    }

//    private fun toggleSpeaker() {
//        isSpeakerEnabled = !isSpeakerEnabled
//        rtcEngine.setEnableSpeakerphone(isSpeakerEnabled)
//
//        val drawable = if (isSpeakerEnabled) {
//            R.drawable.background_circle_green
//        } else {
//            R.drawable.background_circle_white
//        }
//    }
    private fun flipCamera() {
        try {
            rtcEngine?.switchCamera()
            Log.d("Agora", "Camera flipped successfully")
        } catch (e: Exception) {
            Log.e("Agora", "Error while flipping the camera: $e")
            toast("Unable to flip camera.")
        }
    }
//    private fun toggleMute() {
//        isMuted = !isMuted
//        rtcEngine?.muteLocalAudioStream(isMuted)
//
//        if (isMuted) {
//            // Change to mic-off icon when muted
//            binding.ivMic.setImageResource(R.drawable.ic_mic_off)
//        } else {
//            // Clear the background and revert to the original image from the layout
//            binding.ivMic.setImageResource(R.drawable.ic_mic)
//        }
//    }


    private fun switchToRemoteView() {
        // Resize local video to be smaller and move it to a corner
        val layoutParams =
            binding.localVideoViewContainer.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = resources.getDimensionPixelSize(R.dimen.small_video_width)
        layoutParams.height = resources.getDimensionPixelSize(R.dimen.small_video_height)
        // Define constraints to move the local view to the bottom-right corner
        layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.UNSET
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        binding.localVideoViewContainer.layoutParams = layoutParams
        binding.localVideoViewContainer.bringToFront()
        (binding.localVideoViewContainer.getChildAt(0) as SurfaceView).setZOrderMediaOverlay(
            true
        )
        enableLocalVideoDragging()
    }

    private fun enableLocalVideoDragging() {
        var dX = 0f
        var dY = 0f

        binding.localVideoViewContainer.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    // Optional: Prevent the view from moving off-screen
                    val maxX = (binding.root.width - view.width).toFloat()
                    val maxY = (binding.root.height - view.height).toFloat()
                    val finalX = newX.coerceIn(0f, maxX)
                    val finalY = newY.coerceIn(0f, maxY)

                    view.animate()
                        .x(finalX)
                        .y(finalY)
                        .setDuration(0)
                        .start()
                }

                else -> return@setOnTouchListener false
            }
            true
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        rtcEngine.leaveChannel()
        RtcEngine.destroy()
    }

    override fun onOkButtonClicked() {
        viewModel.acceptRequest(channelName.toInt(), "complete")
    }

    override fun onCancelButtonClicked() {
        finish()
    }
}
