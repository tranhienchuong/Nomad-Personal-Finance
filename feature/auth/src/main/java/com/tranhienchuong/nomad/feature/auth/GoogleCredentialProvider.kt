package com.tranhienchuong.nomad.feature.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

sealed interface GoogleCredentialResult {
    data class Success(val idToken: String) : GoogleCredentialResult

    data object Cancelled : GoogleCredentialResult

    data class Failure(val message: String) : GoogleCredentialResult
}

/** Android presentation adapter for the Google account picker. */
suspend fun requestGoogleIdToken(context: Context): GoogleCredentialResult {
    val activity = context.findActivity()
        ?: return GoogleCredentialResult.Failure("Không thể mở trình chọn tài khoản Google.")
    val webClientId = context.webClientId()
        ?: return GoogleCredentialResult.Failure("Google Sign-In chưa được cấu hình cho ứng dụng này.")

    return try {
        val result = CredentialManager.create(activity).getCredential(
            context = activity,
            request = GetCredentialRequest.Builder()
                .addCredentialOption(
                    GetSignInWithGoogleOption.Builder(serverClientId = webClientId).build(),
                )
                .build(),
        )
        GoogleCredentialResult.Success(
            GoogleIdTokenCredential.createFrom(result.credential.data).idToken,
        )
    } catch (_: GetCredentialCancellationException) {
        GoogleCredentialResult.Cancelled
    } catch (_: Exception) {
        GoogleCredentialResult.Failure("Không thể đăng nhập bằng Google. Vui lòng thử lại.")
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.webClientId(): String? {
    val resourceId = resources.getIdentifier("default_web_client_id", "string", packageName)
    return resourceId.takeIf { it != 0 }
        ?.let(::getString)
        ?.takeIf(String::isNotBlank)
}
