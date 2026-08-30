package com.tranhienchuong.nomad.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tranhienchuong.nomad.core.auth.AuthFailure
import com.tranhienchuong.nomad.core.auth.AuthRepository
import com.tranhienchuong.nomad.core.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthTab {
    SignIn,
    SignUp,
}

data class AuthUiState(
    val selectedTab: AuthTab = AuthTab.SignIn,

    // Sign In Fields
    val signInEmail: String = "",
    val signInEmailError: String? = null,
    val signInPassword: String = "",
    val signInPasswordError: String? = null,

    // Sign Up Fields
    val signUpFullName: String = "",
    val signUpFullNameError: String? = null,
    val signUpEmail: String = "",
    val signUpEmailError: String? = null,
    val signUpPassword: String = "",
    val signUpPasswordError: String? = null,
    val signUpConfirmPassword: String = "",
    val signUpConfirmPasswordError: String? = null,

    // Forgot Password Fields
    val forgotPasswordEmail: String = "",
    val forgotPasswordEmailError: String? = null,
    val isForgotPasswordSheetOpen: Boolean = false,
    val forgotPasswordSuccessMessage: String? = null,

    // General state
    val isEmailLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isLoading: Boolean get() = isEmailLoading || isGoogleLoading
}

sealed interface AuthEffect {
    data object NavigateToMain : AuthEffect

