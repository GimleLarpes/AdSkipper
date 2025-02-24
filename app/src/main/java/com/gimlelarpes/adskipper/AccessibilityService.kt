@file:Suppress("PrivatePropertyName")

package com.gimlelarpes.adskipper

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AdSkipperAccessibilityService: AccessibilityService() {

    private val TAG = "AdSkipperService"
    private val BASE = "com.google.android.youtube:id/"
    private val MODERN = "modern_"
    private val BSTATE = "fab_container" // Not perfect, also triggers on normal miniplayer
    private val SKIP_AD_BUTTON = "skip_ad_button"
    private val SKIP_AD_BUTTON_MINIPLAYER = "miniplayer_skip_ad_button"
    private val CLOSE_AD_PANEL_BUTTON_ID = "" // CLOSE PANEL BUTTON RESOURCE ID (doesn't currently exist)
    private val AD_PROGRESS_TEXT = "ad_progress_text"
    private val AD_BADGE_MINIPLAYER = "miniplayer_ad_badge"

    private lateinit var dataStoreManager: SettingsDataStoreManager
    private lateinit var audioManager: AudioManager

    private var _isAudioMuted = MutableStateFlow(false)
    private var _isServiceRunning = MutableStateFlow(false)
    var serviceRunningFlow: Flow<Boolean> = _isServiceRunning.asStateFlow()

    companion object {
        private var instance: AdSkipperAccessibilityService? = null
        fun getInstance (): AdSkipperAccessibilityService? = instance

        private var lastEventTime: Long = 0
        private var adVisible: Boolean = false
        private var bStateOld: Boolean = false
    }

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

        // Set timeout
        var info = instance!!.serviceInfo
        info.notificationTimeout = getNotificationTimeout()
        instance!!.serviceInfo = info

        Log.i(TAG, "onServiceConnected fired")
        super.onServiceConnected()
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
    private fun getNotificationTimeout(): Long {
        return runBlocking {
            dataStoreManager.notificationTimeout.first()
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


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        try {
            // Throttling onAccessibilityEvent calls
            val currentEventTime = event.eventTime
            if (currentEventTime - lastEventTime < serviceInfo.notificationTimeout || !isAdSkipEnabled()) {
                return
            }
            lastEventTime = currentEventTime


            // Target elements
            val miniPlayer = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$MODERN$AD_BADGE_MINIPLAYER")?.getOrNull(0)
            val adProgressText = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$AD_PROGRESS_TEXT")?.getOrNull(0)

            // This is absolutely horrid - the button doesn't have an ID TODO: Find panel ad close button (in a less horrible way)
            var adClosePanelButton = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}engagement_panel")?.getOrNull(0) //rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$CLOSE_AD_PANEL_BUTTON_ID")?.getOrNull(0)
            if (adClosePanelButton != null) {
                adClosePanelButton =
                    getChildFromPath(adClosePanelButton, "001000001") ?: // Website
                    getChildFromPath(adClosePanelButton, "000000001") // App
            }


            //DEBUG STUFF
            //
            //DebugTools.scanFromXML(R.raw.id_scan_list, BASE, applicationContext, rootInActiveWindow)
            val test = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}modern_miniplayer_close")?.getOrNull(0) // Workaround
            if (test != null) {
                val tag = "engagement_panel"
                Log.i(tag, "EXISTS")

                //DebugTools.scanClickable(test)

                /*val path = Website:"001000001", App:"000000001"
                var target = DebugTools.getChildFromPath(test, path)
                Log.w(tag, "idx $path :: ${target?.viewIdResourceName} - text: ${target?.text} - desc: ${target?.contentDescription} - isClickable: ${target?.isClickable}")
                if (target?.isClickable==true) {
                    target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }*/
            }


            // Visibility check
            if (adProgressText==null && miniPlayer==null && adClosePanelButton==null) {
                // Detect in-between state, TODO: Get in-between in one call
                val bState = (rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$BSTATE")?.getOrNull(0)!=null && rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}modern_miniplayer_close")?.getOrNull(0)==null)
                if (adVisible && (bState || bStateOld)) {
                    bStateOld = bState
                    return
                }
                bStateOld = bState // bState lags behind, this is to prevent audio leaking

                unMuteMedia()
                adVisible = false
                Log.v(TAG, "No ads visible.")
                return

            } else if (adProgressText==null && miniPlayer==null) {
                closeAdPanel(adClosePanelButton)
                return

            } else {
                // Video ad is visible, get adskip button
                val adSkipButton = if (miniPlayer == null) {
                    // Normal - I'm guessing they're switching things up currently and will move to modern adskipbutton
                    rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$SKIP_AD_BUTTON")?.getOrNull(0) ?: rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$MODERN$SKIP_AD_BUTTON")?.getOrNull(0)

                } else {
                    //Miniplayer
                    rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$MODERN$SKIP_AD_BUTTON_MINIPLAYER")?.getOrNull(0)
                }
                Log.v(TAG, "Ad is visible, attempting to skip...")
                adVisible = true

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
            Log.e(TAG, R.string.error_generic.toString(), error)
        }
    }
}

private fun getChildFromPath(parent: AccessibilityNodeInfo?, path: String): AccessibilityNodeInfo? {
    // Construct tree
    var steps = arrayListOf<Int>()
    for (n in path) {
        steps.add(n.digitToInt())
    }

    // Traverse tree
    var currentParent = parent
    try {
        if (currentParent != null) {
            for (i in steps) {
                // Prevent error spam by exiting early
                if (i > currentParent!!.childCount - 1) {
                    return null
                }

                currentParent = currentParent.getChild(i)
            }
        }

    } catch (error: Exception) {
        Log.e("getChildFromPath", R.string.error_outdated.toString(), error)
        return null
    }
    return currentParent
}