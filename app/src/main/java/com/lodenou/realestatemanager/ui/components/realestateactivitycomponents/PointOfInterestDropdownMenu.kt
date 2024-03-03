package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PointsOfInterestDropdownMenu(
    pointsOfInterest: List<String>,
    selectedPointsOfInterest: List<String>,
    onPointOfInterestSelected: (String, Boolean) -> Unit,
    shape: RoundedCornerShape
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Button(
            onClick = { expanded = true },
            shape = shape, // Appliquer la forme arrondie spécifiée à ce bouton
            // Vous pouvez ajuster le style du bouton selon vos besoins ici
        ) {
            Text("Points d'intérêt")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            pointsOfInterest.forEach { point ->
                val isSelected = point in selectedPointsOfInterest
                DropdownMenuItem(
                    onClick = {
                        // Inverser la sélection de ce point d'intérêt sans fermer le menu
                        onPointOfInterestSelected(point, !isSelected)
                        // Note: Nous avons retiré `expanded = false` ici pour garder le menu ouvert
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null, // Le changement est géré par onClick, donc pas besoin d'implémenter onCheckedChange ici
                                modifier = Modifier.padding(all = 8.dp) // Ajout d'un padding pour le Checkbox pour améliorer l'ergonomie
                            )
                            Text(text = point)
                        }
                    }
                )
            }
        }
    }
}