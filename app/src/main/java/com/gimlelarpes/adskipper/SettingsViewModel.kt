package com.gimlelarpes.adskipper

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    var adSkipEnabled = mutableStateOf(false)
        private set

    fun toggleAdSkip() {
        adSkipEnabled.value = !adSkipEnabled.value
    }

    fun showLicenses() {
        //Open window with licenses.txt
    }
}