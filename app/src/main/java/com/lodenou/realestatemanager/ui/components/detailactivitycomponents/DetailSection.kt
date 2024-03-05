package com.lodenou.realestatemanager.ui.components.detailactivitycomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun DetailsSection(surface: Double?, numberOfRooms: Int?, location: String?) {
    Column(modifier = Modifier.padding(8.dp)) {
        InfoItemWithIcon(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Surface") },
            infoText = "Surface : ${surface}m²"
        )
        Spacer(modifier = Modifier.height(4.dp))
        InfoItemWithIcon(
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Chambres",
                )
            },
            infoText = "Nombre de chambres : $numberOfRooms"
        )
        Spacer(modifier = Modifier.height(4.dp))
        InfoItemWithIcon(
            icon = {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Localisation",
                )
            },
            infoText = "Localisation : $location"
        )
    }
}
@Composable
fun InfoItemWithIcon(icon: @Composable () -> Unit, infoText: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Text(
            text = infoText,
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}