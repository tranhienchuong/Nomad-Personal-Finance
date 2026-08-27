package com.tranhienchuong.nomad.core.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.nomadDataStore by preferencesDataStore(name = "nomad_preferences")
