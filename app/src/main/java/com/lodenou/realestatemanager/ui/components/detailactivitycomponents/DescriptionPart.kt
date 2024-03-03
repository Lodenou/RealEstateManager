package com.lodenou.realestatemanager.ui.components.detailactivitycomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DescriptionPart(descriptionText: String?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.padding(0.dp, 8.dp))
        if (descriptionText != null) {
            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}