package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.ui.viewmodel.RealEstateViewModel
import com.lodenou.realestatemanager.ui.viewmodel.SearchViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarRealEstate(onMenuClick: () -> Unit, realEstateViewModel: RealEstateViewModel, searchViewModel : SearchViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("Real Estate") },
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
             searchViewModel
        )
    }
}


@Composable
fun SearchCriteriaDialog(onDismiss: () -> Unit, searchViewModel : SearchViewModel) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recherche multi-critères") },
        text = {
            SearchCriteriaForm(searchViewModel , onDismiss)
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun SearchCriteriaForm( searchViewModel: SearchViewModel,  onDismiss: () -> Unit) {
    // Scroll state
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .padding(16.dp)) {

        var areaMin by searchViewModel.minArea
        var areaMax by searchViewModel.maxArea
        var priceMin by searchViewModel.minPrice
        var priceMax by searchViewModel.maxPrice

        var restaurant by searchViewModel.restaurant
        var cinema by searchViewModel.cinema
        var ecole by searchViewModel.ecole
        var commerces by searchViewModel.commerces

        var startDate by searchViewModel.startDate
        var endDate by searchViewModel.endDate

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = if (areaMin == 0) "" else areaMin.toString(),
                onValueChange = { newValue ->
                    areaMin = newValue.toIntOrNull() ?: 0
                },
                label = { Text("Surface min (m²)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = if (areaMax== 0) "" else areaMax.toString(),
                onValueChange = { newValue ->
                    areaMax = newValue.toIntOrNull() ?: 0
                },
                label = { Text("Surface max (m²)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = if (priceMin == 0) "" else priceMin.toString(),
                onValueChange = { newValue ->
                    priceMin = newValue.toIntOrNull() ?: 0
                },
                label = { Text("Prix min") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = if (priceMax == 0) "" else priceMax.toString(),
                onValueChange = { newValue ->
                    priceMax = newValue.toIntOrNull() ?: 0
                },
                label = { Text("Prix max") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        CheckboxWithLabel(label = "Restaurant", checked = restaurant) { restaurant = it }
        CheckboxWithLabel(label = "Cinéma", checked = cinema) { cinema = it }
        CheckboxWithLabel(label = "École", checked = ecole) { ecole = it }
        CheckboxWithLabel(label = "Commerces", checked = commerces) { commerces = it }

        Spacer(Modifier.height(16.dp))
        CustomDatePicker(
            value = startDate,
            onValueChange = { startDate = it },
            label = "Mise sur le marché entre : ",
            defaultText = "",
        )
        Spacer(Modifier.width(8.dp))
        CustomDatePicker(
            value = endDate,
            onValueChange = { endDate = it },
            label = " et :",
            defaultText = ""
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = searchViewModel.isSold.value ?: false,
                onCheckedChange = { newValue ->
                    searchViewModel.isSold.value = newValue
                }
            )
            Text("Afficher uniquement les biens vendus")
        }

        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = {
                onDismiss()
                searchViewModel.performSearch()
            }) {
                Text("Rechercher")
            }
            Button(onClick = {
                onDismiss()
                searchViewModel.resetSearch()
            }) {
                Text("Réinitialiser", textAlign = TextAlign.Center, maxLines = 1)
            }
        }

    }
}