    data object RequestGoogleSignIn : AuthEffect
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _effects = Channel<AuthEffect>(capacity = Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun selectTab(tab: AuthTab) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null) }
    }

    fun onSignInEmailChanged(email: String) {
        val error = if (_uiState.value.signInEmailError != null) {
            AuthValidators.validateEmail(email).errorMessage
        } else {
            null
        }
        _uiState.update { it.copy(signInEmail = email, signInEmailError = error) }
    }

    fun onSignInPasswordChanged(password: String) {
        val error = if (_uiState.value.signInPasswordError != null) {
            AuthValidators.validatePassword(password).errorMessage
        } else {
            null
        }
        _uiState.update { it.copy(signInPassword = password, signInPasswordError = error) }
    }

    fun onSignUpFullNameChanged(fullName: String) {
        val error = if (_uiState.value.signUpFullNameError != null) {
            AuthValidators.validateFullName(fullName).errorMessage
        } else {
            null
        }
        _uiState.update { it.copy(signUpFullName = fullName, signUpFullNameError = error) }
    }

    fun onSignUpEmailChanged(email: String) {
        val error = if (_uiState.value.signUpEmailError != null) {
            AuthValidators.validateEmail(email).errorMessage
        } else {
            null
        }
        _uiState.update { it.copy(signUpEmail = email, signUpEmailError = error) }
    }

    fun onSignUpPasswordChanged(password: String) {
        val error = if (_uiState.value.signUpPasswordError != null) {
            AuthValidators.validatePassword(password).errorMessage
        } else {
            null
        }
        _uiState.update { it.copy(signUpPassword = password, signUpPasswordError = error) }
    }

    fun onSignUpConfirmPasswordChanged(confirmPassword: String) {
        val error = if (_uiState.value.signUpConfirmPasswordError != null) {
            AuthValidators.validateConfirmPassword(_uiState.value.signUpPassword, confirmPassword).errorMessage
        } else {
            null
        }
        _uiState.update { it.copy(signUpConfirmPassword = confirmPassword, signUpConfirmPasswordError = error) }
    }

    fun onForgotPasswordEmailChanged(email: String) {
        val error = if (_uiState.value.forgotPasswordEmailError != null) {
            AuthValidators.validateEmail(email).errorMessage
        } else {
            null
        }
        _uiState.update { it.copy(forgotPasswordEmail = email, forgotPasswordEmailError = error) }
    }

    fun setForgotPasswordSheetOpen(isOpen: Boolean) {
        _uiState.update {
            it.copy(
                isForgotPasswordSheetOpen = isOpen,
                forgotPasswordEmail = if (isOpen) it.signInEmail else "",
                forgotPasswordEmailError = null,
                forgotPasswordSuccessMessage = null,
            )
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearForgotPasswordSuccessMessage() {
        _uiState.update { it.copy(forgotPasswordSuccessMessage = null) }
    }

    fun signIn() {
        if (_uiState.value.isLoading) return

        val state = _uiState.value
        val emailValidation = AuthValidators.validateEmail(state.signInEmail)
        val passwordValidation = AuthValidators.validatePassword(state.signInPassword)

        if (!emailValidation.isValid || !passwordValidation.isValid) {
            _uiState.update {
                it.copy(
                    signInEmailError = emailValidation.errorMessage,
                    signInPasswordError = passwordValidation.errorMessage,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isEmailLoading = true, errorMessage = null) }
            when (val result = safely { authRepository.signIn(state.signInEmail.trim(), state.signInPassword) }) {
                is AuthResult.Success -> completeAuthentication()
                is AuthResult.Failure -> showFailure(result.error, isGoogle = false)
            }
        }
    }

    fun signUp() {
        if (_uiState.value.isLoading) return

        val state = _uiState.value
        val nameValidation = AuthValidators.validateFullName(state.signUpFullName)
        val emailValidation = AuthValidators.validateEmail(state.signUpEmail)
        val passwordValidation = AuthValidators.validatePassword(state.signUpPassword)
        val confirmPasswordValidation = AuthValidators.validateConfirmPassword(
            state.signUpPassword,
            state.signUpConfirmPassword,
        )

        if (!nameValidation.isValid || !emailValidation.isValid ||
            !passwordValidation.isValid || !confirmPasswordValidation.isValid
        ) {
            _uiState.update {
                it.copy(
                    signUpFullNameError = nameValidation.errorMessage,
                    signUpEmailError = emailValidation.errorMessage,
                    signUpPasswordError = passwordValidation.errorMessage,
                    signUpConfirmPasswordError = confirmPasswordValidation.errorMessage,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isEmailLoading = true, errorMessage = null) }
            when (
                val result = safely {
                    authRepository.signUp(
                        displayName = state.signUpFullName.trim(),
                        email = state.signUpEmail.trim(),
                        password = state.signUpPassword,
                    )
                }
            ) {
                is AuthResult.Success -> completeAuthentication()
                is AuthResult.Failure -> showFailure(result.error, isGoogle = false)
            }
        }
    }

    fun sendPasswordReset() {
        if (_uiState.value.isLoading) return

        val state = _uiState.value
        val emailValidation = AuthValidators.validateEmail(state.forgotPasswordEmail)
        if (!emailValidation.isValid) {
            _uiState.update { it.copy(forgotPasswordEmailError = emailValidation.errorMessage) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isEmailLoading = true, errorMessage = null) }
            when (val result = safely { authRepository.sendPasswordReset(state.forgotPasswordEmail.trim()) }) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isEmailLoading = false,
                            isForgotPasswordSheetOpen = false,
                            forgotPasswordSuccessMessage = "Liên kết đặt lại mật khẩu đã được gửi đến email!",
                        )
                    }
                }
                is AuthResult.Failure -> showFailure(result.error, isGoogle = false)
            }
        }
    }

    fun requestGoogleSignIn() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isGoogleLoading = true, errorMessage = null) }
        _effects.trySend(AuthEffect.RequestGoogleSignIn)
    }

    fun signInWithGoogle(idToken: String) {
        if (_uiState.value.isEmailLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isGoogleLoading = true, errorMessage = null) }
            when (val result = safely { authRepository.signInWithGoogle(idToken) }) {
                is AuthResult.Success -> completeAuthentication()
                is AuthResult.Failure -> showFailure(result.error, isGoogle = true)
            }
        }
    }

    fun showGoogleSignInError(message: String) {
        _uiState.update { it.copy(isGoogleLoading = false, errorMessage = message) }
    }

    fun onGoogleSignInCancelled() {
        _uiState.update { it.copy(isGoogleLoading = false) }
    }

    private suspend fun <T> safely(block: suspend () -> AuthResult<T>): AuthResult<T> = try {
        block()
    } catch (_: Exception) {
        AuthResult.Failure(AuthFailure.Unknown)
    }

    private suspend fun completeAuthentication() {
        _uiState.update {
            it.copy(
                isEmailLoading = false,
                isGoogleLoading = false,
            )
        }
        _effects.send(AuthEffect.NavigateToMain)
    }

    private fun showFailure(failure: AuthFailure, isGoogle: Boolean) {
        _uiState.update {
            it.copy(
                isEmailLoading = if (isGoogle) it.isEmailLoading else false,
                isGoogleLoading = if (isGoogle) false else it.isGoogleLoading,
                errorMessage = failure.toMessage(),
            )
        }
    }
}

private fun AuthFailure.toMessage(): String = when (this) {
    AuthFailure.InvalidCredential -> "Thông tin đăng nhập không chính xác hoặc đã hết hạn."
    AuthFailure.UserNotFound -> "Tài khoản không tồn tại trong hệ thống."
    AuthFailure.EmailAlreadyInUse -> "Email này đã được sử dụng cho một tài khoản khác."
    AuthFailure.WeakPassword -> "Mật khẩu không đủ mạnh. Vui lòng dùng ít nhất 6 ký tự."
    AuthFailure.NetworkUnavailable -> "Không thể kết nối mạng. Vui lòng kiểm tra Internet và thử lại."
    AuthFailure.ServiceUnavailable -> "Dịch vụ xác thực hiện chưa sẵn sàng. Vui lòng thử lại sau."
    AuthFailure.Unknown -> "Đã có lỗi xảy ra. Vui lòng thử lại sau."
}
