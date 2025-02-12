package com.gimlelarpes.adskipper

import android.accessibilityservice.AccessibilityService
import android.media.AudioManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

class AdSkipperAccessibilityService: AccessibilityService() {

    private val TAG = "AdSkipperService" //Used for debugging
    private val AD_LEARN_MORE_BUTTON_ID = "com.google.android.youtube:id/player_learn_more_button"
    private val SKIP_AD_BUTTON_ID = "com.google.android.youtube:id/skip_ad_button"
    private val AD_PROGRESS_TEXT = "com.google.android.youtube:id/ad_progress_text"
    private val APP_PROMO_AD_CTA_OVERLAY = "com.google.android.youtube:id/app_promo_ad_cta_overlay"
    private val AD_COUNTDOWN = "com.google.android.youtube:id/ad_countdown"

    private lateinit var dataStoreManager: SettingsDataStoreManager
    private lateinit var audioManager: AudioManager

    private var _isAudioMuted = MutableStateFlow(false)
    private var _isServiceRunning = MutableStateFlow(false)
    var serviceRunningFlow: Flow<Boolean> = _isServiceRunning.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        dataStoreManager = SettingsDataStoreManager(context = this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        Log.v(TAG, "onCreate fired")
    }

    override fun onDestroy() {
        _isServiceRunning.value = false
        Log.v(TAG, "onDestroy fired")
        super.onDestroy()
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        dataStoreManager = SettingsDataStoreManager(context = this)
        Log.v(TAG, "onRebind fired")
    }
    companion object {
        private var instance: AdSkipperAccessibilityService? = null
        fun getInstance (): AdSkipperAccessibilityService? = instance
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        Log.v(TAG, "onUnbind fired")
        return super.onUnbind(intent)
    }

    override fun onServiceConnected() {
        _isServiceRunning.value = true
        instance = this
        Log.v(TAG, "onServiceConnected fired")
        super.onServiceConnected()
    }

    override fun onInterrupt() {
        _isServiceRunning.value = false
        Log.v(TAG, "onInterrupt fired")
    }


    // Get settings
    private fun isAdSkipEnabled(): Boolean {
        return runBlocking {
            dataStoreManager.enableAdSkipperService.first()
        }
    }
    private fun isAdMuteEnabled(): Boolean {
        return runBlocking {
            dataStoreManager.enableAdMute.first()
        }
    }


    // Action Functions
    private fun muteMedia() {
        if (_isAudioMuted.value or !isAdMuteEnabled()) {
            return
        }
        // Mute audio
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        _isAudioMuted.value = true
        Log.v(TAG, "Media Muted")
    }
    private fun unMuteMedia() {
        if (!_isAudioMuted.value or !isAdMuteEnabled()) {
            return
        }
        // unMute audio
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
        _isAudioMuted.value = false
        Log.v(TAG, "Media unMuted")
    }



    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.v(TAG, "onAccessibilityEvent fired")
        //
    }
}