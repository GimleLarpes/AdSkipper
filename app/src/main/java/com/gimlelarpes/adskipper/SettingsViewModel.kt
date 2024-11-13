package com.gimlelarpes.adskipper

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import com.gimlelarpes.adskipper.persistence.PreferenceKeys.SETTINGS_ENABLE_SERVICE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsViewModel(
    private val dataStore: DataStore<Preferences>
): ViewModel() {
    val adSkipEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SETTINGS_ENABLE_SERVICE] ?: false
    }
        //private set

    fun toggleAdSkip() {
        //adSkipEnabled = !adSkipEnabled
    }

    //Check if service is running
}