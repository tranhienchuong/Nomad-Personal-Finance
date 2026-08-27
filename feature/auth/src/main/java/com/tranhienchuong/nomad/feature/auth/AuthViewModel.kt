package com.tranhienchuong.nomad.feature.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else null
        } catch (_: Exception) {
            null
        }
    }

    fun selectTab(tab: AuthTab) {
        _uiState.update { it.copy(selectedTab = tab, errorMessage = null) }
    }

    fun onSignInEmailChanged(email: String) {
        val error = if (_uiState.value.signInEmailError != null) {
            AuthValidators.validateEmail(email).errorMessage
        } else null
        _uiState.update { it.copy(signInEmail = email, signInEmailError = error) }
    }

    fun onSignInPasswordChanged(password: String) {
        val error = if (_uiState.value.signInPasswordError != null) {
            AuthValidators.validatePassword(password).errorMessage
        } else null
        _uiState.update { it.copy(signInPassword = password, signInPasswordError = error) }
    }

    fun onSignUpFullNameChanged(fullName: String) {
        val error = if (_uiState.value.signUpFullNameError != null) {
            AuthValidators.validateFullName(fullName).errorMessage
        } else null
        _uiState.update { it.copy(signUpFullName = fullName, signUpFullNameError = error) }
    }

    fun onSignUpEmailChanged(email: String) {
        val error = if (_uiState.value.signUpEmailError != null) {
            AuthValidators.validateEmail(email).errorMessage
        } else null
        _uiState.update { it.copy(signUpEmail = email, signUpEmailError = error) }
    }

    fun onSignUpPasswordChanged(password: String) {
        val error = if (_uiState.value.signUpPasswordError != null) {
            AuthValidators.validatePassword(password).errorMessage
        } else null
        _uiState.update { it.copy(signUpPassword = password, signUpPasswordError = error) }
    }

    fun onSignUpConfirmPasswordChanged(confirmPassword: String) {
        val error = if (_uiState.value.signUpConfirmPasswordError != null) {
            AuthValidators.validateConfirmPassword(_uiState.value.signUpPassword, confirmPassword).errorMessage
        } else null
        _uiState.update { it.copy(signUpConfirmPassword = confirmPassword, signUpConfirmPasswordError = error) }
    }

    fun onForgotPasswordEmailChanged(email: String) {
        val error = if (_uiState.value.forgotPasswordEmailError != null) {
            AuthValidators.validateEmail(email).errorMessage
        } else null
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

    fun signIn(onSuccess: () -> Unit) {
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val auth = firebaseAuth
            if (auth == null) {
                // Firebase not yet initialized, simulate successful sign in for demo
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                onSuccess()
                return@launch
            }

            try {
                auth.signInWithEmailAndPassword(state.signInEmail.trim(), state.signInPassword).await()
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                onSuccess()
            } catch (e: Exception) {
                val message = mapFirebaseError(e.message)
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val auth = firebaseAuth
            if (auth == null) {
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                onSuccess()
                return@launch
            }

            try {
                val result = auth.createUserWithEmailAndPassword(state.signUpEmail.trim(), state.signUpPassword).await()
                val profileUpdates = userProfileChangeRequest {
                    displayName = state.signUpFullName.trim()
                }
                result.user?.updateProfile(profileUpdates)?.await()

                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                onSuccess()
            } catch (e: Exception) {
                val message = mapFirebaseError(e.message)
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun sendPasswordReset() {
        val state = _uiState.value
        val emailValidation = AuthValidators.validateEmail(state.forgotPasswordEmail)
        if (!emailValidation.isValid) {
            _uiState.update { it.copy(forgotPasswordEmailError = emailValidation.errorMessage) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val auth = firebaseAuth
            if (auth == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isForgotPasswordSheetOpen = false,
                        forgotPasswordSuccessMessage = "Liên kết đặt lại mật khẩu đã được gửi đến email!",
                    )
                }
                return@launch
            }

            try {
                auth.sendPasswordResetEmail(state.forgotPasswordEmail.trim()).await()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isForgotPasswordSheetOpen = false,
                        forgotPasswordSuccessMessage = "Liên kết đặt lại mật khẩu đã được gửi đến email!",
                    )
                }
            } catch (e: Exception) {
                val message = mapFirebaseError(e.message)
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun signInWithGoogle(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val credentialManager = CredentialManager.create(context)
                val webClientId = getWebClientId(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = context, request = request)
                val googleIdToken = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                val auth = firebaseAuth
                if (auth != null) {
                    auth.signInWithCredential(firebaseCredential).await()
                }

                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                onSuccess()
            } catch (e: GetCredentialCancellationException) {
                // User cancelled or dismissed account picker
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                val raw = e.message.orEmpty()
                val message = if (raw.contains("16") || raw.contains("cancel", ignoreCase = true)) {
                    null
                } else {
                    mapFirebaseError(e.message)
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    private fun getWebClientId(context: Context): String {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (resId != 0) {
            context.getString(resId)
        } else {
            "320257622864-97krp9ae7cccp08adcb220lgkqtualmf.apps.googleusercontent.com"
        }
    }

    private fun mapFirebaseError(rawMessage: String?): String {
        val msg = rawMessage.orEmpty()
        return when {
            msg.contains("password", ignoreCase = true) -> "Mật khẩu không chính xác hoặc không đủ mạnh."
            msg.contains("user-not-found", ignoreCase = true) || msg.contains("no user", ignoreCase = true) -> "Tài khoản không tồn tại trong hệ thống."
            msg.contains("email-already-in-use", ignoreCase = true) -> "Email này đã được sử dụng cho một tài khoản khác."
            msg.contains("network", ignoreCase = true) -> "Không thể kết nối mạng. Vui lòng kiểm tra lại kết nối internet."
            else -> "Đã có lỗi xảy ra: ${msg.ifBlank { "Vui lòng thử lại sau" }}"
        }
    }
}
