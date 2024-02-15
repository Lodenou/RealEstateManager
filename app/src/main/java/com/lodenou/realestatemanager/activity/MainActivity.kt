package com.lodenou.realestatemanager.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.ui.theme.RealEstateManagerTheme
import com.lodenou.realestatemanager.ui.viewmodel.MainActivityViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import com.lodenou.realestatemanager.ui.theme.Purple40
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainActivityViewModel by viewModels()//todo use factory to inject repository
//        //todo https://github.com/Lodenou/WorkLifeSimulator/blob/master/app/src/main/java/com/lodenou/worklifesimulator/activities/newgame/ViewModelNewGame.kt
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
                                .width(260.dp) // Spécifiez une largeur fixe pour le tiroir
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
                            val realEstates by viewModel.realEstates.observeAsState(initial = emptyList())
                            RealEstateList(realEstates)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun RealEstateList(realEstates: List<RealEstate>) {
        LazyColumn {
            items(realEstates) { realEstate ->
                RealEstateItem(realEstate)
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
            title = { Text("Ma Toolbar") },
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
            CustomDialog(onDismiss = { showDialog = false })
        }
    }

    @Composable
    fun CustomDialog(onDismiss: () -> Unit) {
        //TODO LINK WITH VM and DB
        var type by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var area by remember { mutableStateOf("") }
        var numberOfRooms by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var pointsOfInterest by remember { mutableStateOf("") }
        var status by remember { mutableStateOf("") }
        var marketEntryDate by remember { mutableStateOf("") }
        var saleDate by remember { mutableStateOf("") }
        var realEstateAgent by remember { mutableStateOf("") }


        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "Détails du bien immobilier") },
            text = {
                Column {
                    TextField(value = type, onValueChange = { type = it }, label = { Text("Type") })
                    TextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Prix") })
                    TextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Surface") })
                    TextField(
                        value = numberOfRooms,
                        onValueChange = { numberOfRooms = it },
                        label = { Text("Nombre de pièces") })
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") })
                    TextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Adresse") })
                    TextField(
                        value = pointsOfInterest,
                        onValueChange = { pointsOfInterest = it },
                        label = { Text("Points d'intérêt") })
                    TextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Statut") })
                    TextField(
                        value = marketEntryDate,
                        onValueChange = { marketEntryDate = it },
                        label = { Text("Date de mise sur le marché") })
                    TextField(
                        value = saleDate,
                        onValueChange = { saleDate = it },
                        label = { Text("Date de vente") })
                    TextField(
                        value = realEstateAgent,
                        onValueChange = { realEstateAgent = it },
                        label = { Text("Agent immobilier") })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            },
            confirmButton = {
                Button(onClick = { /* Traitement de la saisie */ onDismiss() }) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Annuler")
                }
            }
        )
    }
}



