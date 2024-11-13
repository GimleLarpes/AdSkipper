package com.gimlelarpes.adskipper.persistence

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val SETTINGS_ENABLE_SERVICE = booleanPreferencesKey(name = "enable-service")
    val NAME = stringPreferencesKey(name = "name")
}