package com.tranhienchuong.nomad.feature.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tranhienchuong.nomad.core.designsystem.NomadBadgeGradient1
import com.tranhienchuong.nomad.core.designsystem.NomadBadgeGradient2
import com.tranhienchuong.nomad.core.designsystem.NomadBadgeGradient3
import com.tranhienchuong.nomad.core.designsystem.NomadGradientBadge
import com.tranhienchuong.nomad.core.designsystem.NomadPrimaryButton
import kotlinx.coroutines.launch

private data class OnboardingPageData(
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val title: String,
    val description: String,
)

private val OnboardingPages = listOf(
    OnboardingPageData(
        icon = Icons.Outlined.AccountBalanceWallet,
        gradientColors = NomadBadgeGradient1,
        title = "Quản lý Thu Chi Thông minh",
        description = "Ghi chép mọi khoản thu chi hàng ngày dễ dàng, phân loại danh mục tự động và kiểm soát tài chính tức thì.",
    ),
    OnboardingPageData(
        icon = Icons.Outlined.Savings,
        gradientColors = NomadBadgeGradient2,
        title = "Lập Kế hoạch & Tiết kiệm",
        description = "Thiết lập hạn mức ngân sách từng khoản, chủ động ngăn ngừa bội chi và hiện thực hóa mục tiêu tiết kiệm.",
    ),
    OnboardingPageData(
        icon = Icons.Outlined.Analytics,
        gradientColors = NomadBadgeGradient3,
        title = "Báo cáo & Phân tích Trực quan",
        description = "Thấu hiểu dòng tiền và thói quen tiêu dùng thông qua hệ thống biểu đồ chi tiết, giúp bạn tự do tài chính.",
    ),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { OnboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.NavigateToAuth -> onFinished()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar with Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (pagerState.currentPage < OnboardingPages.size - 1) {
                    TextButton(onClick = viewModel::finishOnboarding) {
                        Text(
                            text = "Bỏ qua",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                } else {
                    // Placeholder to preserve top spacing
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { pageIndex ->
                val page = OnboardingPages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    NomadGradientBadge(
                        icon = page.icon,
                        gradientColors = page.gradientColors,
                        size = 130.dp,
                        iconSize = 64.dp,
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 24.sp,
                        ),
                    )
                }
            }

            // Bottom section: Indicator + Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Page Indicator dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp),
                ) {
                    repeat(OnboardingPages.size) { iteration ->
                        val isSelected = pagerState.currentPage == iteration
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            label = "indicator_width",
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            },
                            label = "indicator_color",
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color),
                        )
                    }
                }

                val isLastPage = pagerState.currentPage == OnboardingPages.size - 1
                NomadPrimaryButton(
                    text = if (isLastPage) "Bắt đầu ngay" else "Tiếp tục",
                    onClick = {
                        if (isLastPage) {
                            viewModel.finishOnboarding()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                )
            }
        }
    }
}
