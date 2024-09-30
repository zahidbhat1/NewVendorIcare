package com.raybit.newvendor.ui.activity

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext

import io.agora.rtc2.*
import io.agora.rtc2.RtcEngineConfig
import javax.inject.Inject
import javax.inject.Singleton
class AgoraManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    var agoraEngine: RtcEngine? = null
    private val remoteUids = HashSet<Int>()
    private var isSpeakerEnabled:Boolean=false

    // Initialize Agora engine
    fun initAgoraEngine(appId: String, eventHandler: IRtcEngineEventHandler): Boolean {
        try {
            val config = RtcEngineConfig().apply {
                mContext = context
                mAppId = appId
                mEventHandler = eventHandler
            }
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.setDefaultAudioRoutetoSpeakerphone(false)
            Log.d("Agora", "Agora engine initialized successfully")
        } catch (e: Exception) {
            Log.e("Agora", "Error initializing Agora engine: ${e.message}")
            return false
        }
        return true
    }

    // Join channel
    fun joinChannel(token: String?, channelName: String, uid: Int, isBroadcaster: Boolean) {
        val options = ChannelMediaOptions().apply {
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = if (isBroadcaster) Constants.CLIENT_ROLE_BROADCASTER else Constants.CLIENT_ROLE_AUDIENCE
        }
if(agoraEngine!=null){
    agoraEngine?.joinChannel(token, channelName, uid, options)
}
        else Log.d("agora ","agoraEngineIsNull"
        )
        agoraEngine?.joinChannel(token, channelName, uid, options)
    }
    fun toggleSpeaker() {
        isSpeakerEnabled = !isSpeakerEnabled
        agoraEngine?.setEnableSpeakerphone(isSpeakerEnabled)

        Log.d("AgoraManager", "Speaker toggled to: $isSpeakerEnabled")
    }


    // Leave channel
    fun leaveChannel() {
        agoraEngine?.leaveChannel()
        RtcEngine.destroy()
        agoraEngine = null
    }

    fun getRemoteUids(): HashSet<Int> = remoteUids

    // Add any other Agora methods needed (mute, unmute, etc.)
}
