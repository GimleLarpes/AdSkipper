package com.gimlelarpes.adskipper

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

private object Keys {
    val SETTINGS_ENABLE_AD_SKIPPER_SERVICE = booleanPreferencesKey("enable_ad_skipper_service")
    val SETTINGS_ENABLE_AD_MUTE = booleanPreferencesKey("enable_ad_mute")
    val SETTINGS_NOTIFICATION_TIMEOUT = longPreferencesKey("notification_timeout")
}


class SettingsDataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    // Get values
    val enableAdSkipperService: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_SKIPPER_SERVICE] == true
        }
    val enableAdMute: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_MUTE] != false
        }
    val notificationTimeout: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[Keys.SETTINGS_NOTIFICATION_TIMEOUT] ?: 100
        }

    // Edit values
    suspend fun setEnableAdSkipperService(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_SKIPPER_SERVICE] = value
        }
    }
    suspend fun setEnableAdMute(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_MUTE] = value
        }
    }
    suspend fun setNotificationTimeout(value: Long) {
        dataStore.edit { preferences ->
            preferences[Keys.SETTINGS_NOTIFICATION_TIMEOUT] = value
        }
    }
}