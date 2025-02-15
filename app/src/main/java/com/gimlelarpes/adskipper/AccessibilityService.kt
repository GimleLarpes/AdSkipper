@file:Suppress("PrivatePropertyName")

package com.gimlelarpes.adskipper

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AdSkipperAccessibilityService: AccessibilityService() {

    // COMPILE LIST OF POSSIBLE IDS AND TEST THEM OUT
    // Figure out floaty vs modern - also only look for type="id"
    private val TAG = "AdSkipperService"
    private val BASE = "com.google.android.youtube:id/"
    private val FLOATY = "floaty_bar_"
    private val SKIP_AD_BUTTON_ID = "skip_ad_button" // This doesn't work for the floaty
    private val CLOSE_AD_PANEL_BUTTON_ID = "panel_ad_close" //WHAT IS THE REAL RESOURCE ID?
    private val AD_PROGRESS_TEXT = "ad_progress_text"
    private val AD_LEARN_MORE_BUTTON_ID = "player_learn_more_button"
    private val APP_PROMO_AD_CTA_OVERLAY = "app_promo_ad_cta_overlay"
    private val AD_COUNTDOWN = "ad_countdown"

    private lateinit var dataStoreManager: SettingsDataStoreManager
    private lateinit var audioManager: AudioManager

    private var _isAudioMuted = MutableStateFlow(false)
    private var _isServiceRunning = MutableStateFlow(false)
    var serviceRunningFlow: Flow<Boolean> = _isServiceRunning.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        dataStoreManager = SettingsDataStoreManager(context = this)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        Log.v(TAG, "onCreate fired")
    }

    override fun onDestroy() {
        _isServiceRunning.value = false
        Log.v(TAG, "onDestroy fired")
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(TAG, "onTaskRemoved fired")
        super.onTaskRemoved(rootIntent)
    }

    override fun onInterrupt() {
        _isServiceRunning.value = false
        Log.v(TAG, "onInterrupt fired")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        dataStoreManager = SettingsDataStoreManager(context = this)
        Log.v(TAG, "onRebind fired")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        Log.v(TAG, "onUnbind fired")
        return super.onUnbind(intent)
    }

    override fun onServiceConnected() {
        _isServiceRunning.value = true
        instance = this
        Log.i(TAG, "onServiceConnected fired")
        super.onServiceConnected()
    }

    companion object {
        private var instance: AdSkipperAccessibilityService? = null
        fun getInstance (): AdSkipperAccessibilityService? = instance
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
        if (_isAudioMuted.value || !isAdMuteEnabled()) {
            return
        }
        // Mute audio
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        _isAudioMuted.value = true
        Log.v(TAG, "Media Muted")
    }
    private fun unMuteMedia() {
        if (!_isAudioMuted.value || !isAdMuteEnabled()) {
            return
        }
        // unMute audio
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
        _isAudioMuted.value = false
        Log.v(TAG, "Media unMuted")
    }

    private fun closeAdPanel(adClosePanel: AccessibilityNodeInfo?) {
        if (adClosePanel?.isClickable == true) {
            Log.v(TAG, "Close panel button is clickable, trying to click...")
            adClosePanel.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.i(TAG, "Yay, Clicked close panel button!")
        }
    }



    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        try {
            if (!isAdSkipEnabled()) {
                Log.v(TAG, "Service is not supposed to be enabled.")
                return
            }

            // Target elements
            // Miniplayer active if
            //val playerActive = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}watch_player")?.getOrNull(0)
            val miniPlayer = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}${FLOATY}controls_view")?.getOrNull(0)
            // WHAT ARE THE floaty_bar EQUIVALENTS?
            val adProgressText = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$AD_PROGRESS_TEXT")?.getOrNull(0)
            val adClosePanelButton = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$CLOSE_AD_PANEL_BUTTON_ID")?.getOrNull(0)

            // Other ad-related elements
            //val adAppPromoCtaOverlay = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(APP_PROMO_AD_CTA_OVERLAY)?.getOrNull(0)
            //val adLearnMore = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(AD_LEARN_MORE_BUTTON_ID)?.getOrNull(0)
            //val adCountdown = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(AD_COUNTDOWN)?.getOrNull(0)
            val test = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$FLOATY$SKIP_AD_BUTTON_ID")?.getOrNull(0)

            //DEBUG STUFF
            if (test != null) {
                Log.v(TAG, "OTHER THING EXISTS")
            }
            if (miniPlayer != null) {
                Log.v(TAG, "MINIPLAYER EXISTS")
            }
            //

            // Visibility check
            if (adProgressText==null && adClosePanelButton==null) {
                unMuteMedia()
                Log.v(TAG, "No ads visible.")
                return

            } else if (adProgressText==null) {
                closeAdPanel(adClosePanelButton)
                unMuteMedia()
                return

            } else {
                val adSkipButton = rootInActiveWindow?.findAccessibilityNodeInfosByViewId(SKIP_AD_BUTTON_ID)?.getOrNull(0)
                Log.v(TAG, "Ad is visible, attempting to skip...")

                // Ad visible
                muteMedia()

                closeAdPanel(adClosePanelButton)

                // Skip ad
                if (adSkipButton?.isClickable == true) {
                    Log.v(TAG, "Skip button is clickable, trying to click...")
                    adSkipButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Log.i(TAG, "Yay, Clicked skip button!")
                } else {
                    Log.v(TAG, "Ad not skippable yet.")
                }
            }

        } catch (error: Exception) {
            Log.e(TAG, "Oops, Something went wrong...", error)
        }
    }
}