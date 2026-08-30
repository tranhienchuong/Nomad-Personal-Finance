package com.tranhienchuong.nomad.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tranhienchuong.nomad.core.datastore.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface OnboardingEffect {
    data object NavigateToAuth : OnboardingEffect
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _effects = Channel<OnboardingEffect>(capacity = Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private var isCompleting = false

    fun finishOnboarding() {
        if (isCompleting) return
        isCompleting = true

        viewModelScope.launch {
            try {
                onboardingRepository.setOnboardingCompleted(true)
                _effects.send(OnboardingEffect.NavigateToAuth)
            } catch (_: Exception) {
                isCompleting = false
            }
        }
    }
}
