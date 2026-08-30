package com.tranhienchuong.nomad.core.auth.firebase

import com.tranhienchuong.nomad.core.auth.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthFirebaseModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(implementation: FirebaseAuthRepository): AuthRepository
}
