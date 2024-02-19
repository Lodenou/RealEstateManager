package com.lodenou.realestatemanager.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lodenou.realestatemanager.ui.viewmodel.RealEstateViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.ButtonDefaults.shape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.core.content.pm.ShortcutInfoCompat.Surface
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class RealEstateActivity : ComponentActivity() {

    // Hilt injects
    private val viewModel: RealEstateViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            RealEstateManagerTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val coroutineScope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerContent = {

                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .width(260.dp)
                                .fillMaxHeight()
                                .padding(16.dp)
                        ) {

                            TextButton(onClick = {

                                val intent = Intent(context, LoanCalculatorActivity::class.java)
                                context.startActivity(intent)
                            }) {
                                Text("Calculateur de prêt immobilier")
                            }
                        }
                    },
                    drawerState = drawerState
                ) {
                    Scaffold(
                        topBar = {
                            ToolbarRealEstate(onMenuClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            })
                        }
                    ) { padding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            color = MaterialTheme.colorScheme.background
                        ) {


                            val realEstates by viewModel.allRealEstates.observeAsState(initial = emptyList())
                            RealEstateListScreen(realEstates = realEstates)
                        }
                    }
                }
            }
        }
    }

//    private val realestateViewModel: RealEstateViewModel by viewModels {
//        RealEstateViewModelFactory((application as WordsApplication).repository)
//    }

    @Composable
    fun RealEstateListScreen(realEstates: List<RealEstate>?) {
        if (realEstates.isNullOrEmpty()) {
            Text(text = "Aucun bien à afficher", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
                items(realEstates) { realEstate ->
                    RealEstateItem(realEstate)
                }
            }
        }
    }


    @Composable
    fun RealEstateItem(realEstate: RealEstate) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Type: ${realEstate.type}")
            Text(text = "Prix: ${realEstate.price}")
            Text(text = "Image: ${realEstate.images?.get(0)}")
            Text(text = "Adresse: ${realEstate.address}")
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToolbarRealEstate(onMenuClick: () -> Unit) {
        var showDialog by remember { mutableStateOf(false) }

        TopAppBar(
            title = { Text("Real Estate Manager") },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Ajouter")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        )

        if (showDialog) {
            CustomAlertDialog(onDismiss = { showDialog = false })
        }
    }

    @Composable
    fun CustomAlertDialog(onDismiss: () -> Unit) {

        var type by remember { mutableStateOf("") }
        val types = listOf("Maison", "Appartement", "Loft")
        var price by remember { mutableStateOf("") }
        var area by remember { mutableStateOf("") }


        var numberOfRooms by remember { mutableStateOf("") }
        val rooms = listOf("1", "2", "3", "4", "5", "6")


        var description by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var pointsOfInterest by remember { mutableStateOf("") }
        var status by remember { mutableStateOf("") }

        val statuses = listOf("Disponible", "Vendu")

        var marketEntryDate by remember { mutableStateOf(LocalDate.now()) }
        var saleDate by remember { mutableStateOf<LocalDate?>(null) }

        var realEstateAgent by remember { mutableStateOf("") }
        // Pour gérer l'affichage des menus déroulants et date pickers


        val isFormValid =
            type.isNotEmpty() && price.isNotEmpty() && area.isNotEmpty() && numberOfRooms.isNotEmpty() &&
                    description.isNotEmpty() && address.isNotEmpty() && status.isNotEmpty() &&
                    marketEntryDate != null && realEstateAgent.isNotEmpty()

        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Détails du bien immobilier") },
            text = {
                // Initialisation de l'état de défilement
                val scrollState = rememberScrollState()

                // Application du défilement vertical
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(6.dp) // Ajoutez du padding selon vos préférences
                ) {
                    CustomDropdownMenu(
                        options = types,
                        selectedOption = type,
                        onOptionSelected = { selected -> type = selected },
                        label = "Type"
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { newValue ->
                            // Filter for digit only
                            if (newValue.all { it.isDigit() }) {
                                val newValueLong = newValue.toLongOrNull() ?: 0L
                                // max value allowed
                                if (newValueLong <= 999_999_999L) {
                                    price = newValue
                                }
                            }
                        },
                        label = { Text("Prix") },
                        // digit keyboard
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = { Text("$") },
                        shape = RoundedCornerShape(30.dp)
                    )

                    OutlinedTextField(
                        value = area,
                        onValueChange = { newValue ->
                            // Filter for digit only
                            if (newValue.all { it.isDigit() }) {
                                val newValueLong = newValue.toLongOrNull() ?: 0L
                                // max value allowed
                                if (newValueLong <= 9999L) {
                                    area = newValue
                                }
                            }
                        },
                        label = { Text("Surface") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = { Text("m²") },
                        shape = RoundedCornerShape(30.dp)
                    )
                    CustomDropdownMenu(
                        options = rooms,
                        selectedOption = numberOfRooms,
                        onOptionSelected = { selected -> numberOfRooms = selected },
                        label = "Nombre de pièces"
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        shape = RoundedCornerShape(30.dp)
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Adresse") },
                        shape = RoundedCornerShape(30.dp)
                    )
                    OutlinedTextField(
                        value = pointsOfInterest,
                        onValueChange = { pointsOfInterest = it },
                        label = { Text("Points d'intérêt") },
                        shape = RoundedCornerShape(30.dp)
                    )
                    CustomDropdownMenu(
                        options = statuses,
                        selectedOption = status,
                        onOptionSelected = { selected -> status = selected },
                        label = "Statut"
                    )

                    CustomDatePicker(
                        value = marketEntryDate,
                        onValueChange = { marketEntryDate = it },
                        label = "Date de mise sur le marché",
                        defaultText = ""
                    )

                    CustomDatePicker(
                        value = saleDate,
                        onValueChange = { newDate ->
                            saleDate = newDate // Mise à jour de saleDate avec la nouvelle date ou null pour réinitialiser
                        },
                        label = "Date de vente",
                        defaultText = "Pas encore vendu"
                    )

                    OutlinedTextField(
                        value = realEstateAgent,
                        onValueChange = { realEstateAgent = it },
                        label = { Text("Agent immobilier") },
                        shape = RoundedCornerShape(30.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Confirmer", color = Color.Black)
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Annuler", color = Color.Black)
                }
            }
        )
    }

    @Composable
    fun CustomDropdownMenu(
        options: List<String>,
        selectedOption: String,
        onOptionSelected: (String) -> Unit,
        label: String = "Select an option"
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expanded = true },
                colors = buttonColors(
                    containerColor = Color(0xFF48444f),
                    contentColor = Color.White
                )
            ) {
                Text(text = selectedOption.ifEmpty { label }, color = Color.White)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF48444f))
            ) {
                for (option in options) {
                    DropdownMenuItem(
                        { Text(text = option, color = Color.White) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        })
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CustomDatePicker(
        value: LocalDate?, // Accepte maintenant une valeur nullable
        onValueChange: (LocalDate?) -> Unit,
        label: String,
        defaultText: String
    ) {
        val open = remember { mutableStateOf(false) }

        if (open.value) {
            val initialDate = value ?: LocalDate.now() // Utilisez la valeur actuelle ou aujourd'hui si null
            CalendarDialog(
                state = rememberUseCaseState(
                    visible = true,
                    onCloseRequest = { open.value = false }),
                config = CalendarConfig(
                    yearSelection = true,
                    style = CalendarStyle.MONTH,
                ),
                selection = CalendarSelection.Date(
                    selectedDate = initialDate
                ) { newDate ->
                    onValueChange(newDate)
                },
            )
        }

        val displayText = value?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))  ?: defaultText
        OutlinedTextField(
            modifier = Modifier.clickable { open.value = true },
            enabled = false,
            value = displayText, // Affiche une chaîne vide si value est null
            onValueChange = {},
            label = { Text(label) },
            shape = RoundedCornerShape(30.dp),
            trailingIcon = {
                if (value != null) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.clickable { onValueChange(null) }
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }

}



