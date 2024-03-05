package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import android.app.Activity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoanTitleWithBackButton() {
    val activity = LocalContext.current as Activity
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),

        ) {
        IconButton(
            onClick = { activity.finish() },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color.Black
            )
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Retour",
            )

        }
        Text(
            text = "Simulateur de prêt",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 52.dp),
            textAlign = TextAlign.Start,
        )
    }
}