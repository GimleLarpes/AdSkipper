package com.gimlelarpes.adskipper

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

private object Keys {
    val SETTINGS_ENABLE_AD_SKIPPER_SERVICE = booleanPreferencesKey("enable_ad_skipper_service")
    val SETTINGS_ENABLE_AD_MUTE = booleanPreferencesKey("enable_ad_mute")
}


class SettingsDataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    // Get values
    val enableAdSkipperService: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_SKIPPER_SERVICE] ?: false
        }

    val enableAdMute: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_MUTE] ?: true
        }

    // Edit values
    suspend fun setEnableAdSkipperService(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_SKIPPER_SERVICE] = value
        }
    }
    suspend fun setEnableAdMute(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_SKIPPER_SERVICE] = value
        }
    }
}