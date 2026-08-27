package com.tranhienchuong.nomad.core.designsystem

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SummaryCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(text = "Summary")
    }
}
