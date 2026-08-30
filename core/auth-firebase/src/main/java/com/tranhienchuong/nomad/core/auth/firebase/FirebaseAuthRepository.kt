package com.tranhienchuong.nomad.core.auth.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.tranhienchuong.nomad.core.auth.AuthFailure
import com.tranhienchuong.nomad.core.auth.AuthRepository
import com.tranhienchuong.nomad.core.auth.AuthResult
import com.tranhienchuong.nomad.core.auth.AuthSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): AuthResult<AuthSession> = withAuth { auth ->
        auth.signInWithEmailAndPassword(email, password).await().user.toAuthSession()
    }

    override suspend fun signUp(
        displayName: String,
        email: String,
        password: String,
    ): AuthResult<AuthSession> = withAuth { auth ->
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
            ?: throw IllegalStateException("Firebase did not return a user after registration.")

        // Account creation is the authentication operation. A profile update is best effort so a
        // transient failure cannot turn a real session into a false failure.
        runCatching {
            user.updateProfile(userProfileChangeRequest { this.displayName = displayName }).await()
        }

        user.toAuthSession()
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<AuthSession> = withAuth { auth ->
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await().user.toAuthSession()
    }

    override suspend fun sendPasswordReset(email: String): AuthResult<Unit> = withAuth { auth ->
        auth.sendPasswordResetEmail(email).await()
        Unit
    }

    override suspend fun currentSession(): AuthResult<AuthSession?> = withAuth { auth ->
        auth.currentUser?.toAuthSession()
    }

    private suspend fun <T> withAuth(block: suspend (FirebaseAuth) -> T): AuthResult<T> {
        val auth = try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                return AuthResult.Failure(AuthFailure.ServiceUnavailable)
            }
            FirebaseAuth.getInstance()
        } catch (_: Exception) {
            return AuthResult.Failure(AuthFailure.ServiceUnavailable)
        }

        return try {
            AuthResult.Success(block(auth))
        } catch (exception: Exception) {
            AuthResult.Failure(exception.toAuthFailure())
        }
    }
}

private fun FirebaseUser?.toAuthSession(): AuthSession {
    val user = this ?: throw IllegalStateException("Firebase did not return an authenticated user.")
    return AuthSession(
        userId = user.uid,
        email = user.email,
        displayName = user.displayName,
    )
}

private fun Exception.toAuthFailure(): AuthFailure = when (this) {
    is FirebaseAuthInvalidCredentialsException -> AuthFailure.InvalidCredential
    is FirebaseAuthInvalidUserException -> AuthFailure.UserNotFound
    is FirebaseAuthUserCollisionException -> AuthFailure.EmailAlreadyInUse
    is FirebaseAuthWeakPasswordException -> AuthFailure.WeakPassword
    is FirebaseNetworkException -> AuthFailure.NetworkUnavailable
    else -> AuthFailure.Unknown
}
