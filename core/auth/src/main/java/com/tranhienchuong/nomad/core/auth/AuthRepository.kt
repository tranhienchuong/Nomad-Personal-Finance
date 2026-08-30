package com.tranhienchuong.nomad.core.auth

/**
 * The auth seam used by presentation. Implementations must never report a session unless the
 * identity provider has authenticated it successfully.
 */
interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResult<AuthSession>

    suspend fun signUp(
        displayName: String,
        email: String,
        password: String,
    ): AuthResult<AuthSession>

    suspend fun signInWithGoogle(idToken: String): AuthResult<AuthSession>

    suspend fun sendPasswordReset(email: String): AuthResult<Unit>

    suspend fun currentSession(): AuthResult<AuthSession?>
}

data class AuthSession(
    val userId: String,
    val email: String?,
    val displayName: String?,
)

sealed interface AuthResult<out T> {
    data class Success<T>(val value: T) : AuthResult<T>

    data class Failure(val error: AuthFailure) : AuthResult<Nothing>
}

sealed interface AuthFailure {
    data object InvalidCredential : AuthFailure

    data object UserNotFound : AuthFailure

    data object EmailAlreadyInUse : AuthFailure

    data object WeakPassword : AuthFailure

    data object NetworkUnavailable : AuthFailure

    data object ServiceUnavailable : AuthFailure

    data object Unknown : AuthFailure
}
