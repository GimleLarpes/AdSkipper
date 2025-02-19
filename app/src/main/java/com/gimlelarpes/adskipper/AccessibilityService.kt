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

    private val TAG = "AdSkipperService"
    private val BASE = "com.google.android.youtube:id/"
    private val MODERN = "modern_"
    private val SKIP_AD_BUTTON = "skip_ad_button"
    private val SKIP_AD_BUTTON_MINIPLAYER = "miniplayer_skip_ad_button"
    private val CLOSE_AD_PANEL_BUTTON_ID = "NOTHERE" //WHAT IS THE REAL RESOURCE ID?
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

    // TODO: Find panel ad close button (in a less horrible way)
    // TODO: Detect Ads when in inbetween state

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

            // This is absolutely horrid - maybe reverseengineer a better way using this info though?
            var adClosePanelButton = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}engagement_panel")?.getOrNull(0) //rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$BASE$CLOSE_AD_PANEL_BUTTON_ID")?.getOrNull(0) // UNKNOWN
            if (adClosePanelButton!=null) {
                adClosePanelButton = getChildFromPath(adClosePanelButton, "001000001")
            }

            // Other ad-related elements
            //val test = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("$$BASE$MODERN$SKIP_AD_BUTTON_MINIPLAYER")?.getOrNull(0)
            //val test2 = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}engagement_close_button")?.getOrNull(0)
            //val test3 = rootInActiveWindow?.findAccessibilityNodeInfosByViewId("${BASE}engagement_panel")?.getOrNull(0) // THIS TRIGGERS, too bad it's a huge class


            // Panels that aren't it (retest, since stuff broke before)
            // default_promo_panel
            // default_promo_panel_modern_type
            // app_engagement_panel

            //// main_companion_container
            // ad_companion_card
            // app_promotion_companion_card
            // compact_companion_card
            // multi_item_companion_card
            // shopping_companion_card
            // element_companion_card
            // suggested_videos_companion_card

            //// promoted_sparkles_text_ctd_home_themed_cta_compact_form_landscape
            // muted_ad_view
            // click_overlay
            // cta_button_wrapper
            // promoted_cta_button_horizontal_fill_wrapper
            // price
            // rating
            // rating_text
            // ad_attribution
            // ratings_container
            // description
            // description_container
            // title
            // title_frame
            // close_button_or_contextual_menu_anchor_home
            // title_row
            // overlay_badge_layout
            // icon
            // second_thumbnail
            // thumbnail
            // thumbnail_wrapper
            // inner_background
            // content_background
            // content_layout
            // ad_view

            // ad_cta_button
            // ad_disclosure_banner_navigate_arrow
            // ad_disclosure_banner_side_bar
            // ad_disclosure_container
            // collapse_cta_container
            // collapsible_ad_cta_overlay
            // dismiss_button
            // dislike_button

            //DEBUG STUFF
            /*if (test != null) {
                Log.w(TAG, "miniplayer_ad_skip THING EXISTS") // Can indeed exist, doesn't seem to always work
            }*/
            /*if (test2 != null) {
                Log.w(TAG, "ELEMENT EXISTS")
                if (test2.isClickable) {
                    Log.w(TAG, "ELEMENT CLICKABLE")
                }
            }
            if (test3 != null) { // Traverse Children
                val tag = "engagement_panel"
                Log.i(tag, "EXISTS") // This class is used to display the panel ads (and all other panels), works (not what i need tho)

                //scanClickable(test3, "") // Returns correct input, problem lies in getChildFromPath

                val path = "001000001"
                var target = getChildFromPath(test3, path)
                val name = target?.viewIdResourceName
                val isClickable = target?.isClickable
                val text = target?.text
                val description = target?.contentDescription
                Log.w(tag, "idx $path :: $name - text: $text - desc: $description - isClickable: $isClickable")
                if (target?.isClickable==true) {
                    target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }

            }*/


            // Visibility check
            if (adProgressText==null && miniPlayer==null && adClosePanelButton==null) {
                unMuteMedia()
                Log.v(TAG, "No ads visible.")
                return

            } else if (adProgressText==null && miniPlayer==null) {
                closeAdPanel(adClosePanelButton)
                unMuteMedia()
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

// Debug functions

fun traverseChild(parent: AccessibilityNodeInfo) {
    val tag = parent.viewIdResourceName
    val nChildren = parent.childCount

    for (i in 0..nChildren-1) {
        val child = parent.getChild(i)

        val name = child.viewIdResourceName
        val isClickable = child.isClickable
        val text = child.text
        val description = child.contentDescription
        Log.i(tag, "idx $i :: $name - text: $text - desc: $description - isClickable: $isClickable")
    }
}
fun scanClickable(origin: AccessibilityNodeInfo, path: String) {
    val tag = origin.viewIdResourceName
    val nChildren = origin.childCount

    for (i in 0..nChildren-1) {

        val child = origin.getChild(i)
        val newPath = path + i.toString() // Trace steps taken

        val isClickable = child.isClickable
        if (isClickable) {
            val name = child.viewIdResourceName
            val text = child.text
            val description = child.contentDescription
            Log.i(
                tag,
                "pth $newPath :: $name - text: $text - desc: $description - isClickable: $isClickable"
            )
        } else {
            val name = child.viewIdResourceName
            val text = child.text
            val description = child.contentDescription
            Log.v(
                tag,
                "pth $newPath :: $name - text: $text - desc: $description - isClickable: $isClickable"
            )
        }
        scanClickable(child, newPath)
    }

}
fun getChildFromPath(parent: AccessibilityNodeInfo, path: String): AccessibilityNodeInfo? {
    // Construct tree
    var steps = arrayListOf<Int>()
    for (n in path) {
        steps.add(n.digitToInt())
    }

    // Traverse tree
    var currentParent = parent
    try {
        for (i in steps) {
            // Prevent error spam by exiting early
            if (i > currentParent.childCount-1) { return null }

            currentParent = currentParent.getChild(i)
        }

    } catch (error: Exception) {
        Log.e("getChildFromPath", R.string.error_invalid_pth.toString(), error)
        return null
    }
    return currentParent
}
