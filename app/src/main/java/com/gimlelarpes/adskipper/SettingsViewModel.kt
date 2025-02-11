package com.gimlelarpes.adskipper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(
    private val dataStoreManager: SettingsDataStoreManager//INPUT PARAMS
): ViewModel() {


    //Toggle AdSkip setting
    /*suspend fun toggleAdSkip(context: Context) {
        dataStore.edit { settings ->
            val currentValue = settings[SETTINGS_ENABLE_SERVICE] ?: false
            settings[SETTINGS_ENABLE_SERVICE] = !currentValue
        }

        //Update service
        //updateServiceState()
    }*/

    fun updateServiceState() {
        //UPDATE SERVICE STATE
    }

    //Check if service is running
    //isServiceRunning


    // Read setting state using Flow (does not check if service is actually running), make it do that
    // use SettingsDataStoreManager.enableService
    val isAdSkipEnabledFlow: Flow<Boolean> = dataStoreManager.enableAdSkipperService /*dataStore.data
        .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[SETTINGS_ENABLE_SERVICE] ?: false
        }*/
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