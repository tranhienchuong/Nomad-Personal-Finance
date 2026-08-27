package com.tranhienchuong.nomad.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.nomadDataStore: DataStore<Preferences> by preferencesDataStore(name = "nomad_preferences")

object NomadPreferencesKeys {
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
}

class NomadPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NomadPreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[NomadPreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}
