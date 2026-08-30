package com.tranhienchuong.nomad.feature.auth

import com.tranhienchuong.nomad.core.auth.AuthFailure
import com.tranhienchuong.nomad.core.auth.AuthRepository
import com.tranhienchuong.nomad.core.auth.AuthResult
import com.tranhienchuong.nomad.core.auth.AuthSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun signIn_whenRepositoryAuthenticates_emitsNavigation() = runTest(dispatcher) {
        val viewModel = AuthViewModel(
            FakeAuthRepository(
                signInResult = AuthResult.Success(
                    AuthSession(userId = "user-1", email = "user@nomad.com", displayName = null),
                ),
            ),
        )
        val effect = async { viewModel.effects.first() }

        viewModel.onSignInEmailChanged("user@nomad.com")
        viewModel.onSignInPasswordChanged("correct-password")
        viewModel.signIn()
        advanceUntilIdle()

        assertEquals(AuthEffect.NavigateToMain, effect.await())
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun signIn_whenFirebaseIsUnavailable_showsErrorWithoutNavigation() = runTest(dispatcher) {
        val viewModel = AuthViewModel(
            FakeAuthRepository(
                signInResult = AuthResult.Failure(AuthFailure.ServiceUnavailable),
            ),
        )

        viewModel.onSignInEmailChanged("user@nomad.com")
        viewModel.onSignInPasswordChanged("correct-password")
        viewModel.signIn()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            "Dịch vụ xác thực hiện chưa sẵn sàng. Vui lòng thử lại sau.",
            viewModel.uiState.value.errorMessage,
        )
    }

    @Test
    fun sendPasswordReset_whenRequestFails_doesNotShowSuccessMessage() = runTest(dispatcher) {
        val viewModel = AuthViewModel(
            FakeAuthRepository(
                passwordResetResult = AuthResult.Failure(AuthFailure.NetworkUnavailable),
            ),
        )

        viewModel.setForgotPasswordSheetOpen(true)
        viewModel.onForgotPasswordEmailChanged("user@nomad.com")
        viewModel.sendPasswordReset()
        advanceUntilIdle()

        assertEquals(
            "Không thể kết nối mạng. Vui lòng kiểm tra Internet và thử lại.",
            viewModel.uiState.value.errorMessage,
        )
        assertNull(viewModel.uiState.value.forgotPasswordSuccessMessage)
        assertEquals(true, viewModel.uiState.value.isForgotPasswordSheetOpen)
    }
}

private class FakeAuthRepository(
    private val signInResult: AuthResult<AuthSession> = AuthResult.Failure(AuthFailure.Unknown),
    private val signUpResult: AuthResult<AuthSession> = AuthResult.Failure(AuthFailure.Unknown),
    private val googleResult: AuthResult<AuthSession> = AuthResult.Failure(AuthFailure.Unknown),
    private val passwordResetResult: AuthResult<Unit> = AuthResult.Failure(AuthFailure.Unknown),
    private val sessionResult: AuthResult<AuthSession?> = AuthResult.Success(null),
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): AuthResult<AuthSession> = signInResult

    override suspend fun signUp(
        displayName: String,
        email: String,
        password: String,
    ): AuthResult<AuthSession> = signUpResult

    override suspend fun signInWithGoogle(idToken: String): AuthResult<AuthSession> = googleResult

    override suspend fun sendPasswordReset(email: String): AuthResult<Unit> = passwordResetResult

    override suspend fun currentSession(): AuthResult<AuthSession?> = sessionResult
}
