package com.tranhienchuong.nomad.feature.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tranhienchuong.nomad.core.designsystem.NomadBrandGradient
import com.tranhienchuong.nomad.core.designsystem.NomadGradientBadge
import com.tranhienchuong.nomad.core.designsystem.NomadOutlinedButton
import com.tranhienchuong.nomad.core.designsystem.NomadPrimaryButton
import com.tranhienchuong.nomad.core.designsystem.NomadTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Handle Snackbar messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = "Đóng",
                duration = SnackbarDuration.Short,
            )
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.forgotPasswordSuccessMessage) {
        uiState.forgotPasswordSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Long,
            )
            viewModel.clearForgotPasswordSuccessMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Branding Header
            NomadGradientBadge(
                icon = Icons.Outlined.AccountBalanceWallet,
                gradientColors = NomadBrandGradient,
                size = 72.dp,
                iconSize = 36.dp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nomad",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Quản lý tài chính cá nhân thông minh",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp),
            )

            // PrimaryTabRow: Đăng nhập / Đăng ký
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth(),
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                },
            ) {
                Tab(
                    selected = uiState.selectedTab == AuthTab.SignIn,
                    onClick = { viewModel.selectTab(AuthTab.SignIn) },
                    text = {
                        Text(
                            text = "Đăng nhập",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (uiState.selectedTab == AuthTab.SignIn) FontWeight.Bold else FontWeight.Normal,
                            ),
                        )
                    },
                )
                Tab(
                    selected = uiState.selectedTab == AuthTab.SignUp,
                    onClick = { viewModel.selectTab(AuthTab.SignUp) },
                    text = {
                        Text(
                            text = "Đăng ký",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (uiState.selectedTab == AuthTab.SignUp) FontWeight.Bold else FontWeight.Normal,
                            ),
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Tab Content
            AnimatedContent(
                targetState = uiState.selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "auth_tab_content",
            ) { tab ->
                when (tab) {
                    AuthTab.SignIn -> {
                        SignInForm(
                            uiState = uiState,
                            viewModel = viewModel,
                            onSignInSuccess = onAuthSuccess,
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.signIn(onSuccess = onAuthSuccess)
                            },
                        )
                    }
                    AuthTab.SignUp -> {
                        SignUpForm(
                            uiState = uiState,
                            viewModel = viewModel,
                            onSignUpSuccess = onAuthSuccess,
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.signUp(onSuccess = onAuthSuccess)
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Social Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                )
                Text(
                    text = "Hoặc tiếp tục với",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Sign In Button
            NomadOutlinedButton(
                text = "Tiếp tục với Google",
                onClick = {
                    // Google Sign In triggered (simulate or Credential Manager)
                    onAuthSuccess()
                },
                enabled = !uiState.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ModalBottomSheet for Forgot Password
    if (uiState.isForgotPasswordSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setForgotPasswordSheetOpen(false) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Khôi phục Mật khẩu",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nhập email tài khoản của bạn. Nomad sẽ gửi liên kết hướng dẫn đặt lại mật khẩu mới.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(20.dp))

                NomadTextField(
                    value = uiState.forgotPasswordEmail,
                    onValueChange = viewModel::onForgotPasswordEmailChanged,
                    label = "Email khôi phục",
                    placeholder = "name@example.com",
                    leadingIcon = Icons.Outlined.Email,
                    errorMessage = uiState.forgotPasswordEmailError,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.sendPasswordReset()
                        }
                    ),
                )

                Spacer(modifier = Modifier.height(24.dp))

                NomadPrimaryButton(
                    text = "Gửi liên kết",
                    onClick = viewModel::sendPasswordReset,
                    isLoading = uiState.isLoading,
                )
            }
        }
    }
}

@Composable
private fun SignInForm(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    onSignInSuccess: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NomadTextField(
            value = uiState.signInEmail,
            onValueChange = viewModel::onSignInEmailChanged,
            label = "Email",
            placeholder = "name@example.com",
            leadingIcon = Icons.Outlined.Email,
            errorMessage = uiState.signInEmailError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
        )

        Spacer(modifier = Modifier.height(14.dp))

        NomadTextField(
            value = uiState.signInPassword,
            onValueChange = viewModel::onSignInPasswordChanged,
            label = "Mật khẩu",
            placeholder = "••••••••",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            errorMessage = uiState.signInPasswordError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
        )

        // Forgot password link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = { viewModel.setForgotPasswordSheetOpen(true) },
                enabled = !uiState.isLoading,
            ) {
                Text(
                    text = "Quên mật khẩu?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        NomadPrimaryButton(
            text = "Đăng nhập",
            onClick = { viewModel.signIn(onSignInSuccess) },
            isLoading = uiState.isLoading,
        )
    }
}

@Composable
private fun SignUpForm(
    uiState: AuthUiState,
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NomadTextField(
            value = uiState.signUpFullName,
            onValueChange = viewModel::onSignUpFullNameChanged,
            label = "Họ và tên",
            placeholder = "Nguyễn Văn A",
            leadingIcon = Icons.Outlined.Person,
            errorMessage = uiState.signUpFullNameError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
        )

        Spacer(modifier = Modifier.height(14.dp))

        NomadTextField(
            value = uiState.signUpEmail,
            onValueChange = viewModel::onSignUpEmailChanged,
            label = "Email",
            placeholder = "name@example.com",
            leadingIcon = Icons.Outlined.Email,
            errorMessage = uiState.signUpEmailError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
        )

        Spacer(modifier = Modifier.height(14.dp))

        NomadTextField(
            value = uiState.signUpPassword,
            onValueChange = viewModel::onSignUpPasswordChanged,
            label = "Mật khẩu",
            placeholder = "Ít nhất 6 ký tự",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            errorMessage = uiState.signUpPasswordError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
        )

        Spacer(modifier = Modifier.height(14.dp))

        NomadTextField(
            value = uiState.signUpConfirmPassword,
            onValueChange = viewModel::onSignUpConfirmPasswordChanged,
            label = "Xác nhận mật khẩu",
            placeholder = "Nhập lại mật khẩu",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true,
            errorMessage = uiState.signUpConfirmPasswordError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
        )

        Spacer(modifier = Modifier.height(24.dp))

        NomadPrimaryButton(
            text = "Tạo tài khoản",
            onClick = { viewModel.signUp(onSignUpSuccess) },
            isLoading = uiState.isLoading,
        )
    }
}
