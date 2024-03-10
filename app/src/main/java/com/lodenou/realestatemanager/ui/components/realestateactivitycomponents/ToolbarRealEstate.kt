package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.data.model.SearchCriteria
import com.lodenou.realestatemanager.ui.viewmodel.RealEstateViewModel
import com.lodenou.realestatemanager.ui.viewmodel.SearchViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarRealEstate(onMenuClick: () -> Unit, realEstateViewModel: RealEstateViewModel, searchViewModel : SearchViewModel) {
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
            }, searchViewModel
        )
    }
}


@Composable
fun SearchCriteriaDialog(onDismiss: () -> Unit, onSearch: (SearchCriteria) -> Unit, searchViewModel : SearchViewModel) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recherche multi-critères") },
        text = {
            SearchCriteriaForm(onSearchCriteriaChanged = { criteria ->
                onSearch(criteria)
                onDismiss()
            },searchViewModel )
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
fun SearchCriteriaForm(onSearchCriteriaChanged: (SearchCriteria) -> Unit, searchViewModel: SearchViewModel) {
    // Scroll state pour la colonne scrollable
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .padding(16.dp)) {

        var areaMin by searchViewModel.minArea
        var areaMax by searchViewModel.maxArea
        var priceMin by searchViewModel.minPrice
        var priceMax by searchViewModel.maxPrice

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




        // Ajoutez ici d'autres éléments du formulaire si nécessaire...

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            // Construction de l'objet SearchCriteria avec les valeurs min et max directement.
            val searchCriteria = SearchCriteria(
                minArea = areaMin,
                maxArea = areaMax,
                minPrice = priceMin,
                maxPrice = priceMax,
//                 Assurez-vous d'inclure les autres champs nécessaires pour SearchCriteria
            )

            // Appel de la fonction avec les nouveaux critères de recherche.
//            onSearchCriteriaChanged(searchCriteria)
            searchViewModel.performSearch()
        }) {
            Text("Rechercher")
        }
    }
}



