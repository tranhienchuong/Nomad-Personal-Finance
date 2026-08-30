package com.tranhienchuong.nomad.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tranhienchuong.nomad.core.auth.AuthFailure
import com.tranhienchuong.nomad.core.auth.AuthRepository
import com.tranhienchuong.nomad.core.auth.AuthResult
import com.tranhienchuong.nomad.core.datastore.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StartupDestination {
    Main,
    Auth,
    Onboarding,
}

data class SplashUiState(
    val destination: StartupDestination? = null,
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun resolveDestination() {
        if (_uiState.value.destination != null) return

        viewModelScope.launch {
            val session = runCatching { authRepository.currentSession() }
                .getOrElse { AuthResult.Failure(AuthFailure.ServiceUnavailable) }
            val destination = when (session) {
                is AuthResult.Success -> {
                    if (session.value != null) {
                        StartupDestination.Main
                    } else {
                        onboardingDestination()
                    }
                }
                is AuthResult.Failure -> onboardingDestination()
            }
            _uiState.value = SplashUiState(destination)
        }
    }

    private suspend fun onboardingDestination(): StartupDestination {
        return if (runCatching { onboardingRepository.isOnboardingCompleted() }.getOrDefault(false)) {
            StartupDestination.Auth
        } else {
            StartupDestination.Onboarding
        }
    }
}
