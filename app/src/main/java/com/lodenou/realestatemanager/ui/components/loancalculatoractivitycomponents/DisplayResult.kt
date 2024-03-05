package com.lodenou.realestatemanager.ui.components.loancalculatoractivitycomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DisplayResult(result: Float?) {
    Column {
        if (result != null) {
            Spacer(modifier = Modifier.height(26.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            )
            {
                Text(
                    text = "" + result + "€ de mensualités à payer", textAlign = TextAlign.Center,
                    modifier = Modifier.width(150.dp)
                )
            }
        }
    }
}