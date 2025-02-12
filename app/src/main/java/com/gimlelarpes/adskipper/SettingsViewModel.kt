package com.gimlelarpes.adskipper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStoreManager: SettingsDataStoreManager
): ViewModel() {


    //Toggle AdSkip setting
    fun setEnableAdSkipperService(value: Boolean) {
        viewModelScope.launch() {
            dataStoreManager.setEnableAdSkipperService(value)
        }
    }

    fun updateServiceState() {
        //UPDATE SERVICE STATE
    }

    //Check if service is supposed to be running
    val isAdSkipEnabledFlow: Flow<Boolean> = dataStoreManager.enableAdSkipperService

    //Check if service is running
    val isServiceRunningFlow: Flow<Boolean> = flowOf(false) //current value is placeholder, it's supposed to be [IS SERVICE RUNNING FLOW]
}

class SettingsViewModelFactory(
    private val dataStoreManager: SettingsDataStoreManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                dataStoreManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}