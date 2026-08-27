package com.tranhienchuong.nomad.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.tranhienchuong.nomad.core.datastore.NomadPreferencesRepository
import com.tranhienchuong.nomad.core.datastore.nomadDataStore
import com.tranhienchuong.nomad.core.designsystem.NomadBrandGradient
import com.tranhienchuong.nomad.core.designsystem.NomadGradientBadge
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run animation concurrently with startup checks
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600),
        )
    }

    LaunchedEffect(Unit) {
        delay(1200) // Ensure splash brand presentation (1.2s)

        val isAuthenticated = try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance().currentUser != null
            } else false
        } catch (_: Exception) {
            false
        }

        if (isAuthenticated) {
            onNavigateToMain()
            return@LaunchedEffect
        }

        val repo = NomadPreferencesRepository(context.nomadDataStore)
        val isOnboardingCompleted = try {
            repo.isOnboardingCompleted.first()
        } catch (_: Exception) {
            false
        }

        if (isOnboardingCompleted) {
            onNavigateToAuth()
        } else {
            onNavigateToOnboarding()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value),
        ) {
            NomadGradientBadge(
                icon = Icons.Outlined.AccountBalanceWallet,
                gradientColors = NomadBrandGradient,
                size = 96.dp,
                iconSize = 48.dp,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nomad",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Personal Finance",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
