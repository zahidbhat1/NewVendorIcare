package com.raybit.newvendor.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import com.pawegio.kandroid.toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels

import androidx.lifecycle.lifecycleScope
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
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallingActivity : BaseActivity(), AlertDialogUtil.OnOkCancelDialogListener {
    companion object {
        protected const val PERMISSION_REQ_ID = 22
        protected val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )
    }
    private lateinit var binding: ActivityCallingBinding
    private lateinit var ivSpeaker: ImageView
    private var isSpeakerEnabled: Boolean = false

    private lateinit var userName: String
    private lateinit var userId: String
    val viewModel: HomeViewModel by viewModels()
    private var channelName: String = ""
    private var userData: LoginResponse? = null
    fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[0]
                ) == PackageManager.PERMISSION_GRANTED
    }

    override val layoutResId: Int
        get() = R.layout.activity_calling
    protected var agoraEngine: RtcEngine? = null // The RTCEngine instance
    var remoteUids = HashSet<Int>() // An object to store uids of remote users
    var isJoined = false // Status of the video call
        private set
    var isBroadcaster = true // Local user role

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ivSpeaker = findViewById(R.id.ivSpeaker)
        ivSpeaker.setOnClickListener {
            toggleSpeaker()
        }
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
                userData = it

            }
        }
        channelName = intent.getStringExtra("REQUEST_ID") ?: ""
        userId = intent.getStringExtra("user_id") ?: ""
        userName = intent.getStringExtra("user_name") ?: ""
        binding.tvName.text = userName

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID)
        } else {
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
        }
        viewModel.agoraData.observe(this, Observer {
            joinChannel(
                channelName,
                it.rtcToken
            )
            viewModel.sendCallRequest(
                hashMapOf(
                    "request_id" to channelName,
                    "sender_id" to userData?.id.toString(),
                    "receiver_id" to userId
                )
            )
        })
        viewModel.notifyCall.observe(this, Observer {

        })
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

    private fun toggleSpeaker() {
        isSpeakerEnabled = !isSpeakerEnabled
        agoraEngine?.let {
            it.setEnableSpeakerphone(isSpeakerEnabled)
        }


        val drawable = if (isSpeakerEnabled) {
            R.drawable.background_circle_green
        } else {
            R.drawable.background_circle_white
        }
        ivSpeaker.setBackgroundResource(drawable)
    }



    fun setupAgoraEngine(): Boolean {
        try {
            // Set the engine configuration
            val config = RtcEngineConfig()
            config.mContext = this
            config.mAppId = BuildConfig.AG_APP_ID
            // Assign an event handler to receive engine callbacks
            config.mEventHandler = iRtcEngineEventHandler
            // Create an RtcEngine instance
            agoraEngine = RtcEngine.create(config)
        } catch (e: Exception) {
//            sendMessage(e.toString())
            return false
        }
        return true
    }

    fun destroyAgoraEngine() {
        // Release the RtcEngine instance to free up resources
        RtcEngine.destroy()
        agoraEngine = null
    }

    override fun onDestroy() {
        destroyAgoraEngine()
        super.onDestroy()

    }

    open fun joinChannel(channelName: String, token: String?): Int {
        // Ensure that necessary Android permissions have been granted
        if (!checkSelfPermission()) {
//            sendMessage("Permissions were not granted")
            return -1
        }
        this.channelName = channelName

        // Create an RTCEngine instance
        if (agoraEngine == null) setupAgoraEngine()

        val options = ChannelMediaOptions()
        // For a Video/Voice call, set the channel profile as COMMUNICATION.
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        // Set the client role to broadcaster or audience
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER

        // Join the channel using a token.
        agoraEngine!!.joinChannel(token, channelName, userData?.id ?: 0, options)
        return 0
    }

    protected open val iRtcEngineEventHandler: IRtcEngineEventHandler?
        get() = object : IRtcEngineEventHandler() {
            // Listen for a remote user joining the channel.
            override fun onUserJoined(uid: Int, elapsed: Int) {
                toast("Remote user joined $uid")

                // Save the uid of the remote user.
                remoteUids.add(uid)
//                if (isBroadcaster && (currentProduct == ProductName.INTERACTIVE_LIVE_STREAMING
//                            || currentProduct == ProductName.BROADCAST_STREAMING)
//                ) {
//                    // Remote video does not need to be rendered
//                } else {
//                    // Set up and return a SurfaceView for the new user
//                    setupRemoteVideo(uid)
//                }
            }

            override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                // Set the joined status to true.
                isJoined = true
//                sendMessage("Joined Channel $channel")
                // Save the uid of the local user.
                toast("Joined Channel $channel")
//                mListener!!.onJoinChannelSuccess(channel, uid, elapsed)
            }

            override fun onUserOffline(uid: Int, reason: Int) {
//                sendMessage("Remote user offline $uid $reason")
                // Update the list of remote Uids
                toast("Remote user offline $uid $reason")
                remoteUids.remove(uid)
                // Notify the UI
//                mListener!!.onRemoteUserLeft(uid)
            }

            override fun onError(err: Int) {
                when (err) {
//                    ErrorCode.ERR_TOKEN_EXPIRED -> sendMessage("Your token has expired")
//                    ErrorCode.ERR_INVALID_TOKEN -> sendMessage("Your token is invalid")
//                    else -> sendMessage("Error code: $err")
                }
            }
        }

    override fun onOkButtonClicked() {
        viewModel.acceptRequest(channelName.toInt(), "complete")
    }

    override fun onCancelButtonClicked() {
        finish()
    }

}