package com.lodenou.realestatemanager.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import android.Manifest
import android.app.Activity
import android.os.Environment
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TextField
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.lodenou.realestatemanager.R
import com.lodenou.realestatemanager.Utils.toFirestoreTimestamp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

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


                            //TODO IF INTERNET METHODE GETALLREALESTATE DE FIRESTORE SINON UTILISER  .allRealEstates DE ROOM
//                            val realEstatesRoom by viewModel.allRealEstates.observeAsState(initial = emptyList()) // room
//                            RealEstateListScreen(realEstates = realEstatesRoom)

//                            val realEstates by viewModel.realEstatesFromFirestore.observeAsState(initial = emptyList())
                            val realEstates by viewModel.realEstatesFromFirestore.observeAsState(
                                emptyList()
                            )
                            RealEstateListScreen(realEstates = realEstates)
                        }
                    }
                }
            }
        }
    }

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
    fun RealEstateItem(realEstate: RealEstate, context: Context = LocalContext.current) {
        Column(modifier = Modifier
                .padding(16.dp)
            .clickable { // Intent pour lancer DetailActivity
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("realEstateId", realEstate.id)
                }
                context.startActivity(intent) }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!realEstate.images.isNullOrEmpty()) {
                    // Displaying the image with Coil
                    Image(
                        painter = rememberImagePainter(
                            data = realEstate.images.first().imageUrl,
                            builder = {
                                error(R.drawable.ic_launcher_foreground) // Your error image
                                placeholder(R.drawable.ic_launcher_background) // Loading image
                            }
                        ),
                        contentDescription = "Real Estate Image",
                        modifier = Modifier
                            .size(100.dp) // Adjust the image size according to your needs
                            .clip(RoundedCornerShape(8.dp)) // Rounds the corners of the image
                    )
                } else {
                    // Using Icon to display an icon when no image is available
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "No image",
                        modifier = Modifier.size(100.dp) // Ensure the icon size matches the images for uniform layout
                    )
                }
                Spacer(modifier = Modifier.width(8.dp)) // Space between the image/icon and text

                // Real estate details to the right of the image
                Column {
                    Text(text = "Type: ${realEstate.type}")
                    Text(text = "Price: ${realEstate.price}")
                    Text(text = "Address: ${realEstate.address}")
                }
            }
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

        val context = LocalContext.current
        val realEstateViewModel: RealEstateViewModel by viewModels()

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

        var realEstateAgentName by remember { mutableStateOf("") }


        val isFormValid =
            type.isNotEmpty() && price.isNotEmpty() && area.isNotEmpty() && numberOfRooms.isNotEmpty() &&
                    description.isNotEmpty() && address.isNotEmpty() && status.isNotEmpty() &&
                    marketEntryDate != null && realEstateAgentName.isNotEmpty()

        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Détails du bien immobilier") },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(6.dp) // Ajoutez du padding selon vos préférences
                ) {


                    ImagePickerWithDescription(viewModel = realEstateViewModel)

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
                            saleDate =
                                newDate // Mise à jour de saleDate avec la nouvelle date ou null pour réinitialiser
                        },
                        label = "Date de vente",
                        defaultText = "Pas encore vendu"
                    )

                    OutlinedTextField(
                        value = realEstateAgentName,
                        onValueChange = { realEstateAgentName = it },
                        label = { Text("Agent immobilier") },
                        shape = RoundedCornerShape(30.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (isFormValid) {
                        val realEstate = RealEstate(

                            type = type,
                            price = price.toDoubleOrNull(),
                            area = area.toDoubleOrNull(),
                            numberOfRooms = numberOfRooms.toIntOrNull(),
                            description = description,
                            images = realEstateViewModel.imagesWithDescriptions, // vm list used here
                            address = address,
                            pointsOfInterest = pointsOfInterest,
                            status = status,
                            marketEntryDate = marketEntryDate,
                            saleDate = saleDate,
                            realEstateAgent = realEstateAgentName
                        )

                        //TODO SAVE TO ROOM

                        // Save Object to Firestore
                        realEstateViewModel.saveRealEstateWithImages(realEstate)

                        onDismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Veuillez remplir tous les champs requis.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
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
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        label: String,
        defaultText: String
    ) {
        val open = remember { mutableStateOf(false) }

        if (open.value) {
            val initialDate =
                value ?: LocalDate.now()
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

        val displayText = value?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: defaultText
        OutlinedTextField(
            modifier = Modifier.clickable { open.value = true },
            enabled = false,
            value = displayText,
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

    // Image part

    @Composable
    fun ImagePickerWithDescription(viewModel: RealEstateViewModel) {
        val context = LocalContext.current
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        var showDescriptionDialog by remember { mutableStateOf(false) }
        var imageDescription by remember { mutableStateOf("") }
        var showSourceDialog by remember { mutableStateOf(false) }

        // Création d'une Uri pour stocker l'image prise par la caméra
        val photoUri = remember {
            mutableStateOf<Uri?>(null)
        }

        // Préparer le launcher pour prendre une photo
        val takePictureLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    photoUri.value?.let { uri ->
                        imageUri = uri
                        showDescriptionDialog = true
                    }
                }
            }

        // Préparer le launcher pour sélectionner une image
        val pickImageLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    imageUri = it
                    showDescriptionDialog = true
                }
            }

        // Demander la permission d'accès à la caméra
        val requestPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // Créer une Uri temporaire pour l'image
                    photoUri.value = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        createImageFile(context)
                    )
                    takePictureLauncher.launch(photoUri.value)
                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

        // Sélectionner la source de l'image
        fun showImageSourceDialog() {
            showSourceDialog = true
        }

        if (showSourceDialog) {
            AlertDialog(
                onDismissRequest = { showSourceDialog = false },
                title = { Text("Sélectionner la source de l'image") },
                text = {
                    Column {
                        CustomButton(
                            text = "Prendre une photo",
                            onClick = {
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                showSourceDialog = false
                            }
                        )
                        CustomButton(
                            text = "Choisir depuis la galerie",
                            onClick = {
                                pickImageLauncher.launch("image/*")
                                showSourceDialog = false
                            }
                        )
                    }
                },
                confirmButton = {}
            )
        }

        CustomButton(
            text = "choisir une image",
            onClick = { showImageSourceDialog() }
        )

        // Dialog pour entrer la description de l'image
        if (showDescriptionDialog) {
            DescriptionDialog(imageUri, imageDescription) { desc ->
                imageDescription = desc
                if (imageUri != null && imageDescription.isNotEmpty()) {
                    viewModel.addImageWithDescription(imageUri!!, imageDescription)
                    imageUri = null
                    imageDescription = ""
                }
                showDescriptionDialog = false
            }
        }
        DisplaySelectedImages(viewModel)
    }

    @Composable
    fun CustomButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(contentColor = Color.Black)
        ) {
            Text(text, color = Color.Black)
        }
    }

    @Composable
    fun DescriptionDialog(
        imageUri: Uri?,
        initialDescription: String,
        onConfirm: (String) -> Unit
    ) {
        var description by rememberSaveable { mutableStateOf(initialDescription) }

        if (imageUri != null) {
            AlertDialog(
                onDismissRequest = {
                },
                title = { Text("Description de l'image") },
                text = {
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Entrez une description") }
                    )
                },
                confirmButton = {
                    Button(onClick = { onConfirm(description) }) {
                        Text("Confirmer")
                    }
                }
            )
        }
    }

    @Composable
    fun DisplaySelectedImages(viewModel: RealEstateViewModel) {
        Column(
        ) {
            viewModel.imagesWithDescriptions.forEach { imageWithDescription ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberImagePainter(imageWithDescription.imageUrl),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(imageWithDescription.description)
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.removeImageWithDescription(imageWithDescription) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }

    private fun createImageFile(context: Context): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {

        }
    }
}



