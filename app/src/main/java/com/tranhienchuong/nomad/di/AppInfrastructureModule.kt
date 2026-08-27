package com.tranhienchuong.nomad.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tranhienchuong.nomad.core.datastore.nomadDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppInfrastructureModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.nomadDataStore

    @Provides
    @Singleton
    fun provideNomadPreferencesRepository(dataStore: DataStore<Preferences>): com.tranhienchuong.nomad.core.datastore.NomadPreferencesRepository =
        com.tranhienchuong.nomad.core.datastore.NomadPreferencesRepository(dataStore)
}
