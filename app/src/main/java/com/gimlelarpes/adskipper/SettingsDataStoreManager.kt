package com.gimlelarpes.adskipper

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

private object Keys {
    val SETTINGS_ENABLE_AD_SKIPPER_SERVICE = booleanPreferencesKey("enable_ad_skipper_service")
}


class SettingsDataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    val enableAdSkipperService: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_SKIPPER_SERVICE] ?: false
        }

    suspend fun setEnableAdSkipperService(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SETTINGS_ENABLE_AD_SKIPPER_SERVICE] = value
        }
    }
}