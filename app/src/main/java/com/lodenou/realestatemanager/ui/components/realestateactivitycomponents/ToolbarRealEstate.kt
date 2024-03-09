package com.lodenou.realestatemanager.ui.components.realestateactivitycomponents

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun SearchCriteriaForm(onSearchCriteriaChanged: (SearchCriteria) -> Unit, searchViewModel : SearchViewModel) {
    // Initialisation des états pour les différents critères



//    var areaRange by remember { mutableStateOf(0f..300f) }
//    var priceRange by remember { mutableStateOf(0f..2000000f) }
//    var nearLocation by remember { mutableStateOf(false) }
//    var nearShops by remember { mutableStateOf(false) }
//    var onMarketSince by remember { mutableStateOf(LocalDate.now()) }

    var areaRange by remember {
        mutableStateOf(
            searchViewModel.minarea.doubleValue.toFloat() ?: 0f..searchViewModel.maxarea.doubleValue.toFloat() ?: 500f
        )
    }
    var priceRange by remember {
        mutableStateOf(
            searchViewModel.minprice.doubleValue.toFloat() ?: 0f..searchViewModel.maxprice.doubleValue.toFloat() ?: 2000000f
        )
    }


    RangeSlider(
        value = areaRange,
        onValueChange = { newValue ->
            areaRange = newValue
            searchViewModel.minarea = newValue.start.toDouble()
            searchViewModel.maxarea = newValue.endInclusive.toDouble()
        },
        valueRange = 0f..500f
    )

    Spacer(Modifier.height(8.dp))

    RangeSlider(
        value = priceRange,
        onValueChange = { newValue ->
            priceRange = newValue
            searchViewModel.minprice = newValue.start.toDouble()
            searchViewModel.maxprice = newValue.endInclusive.toDouble()
        },
        valueRange = 0f..2000000f
    )

//    var selectedOption by remember { mutableStateOf(DateOption.WEEK) }

//    var startDate by remember { mutableStateOf("") }
//    var endDate by remember { mutableStateOf("") }
//    var showError by remember { mutableStateOf(false) }
//
//    val categories = listOf("Parc", "Musée", "Cinéma", "Restaurant", "École", "Commerces")
//    val categoryStates = remember { mutableStateMapOf<String, Boolean>().apply { categories.forEach { put(it, false) } } }


//    Column(modifier = Modifier
//        .padding(16.dp)
//        .verticalScroll(rememberScrollState())) {
//        // Exemples de composants pour les critères
//        Text("Surface (m²): ${areaRange.start.toInt()} - ${areaRange.endInclusive.toInt()}")
//        RangeSlider(
//            value = areaRange,
//            onValueChange = { areaRange = it },
//            valueRange = 0f..500f,
//            steps = 0
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Column {
//            Text("Prix: \$${priceRange.start.toInt()} - \$${priceRange.endInclusive.toInt()}")
//
//            RangeSlider(
//                value = priceRange,
//                onValueChange = { newRange ->
//                    priceRange = newRange.start.coerceIn(0f..2000000f)..newRange.endInclusive.coerceIn(0f..20000000f)
//                },
//                valueRange = 0f..2000000f,
//                steps = 10000
//            )
//        }



        Spacer(modifier = Modifier.height(8.dp))

//        categories.forEach { category ->
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Checkbox(
//                    checked = categoryStates[category] ?: false,
//                    onCheckedChange = { isChecked ->
//                        categoryStates[category] = isChecked
//                    }
//                )
//                Text(category)
//            }
//        }
//
//        DateInputField(
//            label = "Date de début",
//            value = startDate,
//            onValueChange = { startDate = it }
//        )
//
//        Spacer(Modifier.height(8.dp))
//
//        DateInputField(
//            label = "Date de fin",
//            value = endDate,
//            onValueChange = { endDate = it }
//        )
//
//        if (showError) {
//            Text("Format de date invalide. Veuillez utiliser le format JJ/MM/AAAA.", color = MaterialTheme.colorScheme.error)
//        }


        Button(onClick = {
            // Construisez votre objet SearchCriteria ici et appelez onSearchCriteriaChanged
            onSearchCriteriaChanged(SearchCriteria(
                areaRange = areaRange,
                priceRange = priceRange,
                nearSchool = nearLocation,
                nearShops = nearShops,
                onMarketSince = onMarketSince
            ))
        }) {
            Text("Rechercher")
        }
    }
}


@Composable
fun DateInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String = "Format invalide (JJ/MM/AAAA)"
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showError by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            showError = false
            onValueChange(newValue)
        },
        label = { Text(label) },
        isError = showError,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            showError = !isValidDate(value)
        }),
        trailingIcon = {
            if (showError) {
                Text(text = errorMessage)
            }
        }
    )
}

fun isValidDate(dateStr: String): Boolean {
    return try {
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        true
    } catch (e: DateTimeParseException) {
        false
    }
}