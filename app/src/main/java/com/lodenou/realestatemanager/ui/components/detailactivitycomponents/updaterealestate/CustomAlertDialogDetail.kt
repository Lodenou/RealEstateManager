package com.lodenou.realestatemanager.ui.components.detailactivitycomponents.updaterealestate

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.ui.components.realestateactivitycomponents.CheckboxWithLabel
import com.lodenou.realestatemanager.ui.components.realestateactivitycomponents.CustomDatePicker
import com.lodenou.realestatemanager.ui.components.realestateactivitycomponents.CustomDropdownMenu
import com.lodenou.realestatemanager.ui.viewmodel.DetailViewModel
import java.time.LocalDate

@Composable
fun CustomAlertDialogDetail(onDismiss: () -> Unit, realEstate: RealEstate, detailViewModel: DetailViewModel) {

    val context = LocalContext.current


    var type by remember { mutableStateOf(realEstate.type.toString()) }
    val types = listOf("Maison", "Appartement", "Loft")
    var price by remember { mutableStateOf(realEstate.price.toString()) }
    var area by remember { mutableStateOf(realEstate.area.toString()) }


    var numberOfRooms by remember { mutableStateOf(realEstate.numberOfRooms.toString()) }
    val rooms = listOf("1", "2", "3", "4", "5", "6")

    var description by remember { mutableStateOf(realEstate.description.toString()) }
    var address by remember { mutableStateOf(realEstate.address.toString()) }

    // point of interest
    var restaurant by remember { mutableStateOf(realEstate.restaurant) }
    var cinema by remember { mutableStateOf(realEstate.cinema) }
    var ecole by remember { mutableStateOf(realEstate.ecole) }
    var commerces by remember { mutableStateOf(realEstate.commerces) }


    var status by remember { mutableStateOf(realEstate.status.toString()) }
    val statuses = listOf("Disponible", "Vendu")

    var marketEntryDate by remember { mutableStateOf(realEstate.marketEntryDate) }
    var saleDate by remember { mutableStateOf<LocalDate?>(realEstate.saleDate) }

    var realEstateAgentName by remember { mutableStateOf(realEstate.realEstateAgent.toString()) }


    val isFormValid =
        type.isNotEmpty()  && price.isNotEmpty() && area.isNotEmpty() && numberOfRooms.isNotEmpty() &&
                description.isNotEmpty() && address.isNotEmpty() && status.isNotEmpty() && realEstateAgentName.isNotEmpty()

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Détails du bien immobilier") },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(6.dp)
            ) {


                ImagePickerWithDescriptionDetail(viewModel = detailViewModel)

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

                CheckboxWithLabel(label = "Restaurant", checked = restaurant, onCheckedChange = { restaurant = it })
                CheckboxWithLabel(label = "Cinéma", checked = cinema, onCheckedChange = { cinema = it })
                CheckboxWithLabel(label = "École", checked = ecole, onCheckedChange = { ecole = it })
                CheckboxWithLabel(label = "Commerces", checked = commerces, onCheckedChange = { commerces = it })

                CustomDropdownMenu(
                    options = statuses,
                    selectedOption = status,
                    onOptionSelected = { selected -> status = selected },
                    label = "Statut"
                )

                CustomDatePicker(
                    value = marketEntryDate,
                    onValueChange = { marketEntryDate = it!! },
                    label = "Date de mise sur le marché",
                    defaultText = ""
                )

                CustomDatePicker(
                    value = saleDate,
                    onValueChange = { newDate ->
                        saleDate =
                            newDate
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
                    val realEstateUpdated = RealEstate(
                        // create random id to avoid pb linked to auto-generated room id or document id from firestore
                        id = realEstate.id,
                        type = type,
                        price = price.toDoubleOrNull(),
                        area = area.toDoubleOrNull(),
                        numberOfRooms = numberOfRooms.toIntOrNull(),
                        description = description,
                        images = detailViewModel.imagesWithDescriptionsDetail, // vm list used here
                        address = address,
                        restaurant = restaurant,
                        cinema = cinema,
                        ecole = ecole,
                        commerces = commerces,
                        status = status,
                        marketEntryDate = marketEntryDate,
                        saleDate = saleDate,
                        realEstateAgent = realEstateAgentName,

                        )



                    // Save Object to room
                    detailViewModel.update(realEstateUpdated)

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

