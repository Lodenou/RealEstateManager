package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.ui.activity.LoanCalculatorActivity

@Composable
fun DrawerContent(onNavigate: (Intent) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .width(260.dp)
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        TextButton(onClick = {
            onNavigate(Intent(context, LoanCalculatorActivity::class.java))
        }) {
            Text("Calculateur de prêt immobilier")
        }
    }
}