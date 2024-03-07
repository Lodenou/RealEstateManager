package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.data.model.SearchCriteria
import com.lodenou.realestatemanager.ui.viewmodel.RealEstateViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarRealEstate(onMenuClick: () -> Unit, realEstateViewModel: RealEstateViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("Real Estate Manager") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { showSearchDialog = true }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Recherche")
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )

    if (showDialog) {
        // clear image
        realEstateViewModel.imagesWithDescriptions.clear()
        CustomAlertDialog(onDismiss = { showDialog = false }, realEstateViewModel)
    }
    if (showSearchDialog) {
        SearchCriteriaDialog(
            onDismiss = { showSearchDialog = false },
            onSearch = { criteria ->
                // Gérer la recherche ici
                // Par exemple, afficher les critères de recherche dans la console
                println("Critères de recherche: $criteria")
                // Ou appeler une fonction de votre ViewModel pour effectuer la recherche
            }
        )
    }
}


@Composable
fun SearchCriteriaDialog(onDismiss: () -> Unit, onSearch: (SearchCriteria) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recherche multi-critères") },
        text = {
            SearchCriteriaForm(onSearchCriteriaChanged = { criteria ->
                onSearch(criteria)
                onDismiss() // Utilisez onDismiss pour fermer le dialogue
            })
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { // Utilisez onDismiss pour fermer le dialogue
                Text("Annuler")
            }
        }
    )
}

@Composable
fun SearchCriteriaForm(onSearchCriteriaChanged: (SearchCriteria) -> Unit) {
    // Initialisation des états pour les différents critères
    var areaRange by remember { mutableStateOf(0f..300f) }
    var priceRange by remember { mutableStateOf(0f..2000000f) }
    var nearSchool by remember { mutableStateOf(false) }
    var nearShops by remember { mutableStateOf(false) }
    var onMarketSince by remember { mutableStateOf(LocalDate.now()) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Exemples de composants pour les critères
        Text("Surface (m²): ${areaRange.start.toInt()} - ${areaRange.endInclusive.toInt()}")
        RangeSlider(
            value = areaRange,
            onValueChange = { areaRange = it },
            valueRange = 0f..500f,
            steps = 0
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text("Prix: \$${priceRange.start.toInt()} - \$${priceRange.endInclusive.toInt()}")

            RangeSlider(
                value = priceRange,
                onValueChange = { newRange ->
                    priceRange = newRange.start.coerceIn(0f..2000000f)..newRange.endInclusive.coerceIn(0f..20000000f)
                },
                valueRange = 0f..2000000f,
                steps = 10000
            )
        }
        // Exemple d'utilisation d'un Slider pour le prix, à adapter
        // Vous pouvez utiliser deux sliders distincts pour le minimum et le maximum ou un custom slider pour les gammes de prix

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = nearSchool,
                onCheckedChange = { nearSchool = it }
            )
            Text("Proche d'une école")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = nearShops,
                onCheckedChange = { nearShops = it }
            )
            Text("Proche des commerces")
        }

        // Exemple de DatePicker pour "mis sur le marché depuis", à implémenter

        Button(onClick = {
            // Construisez votre objet SearchCriteria ici et appelez onSearchCriteriaChanged
            onSearchCriteriaChanged(SearchCriteria(
                areaRange = areaRange,
                priceRange = priceRange,
                nearSchool = nearSchool,
                nearShops = nearShops,
                onMarketSince = onMarketSince
            ))
        }) {
            Text("Rechercher")
        }
    }
}