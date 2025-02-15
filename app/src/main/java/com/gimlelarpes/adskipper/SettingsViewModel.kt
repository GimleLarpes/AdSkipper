package com.gimlelarpes.adskipper

import android.R
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ServiceInfo
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class SettingsViewModel(
    private val dataStoreManager: SettingsDataStoreManager,
    private val application: Application
): ViewModel() {

    val applicationContext: Context = application

    private val service: AdSkipperAccessibilityService? get() {
        return AdSkipperAccessibilityService.getInstance()
    }


    // Update settings
    fun setEnableAdSkipperService(value: Boolean) {
        viewModelScope.launch() {
            dataStoreManager.setEnableAdSkipperService(value)
        }
    }
    fun setEnableAdMute(value: Boolean) {
        viewModelScope.launch() {
            dataStoreManager.setEnableAdMute(value)
        }
    }


    //Check if service is supposed to be running
    val isAdSkipEnabledFlow: Flow<Boolean> = dataStoreManager.enableAdSkipperService
    val isAdMuteEnabledFlow: Flow<Boolean> = dataStoreManager.enableAdMute

    //Check if service is running
    @OptIn(ExperimentalCoroutinesApi::class)
    val isServiceRunningFlow: Flow<Boolean> = flow { emit(service) }.flatMapLatest { currentService -> currentService?.serviceRunningFlow ?: flowOf(false) }

    val isAccessibilityServiceRunning: Boolean get() {
        return getIsAccessibilityServiceRunning(applicationContext, AdSkipperAccessibilityService::class.java)
    }
    private fun getIsAccessibilityServiceRunning(context: Context?, serviceClass: Class<out AdSkipperAccessibilityService>): Boolean {
        val accessibilityManager = context?.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (service in enabledServices) {
            val serviceInfo: ServiceInfo = service.resolveInfo.serviceInfo
            if (serviceInfo.packageName == context.packageName && serviceInfo.name == serviceClass.name) {
                return true
            }
        }
        return false
    }


    // Enable Service
    fun enableAccessibilityService() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }
    fun disableAccessibilityService() {
        service?.disableSelf()
    }
}

class SettingsViewModelFactory(
    private val dataStoreManager: SettingsDataStoreManager,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                dataStoreManager,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}